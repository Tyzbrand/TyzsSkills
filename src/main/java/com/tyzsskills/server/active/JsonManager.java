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

public class JsonManager {

    private static final JsonManager instance = new JsonManager();
    public static JsonManager Get(){return instance;}

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();


    //Prechargement des json globaux au chargement du serveur
    public void LoadDefaultGlobalJson(MinecraftServer server) throws IOException {

        Path globalPath = server.getServerDirectory().resolve("config")
                .resolve("tyzs_skills");

        Path skillFolder = globalPath.resolve("skillsGlobal");
        try
        {
            if(!Files.exists(skillFolder)) {Files.createDirectories(skillFolder);}
        }
        catch (IOException ex) { System.out.println(ex.getMessage());}

        for(Skill sk : PreLoadAbilitiesSkills()) {WriteSkill(sk, skillFolder);}

    }

    public void ReadJson(MinecraftServer server) throws IOException{
        Path globalPath = server.getServerDirectory().resolve("config")
                .resolve("tyzs_skills");

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


    //CHargement par defaut
    private List<Skill> PreLoadAbilitiesSkills() {
        List<Skill> skills = new ArrayList<>();

        skills.add(new PassiveSkill(
                true,
                "Health_boost",
                "Health Boost",
                10,
                List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                Skill.SkillType.GENERIC,
                Skill.SkillCategory.ABILITIES,
                "generic.max_health",
                List.of(2.0f, 4.0f, 6.0f, 8.0f, 10.0f, 12.0f, 14.0f, 16.0f, 18.0f, 20.0f)));

        skills.add(new PassiveSkill(
                true,
                "Speed",
                "Speed Boost",
                5,
                List.of(1, 2, 3, 4, 5),
                Skill.SkillType.GENERIC,
                Skill.SkillCategory.ABILITIES,
                "generic.movement_speed",
                List.of(2.0f, 4.0f, 6.0f, 8.0f, 10.0f)));

        return skills;
    }


    //Utilitaire
    private void WriteSkill(Skill skill, Path path) throws IOException{

        Path skillFile = path.resolve(skill.GetID() + ".json");
        if(Files.exists(skillFile)) {return;}

        String skillJson = gson.toJson(skill);

        Files.writeString(skillFile, skillJson);
    }
}
