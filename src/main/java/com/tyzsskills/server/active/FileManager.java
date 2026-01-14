package com.tyzsskills.server.active;

import java.io.IOException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tyzsskills.client.screen.MainGUI;
import com.tyzsskills.server.model.*;
import com.tyzsskills.server.skills.SkillLoader;
import com.tyzsskills.server.xp.XpManager;
import com.tyzsskills.server.xp.xpEvents.XpBlock;
import com.tyzsskills.server.xp.xpEvents.XpEntity;
import net.minecraft.server.MinecraftServer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;

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
                .resolve("DEFAULT")
                .resolve("abilities"));

        allPaths.add(globalPath.resolve("skills")
                .resolve("DEFAULT")
                .resolve("fight"));

        allPaths.add(globalPath.resolve("skills")
                .resolve("DEFAULT")
                .resolve("misc"));

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
                .resolve("Block-Xp-Values.json");

        Path entityFile = server.getServerDirectory().resolve("config")
                .resolve("tyzs_skills")
                .resolve("Entity-Xp-Values.json");


        if(!Files.exists(blockFile)) Files.writeString(blockFile, BlockXpValuesPreset.GetDefaultXpValues());
        if(!Files.exists(entityFile)) Files.writeString(entityFile, EntityXpValuesPreset.GetDefaultXpValues());
    }

    public void LoadDefaulltLevelPool(MinecraftServer server) throws IOException {
        Path poolFile = server.getServerDirectory().resolve("config")
                .resolve("tyzs_skills")
                .resolve("Level-Pool.json");

        if(!Files.exists(poolFile)) Files.writeString(poolFile, LevelPoolPreset.GetDefaultRewardValues());
    }

    //Lit les jsons par defaut
    public void ReadJsons(MinecraftServer server) throws IOException{
        Path globalPath = server.getServerDirectory().resolve("config")
                .resolve("tyzs_skills")
                .resolve("skills")
                .resolve("DEFAULT");

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
                .resolve("Block-Xp-Values.json");

        Path entityFile = server.getServerDirectory().resolve("config")
                .resolve("tyzs_skills")
                .resolve("Entity-Xp-Values.json");

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
    }

    public void ReadLevelPool(MinecraftServer server) throws IOException {
        Path poolFile = server.getServerDirectory().resolve("config")
                .resolve("tyzs_skills")
                .resolve("Level-Pool.json");

        if(Files.exists((poolFile))){
            var content = Files.readString(poolFile);
            var obj = JsonParser.parseString(content).getAsJsonObject();
            XpManager.LoadPool(obj);
        }
    }


    //Utilitaire
    private void WriteSkill(Skill skill, Path path) throws IOException{

        Path skillFile = path.resolve(skill.GetID() + ".json");
        if(Files.exists(skillFile)) {return;}

        String skillJson = gson.toJson(skill);

        Files.writeString(skillFile, skillJson);
    }

    private Path GetSkillPath(MainGUI.CategoryType category, MinecraftServer server){

        Path skillPath = server.getServerDirectory()
                .resolve("config")
                .resolve("tyzs_skills")
                .resolve("skills")
                .resolve("DEFAULT");

        switch (category){
            case ABILITIES -> {
                return skillPath.resolve("abilities");
            }
            case FIGHT -> {
                return skillPath.resolve("fight");
            }
            case MISC -> {
                return skillPath.resolve("misc");
            }
            case ALL -> {
                return skillPath.resolve("Misc");
            }
        }
        return skillPath;
    }
}
