package com.tyzsskills.impl.server.model;

import com.tyzsskills.api.Enums;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.ArrayList;
import java.util.List;

public class SkillsPreset {

    public static List<Skill> GetDefaultSkills(){
        List<Skill> finalList = new ArrayList<>();

        finalList.addAll(GetAbilitySkills());
        finalList.addAll(GetFightSkills());
        finalList.addAll(GetMiscSkills());
        finalList.addAll(GetTraits());

        return finalList;
    }

    private static List<Skill> GetAbilitySkills(){
        List<Skill> finalList = new ArrayList<>();

        finalList.add(new Skill(
                true,
                "health_boost",
                10,
                List.of(2, 3, 4, 5, 7, 9, 11, 14, 17, 20),
                List.of(2.0f, 4.0f, 6.0f, 8.0f, 10.0f, 12.0f, 14.0f, 16.0f, 18.0f, 20.0f),
                Enums.SkillType.GENERIC,
                Enums.CategoryType.ABILITIES,
                "minecraft:generic.max_health",
                AttributeModifier.Operation.ADD_VALUE,
                true,
                "tyzs_skills:textures/gui/skills/health_boost.png",
                "skill.tyzs_skills.health_boost.displayName",
                "skill.tyzs_skills.health_boost.description",
                "skill.tyzs_skills.unit.half_hearts"));

        finalList.add(new Skill(
                true,
                "oxygen_boost",
                4,
                List.of(2, 3, 4, 5),
                List.of(25f, 50f, 75f, 100f),
                Enums.SkillType.GENERIC,
                Enums.CategoryType.ABILITIES,
                "minecraft:generic.oxygen_bonus",
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                true,
                "tyzs_skills:textures/gui/skills/oxygen_boost.png",
                "skill.tyzs_skills.oxygen_boost.displayName",
                "skill.tyzs_skills.oxygen_boost.description",
                "skill.tyzs_skills.unit.percentage"));

        finalList.add(new Skill(
                true,
                "block_reach",
                4,
                List.of(4, 6, 8, 12),
                List.of(1f, 2f, 3f, 4f),
                Enums.SkillType.GENERIC,
                Enums.CategoryType.ABILITIES,
                "minecraft:player.block_interaction_range",
                AttributeModifier.Operation.ADD_VALUE,
                true,
                "tyzs_skills:textures/gui/skills/block_reach.png",
                "skill.tyzs_skills.block_reach.displayName",
                "skill.tyzs_skills.block_reach.description",
                "skill.tyzs_skills.unit.blocks"));

        finalList.add(new Skill(
                true,
                "attack_damage",
                3,
                List.of(5, 8, 12),
                List.of(1f, 2f, 3f),
                Enums.SkillType.GENERIC,
                Enums.CategoryType.ABILITIES,
                "minecraft:generic.attack_damage",
                AttributeModifier.Operation.ADD_VALUE,
                true,
                "tyzs_skills:textures/gui/skills/attack_damage.png",
                "skill.tyzs_skills.attack_damage.displayName",
                "skill.tyzs_skills.attack_damage.description",
                "skill.tyzs_skills.unit.damage"));

        finalList.add(new Skill(
                true,
                "sneak_speed",
                5,
                List.of(2, 2, 3, 3, 4),
                List.of(30f, 60f, 90f, 120f, 150f),
                Enums.SkillType.GENERIC,
                Enums.CategoryType.ABILITIES,
                "minecraft:player.sneaking_speed",
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                true,
                "tyzs_skills:textures/gui/skills/sneak_speed.png",
                "skill.tyzs_skills.sneak_speed.displayName",
                "skill.tyzs_skills.sneak_speed.description",
                "skill.tyzs_skills.unit.percentage"));

        finalList.add(new Skill(
                true,
                "speed_boost",
                5,
                List.of(2, 3, 4, 5, 7),
                List.of(20f, 40f, 60f, 80f, 100f),
                Enums.SkillType.GENERIC,
                Enums.CategoryType.ABILITIES,
                "minecraft:generic.movement_speed",
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                true,
                "tyzs_skills:textures/gui/skills/speed.png",
                "skill.tyzs_skills.speed.displayName",
                "skill.tyzs_skills.speed.description",
                "skill.tyzs_skills.unit.percentage"));

        finalList.add(new Skill(
                true,
                "step_height",
                3,
                List.of(2, 2, 3),
                List.of(0.5f, 1f, 1.5f),
                Enums.SkillType.GENERIC,
                Enums.CategoryType.ABILITIES,
                "minecraft:generic.step_height",
                AttributeModifier.Operation.ADD_VALUE,
                true,
                "tyzs_skills:textures/gui/skills/step_height.png",
                "skill.tyzs_skills.step_height.displayName",
                "skill.tyzs_skills.step_height.description",
                "skill.tyzs_skills.unit.blocks"));

        finalList.add(new Skill(
                true,
                "swim_speed",
                5,
                List.of(2, 3, 4, 5, 7),
                List.of(20f, 40f, 60f, 80f, 100f),
                Enums.SkillType.GENERIC,
                Enums.CategoryType.ABILITIES,
                "neoforge:swim_speed",
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                true,
                "tyzs_skills:textures/gui/skills/swim_speed.png",
                "skill.tyzs_skills.swim_speed.displayName",
                "skill.tyzs_skills.swim_speed.description",
                "skill.tyzs_skills.unit.percentage"));

        return finalList;
    }

    private static List<Skill> GetFightSkills(){
        List<Skill> finalList = new ArrayList<>();

        finalList.add(new Skill(
                true,
                "venomous_attack",
                5,
                List.of(2, 3, 4, 5, 7),
                List.of(3f, 8f, 14f, 25f, 40f),
                Enums.SkillType.IMMUTABLE,
                Enums.CategoryType.FIGHT,
                "",
                null,
                true,
                "tyzs_skills:textures/gui/skills/venomous_attack.png",
                "skill.tyzs_skills.venomous_attack.displayName",
                "skill.tyzs_skills.venomous_attack.description",
                "skill.tyzs_skills.unit.percentage"));

        finalList.add(new Skill(
                true,
                "critical_hit",
                5,
                List.of(2, 3, 4, 5, 7),
                List.of(5f, 10f, 15f, 20f, 25f),
                Enums.SkillType.IMMUTABLE,
                Enums.CategoryType.FIGHT,
                "",
                null,
                true,
                "tyzs_skills:textures/gui/skills/critical_hit.png",
                "skill.tyzs_skills.critical_hit.displayName",
                "skill.tyzs_skills.critical_hit.description",
                "skill.tyzs_skills.unit.percentage"));

        finalList.add(new Skill(
                true,
                "damage_deal",
                5,
                List.of(2, 3, 4, 5, 7),
                List.of(3f, 7f, 11f, 15f, 20f),
                Enums.SkillType.IMMUTABLE,
                Enums.CategoryType.FIGHT,
                "",
                null,
                true,
                "tyzs_skills:textures/gui/skills/damage_deal.png",
                "skill.tyzs_skills.damage_deal.displayName",
                "skill.tyzs_skills.damage_deal.description",
                "skill.tyzs_skills.unit.percentage"));

        finalList.add(new Skill(
                true,
                "bloodlust",
                4,
                List.of(5, 8, 12, 18),
                List.of(7f, 15f, 22f, 30f),
                Enums.SkillType.IMMUTABLE,
                Enums.CategoryType.FIGHT,
                "",
                null,
                true,
                "tyzs_skills:textures/gui/skills/bloodlust.png",
                "skill.tyzs_skills.bloodlust.displayName",
                "skill.tyzs_skills.bloodlust.description",
                "skill.tyzs_skills.unit.percentage"));

        finalList.add(new Skill(
                true,
                "resistance",
                5,
                List.of(5, 8, 12, 18, 24),
                List.of(10f, 20f, 30f, 40f, 50f),
                Enums.SkillType.IMMUTABLE,
                Enums.CategoryType.FIGHT,
                "",
                null,
                true,
                "tyzs_skills:textures/gui/skills/resistance.png",
                "skill.tyzs_skills.resistance.displayName",
                "skill.tyzs_skills.resistance.description",
                "skill.tyzs_skills.unit.percentage"));

        finalList.add(new Skill(
                true,
                "backstab",
                5,
                List.of(2, 3, 4, 5, 7),
                List.of(5f, 10f, 15f, 20f, 25f),
                Enums.SkillType.IMMUTABLE,
                Enums.CategoryType.FIGHT,
                "",
                null,
                true,
                "tyzs_skills:textures/gui/skills/backstab.png",
                "skill.tyzs_skills.backstab.displayName",
                "skill.tyzs_skills.backstab.description",
                "skill.tyzs_skills.unit.percentage"));

        finalList.add(new Skill(
                true,
                "rage",
                5,
                List.of(2, 3, 4, 5, 7),
                List.of(10f, 20f, 30f, 40f, 50f),
                Enums.SkillType.IMMUTABLE,
                Enums.CategoryType.FIGHT,
                "",
                null,
                true,
                "tyzs_skills:textures/gui/skills/rage.png",
                "skill.tyzs_skills.rage.displayName",
                "skill.tyzs_skills.rage.description",
                "skill.tyzs_skills.unit.percentage"));

        finalList.add(new Skill(
                true,
                "adrenaline",
                1,
                List.of(2),
                List.of(25f),
                Enums.SkillType.IMMUTABLE,
                Enums.CategoryType.FIGHT,
                "",
                null,
                true,
                "tyzs_skills:textures/gui/skills/adrenaline.png",
                "skill.tyzs_skills.adrenaline.displayName",
                "skill.tyzs_skills.adrenaline.description",
                "skill.tyzs_skills.unit.percentage"));

        finalList.add(new Skill(
                true,
                "resilience",
                9,
                List.of(2, 3, 4, 5, 7, 9, 11, 14, 17),
                List.of(15f, 25f, 35f, 45f, 55f, 65f, 75f, 85f, 95f),
                Enums.SkillType.IMMUTABLE,
                Enums.CategoryType.FIGHT,
                "",
                null,
                true,
                "tyzs_skills:textures/gui/skills/resilience.png",
                "skill.tyzs_skills.resilience.displayName",
                "skill.tyzs_skills.resilience.description",
                "skill.tyzs_skills.unit.percentage"));

        return finalList;
    }

    private static List<Skill> GetMiscSkills(){
        List<Skill> finalList = new ArrayList<>();

        finalList.add(new Skill(
                true,
                "swift_learn",
                5,
                List.of(5, 8, 12, 18, 24),
                List.of(20f, 40f, 60f, 80f, 100f),
                Enums.SkillType.GENERIC,
                Enums.CategoryType.MISC,
                "tyzs_skills:skill_xp_multiplier",
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                true,
                "tyzs_skills:textures/gui/skills/swift_learn.png",
                "skill.tyzs_skills.swift_learn.displayName",
                "skill.tyzs_skills.swift_learn.description",
                "skill.tyzs_skills.unit.percentage"));

        finalList.add(new Skill(
                true,
                "green_thumb",
                5,
                List.of(2, 2, 3, 3, 4),
                List.of(15f, 30f, 45f, 60f, 75f),
                Enums.SkillType.IMMUTABLE,
                Enums.CategoryType.MISC,
                "",
                null,
                true,
                "tyzs_skills:textures/gui/skills/green_thumb.png",
                "skill.tyzs_skills.green_thumb.displayName",
                "skill.tyzs_skills.green_thumb.description",
                "skill.tyzs_skills.unit.percentage"));

        finalList.add(new Skill(
                true,
                "nutrition",
                5,
                List.of(2, 3, 4, 5, 7),
                List.of(5f, 10f, 15f, 20f, 25f),
                Enums.SkillType.IMMUTABLE,
                Enums.CategoryType.MISC,
                "",
                null,
                true,
                "tyzs_skills:textures/gui/skills/nutrition.png",
                "skill.tyzs_skills.nutrition.displayName",
                "skill.tyzs_skills.nutrition.description",
                "skill.tyzs_skills.unit.percentage"));

        finalList.add(new Skill(
                true,
                "experience_boost",
                5,
                List.of(4, 6, 8, 12, 16),
                List.of(20f, 40f, 60f, 80f, 100f),
                Enums.SkillType.IMMUTABLE,
                Enums.CategoryType.MISC,
                "",
                null,
                true,
                "tyzs_skills:textures/gui/skills/experience_boost.png",
                "skill.tyzs_skills.experience_boost.displayName",
                "skill.tyzs_skills.experience_boost.description",
                "skill.tyzs_skills.unit.percentage"));

        finalList.add(new Skill(
                true,
                "excavation",
                10,
                List.of(2, 2, 3, 3, 4, 4, 5, 5, 6, 6),
                List.of(20f, 40f, 60f, 80f, 100f, 120f, 140f, 160f, 180f, 200f),
                Enums.SkillType.GENERIC,
                Enums.CategoryType.MISC,
                "minecraft:player.block_break_speed",
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                true,
                "tyzs_skills:textures/gui/skills/excavation.png",
                "skill.tyzs_skills.excavation.displayName",
                "skill.tyzs_skills.excavation.description",
                "skill.tyzs_skills.unit.percentage"));

        finalList.add(new Skill(
                true,
                "stealth",
                5,
                List.of(4, 6, 8, 12, 16),
                List.of(10f, 20f, 30f, 40f, 50f),
                Enums.SkillType.IMMUTABLE,
                Enums.CategoryType.MISC,
                "",
                null,
                true,
                "tyzs_skills:textures/gui/skills/stealth.png",
                "skill.tyzs_skills.stealth.displayName",
                "skill.tyzs_skills.stealth.description",
                "skill.tyzs_skills.unit.percentage"));

        finalList.add(new Skill(
                true,
                "magnet",
                4,
                List.of(4, 6, 8, 12),
                List.of(3f, 5f, 7f, 10f),
                Enums.SkillType.IMMUTABLE,
                Enums.CategoryType.MISC,
                "",
                null,
                true,
                "tyzs_skills:textures/gui/skills/magnet.png",
                "skill.tyzs_skills.magnet.displayName",
                "skill.tyzs_skills.magnet.description",
                "skill.tyzs_skills.unit.block_radius"));

        finalList.add(new Skill(
                true,
                "trait_surge",
                5,
                List.of(10, 15, 20, 30, 45),
                List.of(1f, 2f, 3f, 4f, 5f),
                Enums.SkillType.GENERIC,
                Enums.CategoryType.MISC,
                "tyzs_skills:trait_power",
                AttributeModifier.Operation.ADD_VALUE,
                true,
                "tyzs_skills:textures/gui/skills/trait_surge.png",
                "skill.tyzs_skills.trait_surge.displayName",
                "skill.tyzs_skills.trait_surge.description",
                "skill.tyzs_skills.unit.trait_power"));

        return finalList;
    }

    private static List<Skill> GetTraits(){
        List<Skill> finalList = new ArrayList<>();

        finalList.add(new Trait(
                true,
                "cinder_blood",
                6,
                55,
                true,
                "tyzs_skills:textures/gui/skills/cinder_blood.png",
                "skill.tyzs_skills.cinder_blood.displayName",
                "skill.tyzs_skills.cinder_blood.description"));

        finalList.add(new Trait(
                true,
                "deep_lode",
                6,
                55,
                true,
                "tyzs_skills:textures/gui/skills/deep_lode.png",
                "skill.tyzs_skills.deep_lode.displayName",
                "skill.tyzs_skills.deep_lode.description"));

        finalList.add(new Trait(
                true,
                "root_cleaver",
                3,
                25,
                true,
                "tyzs_skills:textures/gui/skills/root_cleaver.png",
                "skill.tyzs_skills.root_cleaver.displayName",
                "skill.tyzs_skills.root_cleaver.description"));

        finalList.add(new Trait(
                true,
                "iron_gut",
                1,
                10,
                true,
                "tyzs_skills:textures/gui/skills/iron_gut.png",
                "skill.tyzs_skills.iron_gut.displayName",
                "skill.tyzs_skills.iron_gut.description"));

        finalList.add(new Trait(
                true,
                "gilded_aura",
                3,
                20,
                true,
                "tyzs_skills:textures/gui/skills/gilded_aura.png",
                "skill.tyzs_skills.gilded_aura.displayName",
                "skill.tyzs_skills.gilded_aura.description"));

        finalList.add(new Trait(
                true,
                "silver_tongue",
                2,
                15,
                true,
                "tyzs_skills:textures/gui/skills/silver_tongue.png",
                "skill.tyzs_skills.silver_tongue.displayName",
                "skill.tyzs_skills.silver_tongue.description"));

        finalList.add(new Trait(
                true,
                "keepsake",
                7,
                65,
                true,
                "tyzs_skills:textures/gui/skills/keepsake.png",
                "skill.tyzs_skills.keepsake.displayName",
                "skill.tyzs_skills.keepsake.description"));

        finalList.add(new Trait(
                false,
                "soundless",
                5,
                45,
                false,
                "tyzs_skills:textures/gui/skills/soundless.png",
                "skill.tyzs_skills.soundless.displayName",
                "skill.tyzs_skills.soundless.description"));

        finalList.add(new Trait(
                true,
                "refiner",
                4,
                35,
                true,
                "tyzs_skills:textures/gui/skills/refiner.png",
                "skill.tyzs_skills.refiner.displayName",
                "skill.tyzs_skills.refiner.description"));

        finalList.add(new Trait(
                true,
                "deep_sight",
                4,
                40,
                true,
                "tyzs_skills:textures/gui/skills/deep_sight.png",
                "skill.tyzs_skills.deep_sight.displayName",
                "skill.tyzs_skills.deep_sight.description"));

        finalList.add(new Trait(
                true,
                "deep_rest",
                4,
                30,
                true,
                "tyzs_skills:textures/gui/skills/deep_rest.png",
                "skill.tyzs_skills.deep_rest.displayName",
                "skill.tyzs_skills.deep_rest.description"));


        return finalList;
    }
}
