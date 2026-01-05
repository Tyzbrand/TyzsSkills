package com.tyzsskills.server.active;

import java.io.IOException;

import com.google.gson.JsonObject;
import com.tyzsskills.server.model.*;
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

        allPaths.add(globalPath.resolve("skills")
                .resolve("DEFAULT")
                .resolve("special"));

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
        Path file = server.getServerDirectory().resolve("config")
                .resolve("tyzs_skills")
                .resolve("BlockXpValues.json");

        if(!Files.exists(file)) Files.writeString(file, XpValuesPreset.GetDefaultXpValues());
    }

    //Permet de lire et d'appeler la construction des skills
    public void ReadSkills(MinecraftServer server) throws IOException{
        Path globalPath = server.getServerDirectory().resolve("config")
                .resolve("tyzs_skills")
                .resolve("skills")
                .resolve("DEFAULT");

        if(!Files.exists(globalPath)) return;

        try{
            Files.walk(globalPath)
                    .filter(Files::isRegularFile)
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

    private Path GetSkillPath(Skill.SkillCategory category, MinecraftServer server){

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
            case SPECIAL -> {
                return skillPath.resolve("special");
            }
        }
        return skillPath;
    }
}
