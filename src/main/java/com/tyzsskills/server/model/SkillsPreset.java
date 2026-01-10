package com.tyzsskills.server.model;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.ArrayList;
import java.util.List;

public class SkillsPreset {

    public static List<Skill> GetDefaultSkills(){
        List<Skill> finalList = new ArrayList<>();

        finalList.addAll(GetAbilitieSkills());
        finalList.addAll(GetFightSkills());

        return finalList;
    }

    private static List<Skill> GetAbilitieSkills(){
        List<Skill> finalList = new ArrayList<>();

        finalList.add(new Skill(
                true,
                "Health_boost",
                10,
                List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                List.of(2.0f, 4.0f, 6.0f, 8.0f, 10.0f, 12.0f, 14.0f, 16.0f, 18.0f, 20.0f),
                Skill.SkillType.GENERIC,
                Skill.SkillCategory.ABILITIES,
                "minecraft:generic.max_health",
                AttributeModifier.Operation.ADD_VALUE,
                true));

        return finalList;
    }

    private static List<Skill> GetFightSkills(){
        List<Skill> finalList = new ArrayList<>();
        return finalList;
    }
}
