package com.tyzsskills.impl.server.active;

import com.google.gson.*;
import com.tyzsskills.api.records.SkillPrefab;
import com.tyzsskills.impl.server.Level.LevelPoolPreset;
import com.tyzsskills.impl.server.model.*;
import com.tyzsskills.impl.server.skills.SkillLoader;
import com.tyzsskills.impl.server.xp.XpGainRegistry;
import com.tyzsskills.impl.server.xp.XpManager;
import com.tyzsskills.impl.server.xp.XpValuePresets;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

@ApiStatus.Internal
public class FileManager {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static final List<SkillPrefab> prefabQueue = new ArrayList<>();
    public static void registerPrefab(@NotNull SkillPrefab prefab){prefabQueue.add(prefab);}
    public static void clearPrefab(){prefabQueue.clear();}

    public static final String BLOCK_VALUES_KEY = "block-xp-values";
    public static final String ENTITY_VALUES_KEY = "entity-xp-values";
    public static final String FOOD_VALUES_KEY = "food-xp-values";
    public static final String LEVEL_POOL_KEY = "level-pool";


    //PATHS
    @NotNull
    private static Path getBasePath(@NotNull MinecraftServer server){
        return server.getServerDirectory().resolve("config").resolve("tyzs_skills");
    }
    @NotNull
    private static Path getDefaultSkillPath(@NotNull MinecraftServer server){
        return getBasePath(server).resolve("default").resolve("skills");
    }
    @NotNull
    private static Path getDefaultDataPath(@NotNull MinecraftServer server){
        return getBasePath(server).resolve("default").resolve("data");
    }
    @NotNull
    private static Path getCustomSkillPath(@NotNull MinecraftServer server){
        return getBasePath(server).resolve("custom").resolve("skills");
    }
    @NotNull
    private static Path getCustomDataPath(@NotNull MinecraftServer server){
        return getBasePath(server).resolve("custom").resolve("data");
    }

    //INIT FOR FILES/FOLDERS
    public static void init(@NotNull MinecraftServer server) throws IOException {
        clearDefaultPath(server);

        Files.createDirectories(getDefaultSkillPath(server));
        Files.createDirectories(getDefaultDataPath(server));

        Files.createDirectories(getCustomSkillPath(server));
        Files.createDirectories(getCustomDataPath(server));

        Migration.runMigration(server);
    }


    //WRITING
    public static void writeDefaultSkills(@NotNull MinecraftServer server) throws IOException {
        Path targetPath = getDefaultSkillPath(server);

        for(var prefab : prefabQueue){
            var skill = new Skill(prefab.active(), prefab.id(), prefab.maximumLevel(),
                    prefab.prices(), prefab.type(), prefab.category(),
                    prefab.icon(), prefab.displayName(), prefab.description(), prefab.modifiers(), prefab.customValues(),
                    prefab.config()
            );
            writeFile(skill , targetPath, skill.getID());
        }
    }

    public static void writeDefaultData(@NotNull MinecraftServer server) throws IOException {
        Path targetPath = getDefaultDataPath(server);

        writeFile(XpValuePresets.getBlockValuesPreset(), targetPath, BLOCK_VALUES_KEY);
        writeFile(XpValuePresets.getEntityValuesPreset(), targetPath, ENTITY_VALUES_KEY);
        writeFile(XpValuePresets.getFoodValuesPreset(), targetPath, FOOD_VALUES_KEY);

        writeFile(LevelPoolPreset.getLevelDataPreset(), targetPath, LEVEL_POOL_KEY);
    }

    //READING
    public static void readSkills(@NotNull MinecraftServer server) throws IOException {
        walkThroughSkills(getDefaultSkillPath(server), true);
        walkThroughSkills(getCustomSkillPath(server), false);

        SkillLoader.finalizePreLoading();
    }

    public static void readData(@NotNull MinecraftServer server) throws IOException{
        var defaultPath = getDefaultDataPath(server);
        var customPath = getCustomDataPath(server);

        var blockFile = Path.of(BLOCK_VALUES_KEY + ".json");
        readExclusiveMap(customPath.resolve(blockFile), defaultPath.resolve(blockFile), XpGainRegistry::loadBlockMap);

        var entityFile = Path.of(ENTITY_VALUES_KEY + ".json");
        readExclusiveMap(customPath.resolve(entityFile), defaultPath.resolve(entityFile), XpGainRegistry::loadEntityMap);

        var foodFile = Path.of(FOOD_VALUES_KEY + ".json");
        readExclusiveMap(customPath.resolve(foodFile), defaultPath.resolve(foodFile), XpGainRegistry::loadFoodMap);

        var levelFile = Path.of(LEVEL_POOL_KEY + ".json");
        readExclusiveMap(customPath.resolve(levelFile), defaultPath.resolve(levelFile), XpManager::loadPool);
    }



    //UTILS
    private static <T> void writeFile(@NotNull T obj, @NotNull Path path, @NotNull String fileName) throws IOException {
        if(!Files.exists(path) || obj instanceof String) return;
        Files.writeString(path.resolve(fileName + ".json"), gson.toJson(obj));
    }

    @Nullable
    private static JsonObject readFile(@NotNull Path path) throws IOException {
        return gson.fromJson(Files.readString(path), JsonObject.class);
    }

    private static void processFile(@NotNull Path file, @NotNull Consumer<JsonObject> action){
        try {
            var obj = readFile(file);
            if(obj != null) action.accept(obj);
        }
        catch (JsonSyntaxException ex) {
            ErrorManager.registerLoadError("parsing " + file.getFileName(), "JSON Syntax error");
        }
        catch (Exception ex){
            ErrorManager.registerLoadError("loading " + file.getFileName(), ex.getMessage());
        }
    }

    private static void walkThroughSkills(@NotNull Path path, boolean isDefault) throws IOException {
        if(!Files.exists(path)) return;

        try(var stream = Files.walk(path)){
            stream.filter(Files::isRegularFile).filter(p -> p.toString().endsWith(".json"))
                    .forEach(file -> processFile(file, (obj) ->
                            SkillLoader.preLoadSkill(obj, String.valueOf(path.getFileName()), isDefault)));
            }
    }

    private static void readExclusiveMap(@NotNull Path customFile, @NotNull Path defaultFile, @NotNull Consumer<JsonObject> action) throws IOException {
        var source = Files.exists(customFile) ? readFile(customFile) : readFile(defaultFile);
        if(source != null) action.accept(source);
    }

    private static void clearDefaultPath(@NotNull MinecraftServer server) throws IOException {
        Path[] defaultPaths = new Path[]{getDefaultDataPath(server), getDefaultSkillPath(server)};

        for (var path : defaultPaths){
            if (!Files.exists(path)) continue;

            try (Stream<Path> walk = Files.walk(path)) {
                walk.sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        }
    }

    private static class Migration{
        public static void runMigration(@NotNull MinecraftServer server){
            var basePath = getBasePath(server);
            var oldSkillsRoot = basePath.resolve("skills");
            var oldCustomSkills = oldSkillsRoot.resolve("custom");

            var newCustomSkills = getCustomSkillPath(server);
            var newCustomData = getCustomDataPath(server);

            if (Files.exists(oldCustomSkills)) {
                try (Stream<Path> stream = Files.walk(oldCustomSkills)) {
                    stream.filter(Files::isRegularFile).filter(p -> p.toString().endsWith(".json")).forEach(source -> {
                        try {
                            Files.move(source, newCustomSkills.resolve(source.getFileName()), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException ignored) {}
                    });
                } catch (IOException ignored) {}
            }

            if (Files.exists(oldSkillsRoot)) {
                try (Stream<Path> walk = Files.walk(oldSkillsRoot)) {
                    walk.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
                } catch (IOException ignored) {}
            }

            migrateData(basePath.resolve(BLOCK_VALUES_KEY + ".json"),
                    newCustomData.resolve(BLOCK_VALUES_KEY + ".json"),
                    gson.toJsonTree(XpValuePresets.getBlockValuesPreset()));

            migrateData(basePath.resolve(ENTITY_VALUES_KEY + ".json"),
                    newCustomData.resolve(ENTITY_VALUES_KEY + ".json"),
                    gson.toJsonTree(XpValuePresets.getEntityValuesPreset()));

            migrateData(basePath.resolve(FOOD_VALUES_KEY + ".json"),
                    newCustomData.resolve(FOOD_VALUES_KEY + ".json"),
                    gson.toJsonTree(XpValuePresets.getFoodValuesPreset()));

            migrateData(basePath.resolve(LEVEL_POOL_KEY + ".json"),
                    newCustomData.resolve(LEVEL_POOL_KEY + ".json"),
                    gson.toJsonTree(LevelPoolPreset.getLevelDataPreset()));
        }

        private static void migrateData(@NotNull Path oldFile, @NotNull Path newFile, @NotNull JsonElement defaultTree){
            if (!Files.exists(oldFile)) return;

            try {
                var oldJson = gson.fromJson(Files.readString(oldFile), JsonObject.class);

                if (oldJson != null) {
                    for (String catKey : oldJson.keySet()) {
                        if (oldJson.get(catKey).isJsonObject()) {
                            JsonObject category = oldJson.getAsJsonObject(catKey);

                            if (category.has("blocks")) category.add("id", category.remove("blocks"));
                            if (category.has("entities")) category.add("id", category.remove("entities"));
                            if (category.has("foods")) category.add("id", category.remove("foods"));
                        }
                    }

                    if (oldJson.equals(defaultTree)) {
                        Files.delete(oldFile);
                    } else {
                        Files.writeString(newFile, gson.toJson(oldJson));
                        Files.delete(oldFile);
                    }
                }
            } catch (Exception e) {
                try { Files.move(oldFile, newFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING); } catch (IOException ignored) {}
            }
        }
    }
}
