package com.tyzsskills.impl.server.active;

import java.io.File;
import java.io.IOException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.records.SkillPrefab;
import com.tyzsskills.impl.server.model.*;
import com.tyzsskills.impl.server.skills.SkillLoader;
import com.tyzsskills.impl.server.skills.SkillManager;
import com.tyzsskills.impl.server.xp.XpManager;
import com.tyzsskills.impl.server.xp.xpEvents.XpBlock;
import com.tyzsskills.impl.server.xp.xpEvents.XpEntity;
import com.tyzsskills.impl.server.xp.xpEvents.XpFood;
import net.minecraft.server.MinecraftServer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.nio.file.Files;
import java.util.stream.Stream;

@ApiStatus.Internal
public class FileManager {

    private static final FileManager instance = new FileManager();
    public static FileManager get(){return instance;}

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static final List<SkillPrefab> prefabQueue = new ArrayList<>();


    //Creer les dossiers
    public void initPath(MinecraftServer server){
        Path globalPath = server.getServerDirectory()
                .resolve("config")
                .resolve("tyzs_skills");

        List<Path> allPaths = new ArrayList<>();

        allPaths.add(globalPath.resolve("skills")
                .resolve("default")
                .resolve("abilities"));

        allPaths.add(globalPath.resolve("skills")
                .resolve("default")
                .resolve("fight"));

        allPaths.add(globalPath.resolve("skills")
                .resolve("default")
                .resolve("misc"));

        allPaths.add(globalPath.resolve("skills")
                .resolve("default")
                .resolve("traits"));

        allPaths.add(globalPath.resolve("skills")
                .resolve("custom"));


        for(var path : allPaths){
            try {Files.createDirectories(path);}
            catch(IOException ex) {throw new RuntimeException(ex);}
        }
    }

    //Ecrit les jsons par defaut
    public void loadDefaultJson(MinecraftServer server) throws IOException {
        for(var skill : SkillsPreset.getDefaultSkills()){
            writeSkill(skill, getSkillPath(skill.getCategory(), server));
        }

        for (var prefab : prefabQueue){
            Skill skillToSave;

            if(prefab.isTrait()){
                var firstPrice = prefab.prices().isEmpty()? 0 : prefab.prices().getFirst();
                skillToSave = new Trait(prefab.active(), prefab.id(), prefab.powerWeight(),
                        firstPrice, prefab.purchasable(), prefab.icon(), prefab.displayName(), prefab.description());
            }
            else{
                skillToSave = new Skill(prefab.active(), prefab.id(), prefab.maximumLevel(),
                        prefab.prices(), prefab.values(), prefab.type(), prefab.category(),
                        prefab.modifier(), prefab.operation(), prefab.purchasable(),
                        prefab.icon(), prefab.displayName(), prefab.description(), prefab.unit());
            }

            Path targetPath = getSkillPath(prefab.category(), server);
            writeSkill(skillToSave, targetPath);
        }
    }

    public void loadDefaultXpValues(MinecraftServer server) throws IOException {
        Path blockFile = server.getServerDirectory().resolve("config")
                .resolve("tyzs_skills")
                .resolve("block-xp-values.json");

        Path entityFile = server.getServerDirectory().resolve("config")
                .resolve("tyzs_skills")
                .resolve("entity-xp-values.json");

        Path foodFile = server.getServerDirectory().resolve("config")
                .resolve("tyzs_skills")
                .resolve("food-xp-values.json");


        if(!Files.exists(blockFile)) Files.writeString(blockFile, BlockXpValuesPreset.getDefaultXpValues());
        if(!Files.exists(entityFile)) Files.writeString(entityFile, EntityXpValuesPreset.getDefaultXpValues());
        if(!Files.exists(foodFile)) Files.writeString(foodFile, FoodValuesPreset.getDefaultXpValues());
    }

    public void LoadDefaultLevelPool(MinecraftServer server) throws IOException {
        Path poolFile = server.getServerDirectory().resolve("config")
                .resolve("tyzs_skills")
                .resolve("level-pool.json");

        if(!Files.exists(poolFile)) Files.writeString(poolFile, LevelPoolPreset.getDefaultRewardValues());
    }

    //Lit les jsons par defaut
    public void readJsons(MinecraftServer server) throws IOException{
        Path globalPath = server.getServerDirectory().resolve("config")
                .resolve("tyzs_skills")
                .resolve("skills");

        var defaultPath = globalPath.resolve("default");
        var customPath = globalPath.resolve("custom");


        if(Files.exists(defaultPath)){
            try(var stream = Files.walk(defaultPath)){
                stream.filter(Files::isRegularFile)
                        .filter(p -> p.toString().endsWith(".json"))
                        .forEach(path ->{
                            try{
                                var jsonString = Files.readString(path);
                                var jsonObj = gson.fromJson(jsonString, JsonObject.class);
                                SkillLoader.loadSkill(jsonObj);
                            }
                            catch (IOException ex){throw new RuntimeException(ex);}
                        });
            }
        }

        if(Files.exists(customPath)){
            try(var stream = Files.walk(customPath)){
                stream.filter(Files::isRegularFile)
                        .filter(p -> p.toString().endsWith(".json"))
                        .forEach(path ->{
                            try{
                                var jsonString = Files.readString(path);
                                var jsonObj = gson.fromJson(jsonString, JsonObject.class);

                                var type = jsonObj.has("type") ? jsonObj.get("type").getAsString() : null;
                                var id = jsonObj.has("id") ? jsonObj.get("id").getAsString().toLowerCase() : null;
                                if(type == null || id == null) return;

                                if(!type.equalsIgnoreCase(Enums.SkillType.CUSTOM.name())){
                                    if(!SkillManager.get().isSkillLoaded(id)) return;
                                }
                                SkillLoader.loadSkill(jsonObj);
                            }
                            catch (Exception ex){System.out.println("Unable to load custom skill: " + path);}
                        });
            }
        }

    }

    public void readXpValues(MinecraftServer server) throws IOException {
        Path blockFile = server.getServerDirectory().resolve("config")
                .resolve("tyzs_skills")
                .resolve("block-xp-values.json");

        Path entityFile = server.getServerDirectory().resolve("config")
                .resolve("tyzs_skills")
                .resolve("entity-xp-values.json");

        Path foodFile = server.getServerDirectory().resolve("config")
                .resolve("tyzs_skills")
                .resolve("food-xp-values.json");

        if(Files.exists(blockFile)){
            var content = Files.readString(blockFile);
            var obj = JsonParser.parseString(content).getAsJsonObject();
            XpBlock.loadValues(obj);
        }

        if (Files.exists(entityFile)) {
            var content = Files.readString(entityFile);
            var obj = JsonParser.parseString(content).getAsJsonObject();
            XpEntity.loadValues(obj);
        }

        if (Files.exists(foodFile)) {
            var content = Files.readString(foodFile);
            var obj = JsonParser.parseString(content).getAsJsonObject();
            XpFood.loadValues(obj);
        }
    }

    public void readLevelPool(MinecraftServer server) throws IOException {
        Path poolFile = server.getServerDirectory().resolve("config")
                .resolve("tyzs_skills")
                .resolve("level-pool.json");

        if(Files.exists((poolFile))){
            var content = Files.readString(poolFile);
            var obj = JsonParser.parseString(content).getAsJsonObject();
            XpManager.loadPool(obj);
        }
    }


    public static void registerSkillPrefab(SkillPrefab prefab){
        if(prefab != null) prefabQueue.add(prefab);
    }


    //Utilitaire
    private void writeSkill(Skill skill, Path path) throws IOException{

        Path skillFile = path.resolve(skill.getID() + ".json");
        String skillJson = gson.toJson(skill);
        Files.writeString(skillFile, skillJson);
    }

    private Path getSkillPath(Enums.CategoryType category, MinecraftServer server){

        Path skillPath = server.getServerDirectory()
                .resolve("config")
                .resolve("tyzs_skills")
                .resolve("skills");

        switch (category){
            case ABILITIES -> {
                return skillPath.resolve("default").resolve("abilities");
            }
            case FIGHT -> {
                return skillPath.resolve("default").resolve("fight");
            }
            case MISC, ALL, BOOKMARKS -> {
                return skillPath.resolve("default").resolve("misc");
            }
            case TRAITS -> {
                return skillPath.resolve("default").resolve("traits");
            }
        }
        return skillPath;
    }


    public void backupCustomFiles(MinecraftServer server) throws IOException {
        Path skillPath = server.getServerDirectory()
                .resolve("config")
                .resolve("tyzs_skills")
                .resolve("skills");

        var backupFolder = skillPath.resolve("skill_config_backup_v6.1");

        if(Files.exists(backupFolder)) return;

        var defaultFolder = skillPath.resolve("default");
        var traitsFolder = skillPath.resolve("traits");

        if(Files.exists(defaultFolder)) {
            Path targetDefaultBackup = backupFolder.resolve("default");
            try (Stream<Path> stream = Files.walk(defaultFolder)) {
                stream.forEach(source -> {
                    Path destination = targetDefaultBackup.resolve(defaultFolder.relativize(source));
                    try {
                        if (Files.isDirectory(source)) {
                            Files.createDirectories(destination);
                        } else {
                            Files.createDirectories(destination.getParent());
                            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("[Tyz's Skills] Unable to copy file: " + source, e);
                    }
                });
            }
        }

        if(Files.exists(traitsFolder)) {
            Path targetTraitsBackup = backupFolder.resolve("traits");
            try (Stream<Path> stream = Files.walk(traitsFolder)) {
                stream.forEach(source -> {
                    Path destination = targetTraitsBackup.resolve(traitsFolder.relativize(source));
                    try {
                        if (Files.isDirectory(source)) {
                            Files.createDirectories(destination);
                        } else {
                            Files.createDirectories(destination.getParent());
                            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("[Tyz's Skills] Unable to copy file: " + source, e);
                    }
                });

            }
        }

    }

    public void cleanPaths(MinecraftServer server) throws IOException {
        Path path = server.getServerDirectory()
                .resolve("config")
                .resolve("tyzs_skills")
                .resolve("skills")
                .resolve("default");

        if (!Files.exists(path)) return;

        try (Stream<Path> walk = Files.walk(path)) {
            walk.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }
}
