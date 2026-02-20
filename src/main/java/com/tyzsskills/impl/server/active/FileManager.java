package com.tyzsskills.impl.server.active;

import java.io.IOException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tyzsskills.impl.server.model.*;
import com.tyzsskills.impl.server.skills.SkillLoader;
import com.tyzsskills.impl.server.xp.XpManager;
import com.tyzsskills.impl.server.xp.xpEvents.XpBlock;
import com.tyzsskills.impl.server.xp.xpEvents.XpEntity;
import com.tyzsskills.impl.server.xp.xpEvents.XpFood;
import net.minecraft.server.MinecraftServer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;

@ApiStatus.Internal
public class FileManager {

    private static final FileManager instance = new FileManager();
    public static FileManager Get(){return instance;}

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    //Creer les dossiers
    public void InitPath(MinecraftServer server){
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
                .resolve("custom"));

        allPaths.add(globalPath.resolve("skills")
                .resolve("traits"));

        for(var path : allPaths){
            try {Files.createDirectories(path);}
            catch(IOException ex) {throw new RuntimeException(ex);}
        }
    }

    //Ecrit les jsons par defaut
    public void LoadDefaultJson(MinecraftServer server) throws IOException {
        for(var skill : SkillsPreset.GetDefaultSkills()){
            WriteSkill(skill, GetSkillPath(skill.GetCategory(), server));
        }
    }

    public void LoadDefaultXpValues(MinecraftServer server) throws IOException {
        Path blockFile = server.getServerDirectory().resolve("config")
                .resolve("tyzs_skills")
                .resolve("block-xp-values.json");

        Path entityFile = server.getServerDirectory().resolve("config")
                .resolve("tyzs_skills")
                .resolve("entity-xp-values.json");

        Path foodFile = server.getServerDirectory().resolve("config")
                .resolve("tyzs_skills")
                .resolve("food-xp-values.json");


        if(!Files.exists(blockFile)) Files.writeString(blockFile, BlockXpValuesPreset.GetDefaultXpValues());
        if(!Files.exists(entityFile)) Files.writeString(entityFile, EntityXpValuesPreset.GetDefaultXpValues());
        if(!Files.exists(foodFile)) Files.writeString(foodFile, FoodValuesPreset.GetDefaultXpValues());
    }

    public void LoadDefaulltLevelPool(MinecraftServer server) throws IOException {
        Path poolFile = server.getServerDirectory().resolve("config")
                .resolve("tyzs_skills")
                .resolve("level-pool.json");

        if(!Files.exists(poolFile)) Files.writeString(poolFile, LevelPoolPreset.GetDefaultRewardValues());
    }

    //Lit les jsons par defaut
    public void ReadJsons(MinecraftServer server) throws IOException{
        Path globalPath = server.getServerDirectory().resolve("config")
                .resolve("tyzs_skills")
                .resolve("skills");

        if(!Files.exists(globalPath)) return;

        try(var stream = Files.walk(globalPath)){
            stream.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".json"))
                    .forEach(path ->{
                        try{
                            var jsonString = Files.readString(path);
                            var jsonObj = gson.fromJson(jsonString, JsonObject.class);
                            SkillLoader.LoadSKill(jsonObj);
                        }
                        catch (IOException ex){throw new RuntimeException(ex);}
                    });
        }
        catch (IOException ex){throw new RuntimeException(ex);}

    }

    public void ReadXpValues(MinecraftServer server) throws IOException {
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
            XpBlock.LoadValues(obj);
        }

        if (Files.exists(entityFile)) {
            var content = Files.readString(entityFile);
            var obj = JsonParser.parseString(content).getAsJsonObject();
            XpEntity.LoadValues(obj);
        }

        if (Files.exists(foodFile)) {
            var content = Files.readString(foodFile);
            var obj = JsonParser.parseString(content).getAsJsonObject();
            XpFood.LoadValues(obj);
        }
    }

    public void ReadLevelPool(MinecraftServer server) throws IOException {
        Path poolFile = server.getServerDirectory().resolve("config")
                .resolve("tyzs_skills")
                .resolve("level-pool.json");

        if(Files.exists((poolFile))){
            var content = Files.readString(poolFile);
            var obj = JsonParser.parseString(content).getAsJsonObject();
            XpManager.loadPool(obj);
        }
    }

    public void ReadCustomSkills(MinecraftServer server){
        Path skillPath = server.getServerDirectory().resolve("config")
                .resolve("tyzs_skills")
                .resolve("skills")
                .resolve("custom");

        if(!Files.exists(skillPath)) return;

        try(var stream = Files.walk(skillPath)){
            stream.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".json"))
                    .forEach(path ->{
                        try{
                            var jsonString = Files.readString(path);
                            var jsonObj = gson.fromJson(jsonString, JsonObject.class);
                            SkillLoader.LoadSKill(jsonObj);
                        }
                        catch (IOException ex){throw new RuntimeException(ex);}
                    });
        }
        catch (IOException ex){throw new RuntimeException(ex);}
    }


    //Utilitaire
    private void WriteSkill(Skill skill, Path path) throws IOException{

        Path skillFile = path.resolve(skill.GetID() + ".json");
        if(Files.exists(skillFile)) {return;}

        String skillJson = gson.toJson(skill);

        Files.writeString(skillFile, skillJson);
    }

    private Path GetSkillPath(Skill.CategoryType category, MinecraftServer server){

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
                return skillPath.resolve("traits");
            }
        }
        return skillPath;
    }
}
