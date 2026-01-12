package com.tyzsskills.server.model;

import com.tyzsskills.client.screen.MainGUI;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.ArrayList;
import java.util.List;

public class SkillsPreset {

    public static List<Skill> GetDefaultSkills(){
        List<Skill> finalList = new ArrayList<>();

        finalList.addAll(GetAbilitySkills());
        finalList.addAll(GetFightSkills());

        return finalList;
    }

    private static List<Skill> GetAbilitySkills(){
        List<Skill> finalList = new ArrayList<>();

        finalList.add(new Skill(
                true,
                "health_boost",
                10,
                List.of(2, 2, 3, 3, 5, 5, 7, 7, 9, 9),
                List.of(2.0f, 4.0f, 6.0f, 8.0f, 10.0f, 12.0f, 14.0f, 16.0f, 18.0f, 20.0f),
                Skill.SkillType.GENERIC,
                MainGUI.CategoryType.ABILITIES,
                "minecraft:generic.max_health",
                AttributeModifier.Operation.ADD_VALUE,
                true,
                "tyzs_skills:textures/gui/skills/health_boost.png",
                "skill.tyzs_skills.health_boost.displayName",
                "skill.tyzs_skills.health_boost.description"));

        finalList.add(new Skill(
                true,
                "oxygen_boost",
                4,
                List.of(3, 5, 7, 9),
                List.of(1.25f, 1.50f, 1.75f, 2f),
                Skill.SkillType.GENERIC,
                MainGUI.CategoryType.ABILITIES,
                "minecraft:generic.oxygen_bonus",
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                true,
                "tyzs_skills:textures/gui/skills/oxygene_boost.png",
                "skill.tyzs_skills.oxygen_boost.displayName",
                "skill.tyzs_skills.oxygen_boost.description"));

        finalList.add(new Skill(
                true,
                "block_reach",
                4,
                List.of(3, 5, 9, 13),
                List.of(1f, 2f, 3f, 4f),
                Skill.SkillType.GENERIC,
                MainGUI.CategoryType.ABILITIES,
                "minecraft:player.block_interaction_range",
                AttributeModifier.Operation.ADD_VALUE,
                true,
                "tyzs_skills:textures/gui/skills/block_reach.png",
                "skill.tyzs_skills.block_reach.displayName",
                "skill.tyzs_skills.block_reach.description"));

        finalList.add(new Skill(
                true,
                "attack_damage",
                3,
                List.of(5, 10, 16),
                List.of(1f, 2f, 3f),
                Skill.SkillType.GENERIC,
                MainGUI.CategoryType.ABILITIES,
                "minecraft:generic.attack_damage",
                AttributeModifier.Operation.ADD_VALUE,
                true,
                "tyzs_skills:textures/gui/skills/attack_damage.png",
                "skill.tyzs_skills.attack_damage.displayName",
                "skill.tyzs_skills.attack_damage.description"));

        finalList.add(new Skill(
                true,
                "sneaking_speed",
                5,
                List.of(1, 2, 3, 4, 5),
                List.of(1.2f, 1.4f, 1.6f, 1.8f, 2f),
                Skill.SkillType.GENERIC,
                MainGUI.CategoryType.ABILITIES,
                "minecraft:player.sneaking_speed",
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                true,
                "tyzs_skills:textures/gui/skills/sneaking_speed.png",
                "skill.tyzs_skills.sneaking_speed.displayName",
                "skill.tyzs_skills.sneaking_speed.description"));

        finalList.add(new Skill(
                true,
                "speed_boost",
                5,
                List.of(2, 4, 6, 8, 11),
                List.of(1.2f, 1.4f, 1.6f, 1.8f, 2f),
                Skill.SkillType.GENERIC,
                MainGUI.CategoryType.ABILITIES,
                "minecraft:generic.movement_speed",
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                true,
                "tyzs_skills:textures/gui/skills/speed.png",
                "skill.tyzs_skills.speed.displayName",
                "skill.tyzs_skills.speed.description"));

        finalList.add(new Skill(
                true,
                "step_height",
                2,
                List.of(3, 5),
                List.of(1f, 2f),
                Skill.SkillType.GENERIC,
                MainGUI.CategoryType.ABILITIES,
                "minecraft:generic.step_height",
                AttributeModifier.Operation.ADD_VALUE,
                true,
                "tyzs_skills:textures/gui/skills/step_height.png",
                "skill.tyzs_skills.step_height.displayName",
                "skill.tyzs_skills.step_height.description"));

        finalList.add(new Skill(
                true,
                "swim_speed",
                5,
                List.of(2, 4, 6, 8, 11),
                List.of(1.2f, 1.4f, 1.6f, 1.8f, 2f),
                Skill.SkillType.GENERIC,
                MainGUI.CategoryType.ABILITIES,
                "neoforge:swim_speed",
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                true,
                "tyzs_skills:textures/gui/skills/swim_speed.png",
                "skill.tyzs_skills.swim_speed.displayName",
                "skill.tyzs_skills.swim_speed.description"));

        return finalList;
    }


    private static List<Skill> GetFightSkills(){
        List<Skill> finalList = new ArrayList<>();

        finalList.add(new Skill(
                true,
                "venomous_attack",
                5,
                List.of(3, 5, 7, 9, 13),
                List.of(3f, 5f, 8f, 11f, 15f),
                Skill.SkillType.IMMUTABLE,
                MainGUI.CategoryType.FIGHT,
                "",
                null,
                true,
                "tyzs_skills:textures/gui/skills/venomous_attack.png",
                "skill.tyzs_skills.venomous_attack.displayName",
                "skill.tyzs_skills.venomous_attack.description"));

        return finalList;
    }
}
