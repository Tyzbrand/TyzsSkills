package com.tyzsskills.impl.server.model;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.records.Modifier;
import com.tyzsskills.api.records.ValueSet;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SkillsPreset {

    public static List<Skill> getDefaultSkills(){
        List<Skill> finalList = new ArrayList<>();

        finalList.addAll(getAbilitySkills());
        finalList.addAll(getFightSkills());
        finalList.addAll(getMiscSkills());

        return finalList;
    }

    private static List<Skill> getAbilitySkills(){
        List<Skill> finalList = new ArrayList<>();

        finalList.add(new Skill(
                true,
                "health_boost",
                10,
                List.of(2, 3, 4, 5, 7, 9, 11, 14, 17, 20),
                Enums.SkillType.GENERIC,
                Enums.CategoryType.ABILITIES,
                true,
                "tyzs_skills:textures/gui/skills/health_boost.png",
                "skill.tyzs_skills.health_boost.displayName",
                "skill.tyzs_skills.health_boost.description",
                List.of(new Modifier("minecraft:generic.max_health",
                        AttributeModifier.Operation.ADD_VALUE,
                        List.of(2.0f, 4.0f, 6.0f, 8.0f, 10.0f, 12.0f, 14.0f, 16.0f, 18.0f, 20.0f),
                        "skill.tyzs_skills.unit.half_hearts")),
                null));

        finalList.add(new Skill(
                true,
                "oxygen_boost",
                4,
                List.of(2, 3, 4, 5),
                Enums.SkillType.GENERIC,
                Enums.CategoryType.ABILITIES,
                true,
                "tyzs_skills:textures/gui/skills/oxygen_boost.png",
                "skill.tyzs_skills.oxygen_boost.displayName",
                "skill.tyzs_skills.oxygen_boost.description",
                List.of(new Modifier("minecraft:generic.oxygen_bonus",
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                        List.of(25f, 50f, 75f, 100f),
                        "skill.tyzs_skills.unit.percentage")),
                null));

        finalList.add(new Skill(
                true,
                "block_reach",
                4,
                List.of(4, 6, 8, 12),
                Enums.SkillType.GENERIC,
                Enums.CategoryType.ABILITIES,
                true,
                "tyzs_skills:textures/gui/skills/block_reach.png",
                "skill.tyzs_skills.block_reach.displayName",
                "skill.tyzs_skills.block_reach.description",
                List.of(new Modifier("minecraft:player.block_interaction_range",
                        AttributeModifier.Operation.ADD_VALUE,
                        List.of(1f, 2f, 3f, 4f),
                        "skill.tyzs_skills.unit.blocks")),
                null));

        finalList.add(new Skill(
                true,
                "attack_damage",
                3,
                List.of(5, 8, 12),
                Enums.SkillType.GENERIC,
                Enums.CategoryType.ABILITIES,
                true,
                "tyzs_skills:textures/gui/skills/attack_damage.png",
                "skill.tyzs_skills.attack_damage.displayName",
                "skill.tyzs_skills.attack_damage.description",
                List.of(new Modifier("minecraft:generic.attack_damage",
                        AttributeModifier.Operation.ADD_VALUE,
                        List.of(1f, 2f, 3f),
                        "skill.tyzs_skills.unit.damage")),
                null));

        finalList.add(new Skill(
                true,
                "sneak_speed",
                5,
                List.of(2, 2, 3, 3, 4),
                Enums.SkillType.GENERIC,
                Enums.CategoryType.ABILITIES,
                true,
                "tyzs_skills:textures/gui/skills/sneak_speed.png",
                "skill.tyzs_skills.sneak_speed.displayName",
                "skill.tyzs_skills.sneak_speed.description",
                List.of(new Modifier("minecraft:player.sneaking_speed",
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                        List.of(20f, 40f, 60f, 80f, 100f),
                        "skill.tyzs_skills.unit.percentage")),
                null));

        finalList.add(new Skill(
                true,
                "speed_boost",
                5,
                List.of(2, 3, 4, 5, 7),
                Enums.SkillType.GENERIC,
                Enums.CategoryType.ABILITIES,
                true,
                "tyzs_skills:textures/gui/skills/speed.png",
                "skill.tyzs_skills.speed.displayName",
                "skill.tyzs_skills.speed.description",
                List.of(new Modifier("minecraft:generic.movement_speed",
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                        List.of(15f, 30f, 45f, 60f, 75f),
                        "skill.tyzs_skills.unit.percentage")),
                null));

        finalList.add(new Skill(
                true,
                "step_height",
                3,
                List.of(2, 2, 3),
                Enums.SkillType.GENERIC,
                Enums.CategoryType.ABILITIES,
                true,
                "tyzs_skills:textures/gui/skills/step_height.png",
                "skill.tyzs_skills.step_height.displayName",
                "skill.tyzs_skills.step_height.description",
                List.of(new Modifier("minecraft:generic.step_height",
                        AttributeModifier.Operation.ADD_VALUE,
                        List.of(0.5f, 1f, 1.5f),
                        "skill.tyzs_skills.unit.blocks")),
                null));

        finalList.add(new Skill(
                true,
                "swim_speed",
                5,
                List.of(2, 3, 4, 5, 7),
                Enums.SkillType.GENERIC,
                Enums.CategoryType.ABILITIES,
                true,
                "tyzs_skills:textures/gui/skills/swim_speed.png",
                "skill.tyzs_skills.swim_speed.displayName",
                "skill.tyzs_skills.swim_speed.description",
                List.of(new Modifier("neoforge:swim_speed",
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                        List.of(15f, 30f, 45f, 60f, 75f),
                        "skill.tyzs_skills.unit.percentage")),
                null));

        return finalList;
    }

    private static List<Skill> getFightSkills(){
        List<Skill> finalList = new ArrayList<>();

        finalList.add(new Skill(
                true,
                "venomous_attack",
                5,
                List.of(2, 3, 4, 5, 7),
                Enums.SkillType.IMMUTABLE,
                Enums.CategoryType.FIGHT,
                true,
                "tyzs_skills:textures/gui/skills/venomous_attack.png",
                "skill.tyzs_skills.venomous_attack.displayName",
                "skill.tyzs_skills.venomous_attack.description",
                null,
                Map.of(
                        "success_probability", new ValueSet(List.of(3f, 8f, 14f, 25f, 40f), "skill.tyzs_skills.unit.percentage"))
        ));

        finalList.add(new Skill(
                true,
                "critical_hit",
                5,
                List.of(2, 3, 4, 5, 7),
                Enums.SkillType.IMMUTABLE,
                Enums.CategoryType.FIGHT,
                true,
                "tyzs_skills:textures/gui/skills/critical_hit.png",
                "skill.tyzs_skills.critical_hit.displayName",
                "skill.tyzs_skills.critical_hit.description",
                null,
                Map.of(
                        "success_probability", new ValueSet(List.of(5f, 10f, 15f, 20f, 25f), "skill.tyzs_skills.unit.percentage"))
        ));

        finalList.add(new Skill(
                true,
                "damage_deal",
                5,
                List.of(2, 3, 4, 5, 7),
                Enums.SkillType.IMMUTABLE,
                Enums.CategoryType.FIGHT,
                true,
                "tyzs_skills:textures/gui/skills/damage_deal.png",
                "skill.tyzs_skills.damage_deal.displayName",
                "skill.tyzs_skills.damage_deal.description",
                null,
                Map.of(
                        "success_probability", new ValueSet(List.of(3f, 7f, 11f, 15f, 20f), "skill.tyzs_skills.unit.percentage"))
        ));


        finalList.add(new Skill(
                true,
                "bloodlust",
                4,
                List.of(5, 8, 12, 18),
                Enums.SkillType.IMMUTABLE,
                Enums.CategoryType.FIGHT,
                true,
                "tyzs_skills:textures/gui/skills/bloodlust.png",
                "skill.tyzs_skills.bloodlust.displayName",
                "skill.tyzs_skills.bloodlust.description",
                null,
                Map.of(
                        "health_percentage", new ValueSet(List.of(7f, 15f, 22f, 30f), "skill.tyzs_skills.unit.percentage"))
        ));

        finalList.add(new Skill(
                true,
                "resistance",
                6,
                List.of(5, 8, 12, 18, 24, 32),
                Enums.SkillType.IMMUTABLE,
                Enums.CategoryType.FIGHT,
                true,
                "tyzs_skills:textures/gui/skills/resistance.png",
                "skill.tyzs_skills.resistance.displayName",
                "skill.tyzs_skills.resistance.description",
                null,
                Map.of(
                        "damage_resistance", new ValueSet(List.of(7f, 15f, 20f, 30f, 35f, 45f), "skill.tyzs_skills.unit.percentage"))
        ));

        finalList.add(new Skill(
                true,
                "backstab",
                5,
                List.of(2, 3, 4, 5, 7),
                Enums.SkillType.IMMUTABLE,
                Enums.CategoryType.FIGHT,
                true,
                "tyzs_skills:textures/gui/skills/backstab.png",
                "skill.tyzs_skills.backstab.displayName",
                "skill.tyzs_skills.backstab.description",
                null,
                Map.of(
                        "damage_buff", new ValueSet(List.of(5f, 10f, 15f, 20f, 25f), "skill.tyzs_skills.unit.percentage"))
        ));

        finalList.add(new Skill(
                true,
                "rage",
                5,
                List.of(2, 3, 4, 5, 7),
                Enums.SkillType.IMMUTABLE,
                Enums.CategoryType.FIGHT,
                true,
                "tyzs_skills:textures/gui/skills/rage.png",
                "skill.tyzs_skills.rage.displayName",
                "skill.tyzs_skills.rage.description",
                null,
                Map.of(
                        "damage_buff", new ValueSet(List.of(10f, 20f, 30f, 40f, 50f), "skill.tyzs_skills.unit.percentage_damage"),
                        "health_threshold", new ValueSet(List.of(10f, 10f, 20f, 20f, 30f), "skill.tyzs_skills.unit.percentage"))
        ));

        finalList.add(new Skill(
                true,
                "adrenaline",
                3,
                List.of(2, 4, 6),
                Enums.SkillType.IMMUTABLE,
                Enums.CategoryType.FIGHT,
                true,
                "tyzs_skills:textures/gui/skills/adrenaline.png",
                "skill.tyzs_skills.adrenaline.displayName",
                "skill.tyzs_skills.adrenaline.description",
                null,
                Map.of(
                        "health_threshold", new ValueSet(List.of(10f, 20f, 30f), "skill.tyzs_skills.unit.percentage"),
                        "effect_duration", new ValueSet(List.of(3f, 5f, 8f), "skill.tyzs_skills.unit.seconds"))
        ));

        finalList.add(new Skill(
                true,
                "resilience",
                9,
                List.of(2, 3, 4, 5, 7, 9, 11, 14, 17),
                Enums.SkillType.IMMUTABLE,
                Enums.CategoryType.FIGHT,
                true,
                "tyzs_skills:textures/gui/skills/resilience.png",
                "skill.tyzs_skills.resilience.displayName",
                "skill.tyzs_skills.resilience.description",
                null,
                Map.of(
                        "effect_time_reduction", new ValueSet(List.of(15f, 25f, 35f, 45f, 55f, 65f, 75f, 85f, 95f), "skill.tyzs_skills.unit.percentage"))
        ));

        finalList.add(new Skill(
                true,
                "jinxed",
                3,
                List.of(5, 8, 12),
                Enums.SkillType.IMMUTABLE,
                Enums.CategoryType.FIGHT,
                true,
                "tyzs_skills:textures/gui/skills/jinxed.png",
                "skill.tyzs_skills.jinxed.displayName",
                "skill.tyzs_skills.jinxed.description",
                null,
                Map.of(
                        "success_probability", new ValueSet(List.of(3f, 5f, 7f), "skill.tyzs_skills.unit.percentage"))
        ));

        finalList.add(new Skill(
                true,
                "true_strike",
                3,
                List.of(10, 15, 20),
                Enums.SkillType.IMMUTABLE,
                Enums.CategoryType.FIGHT,
                true,
                "tyzs_skills:textures/gui/skills/true_strike.png",
                "skill.tyzs_skills.true_strike.displayName",
                "skill.tyzs_skills.true_strike.description",
                null,
                Map.of(
                        "success_probability", new ValueSet(List.of(3f, 6f, 9f), "skill.tyzs_skills.unit.percentage"))
        ));

        finalList.add(new Skill(
                true,
                "sunder_armor",
                5,
                List.of(4, 6, 8, 12, 16),
                Enums.SkillType.IMMUTABLE,
                Enums.CategoryType.FIGHT,
                true,
                "tyzs_skills:textures/gui/skills/sunder_armor.png",
                "skill.tyzs_skills.sunder_armor.displayName",
                "skill.tyzs_skills.sunder_armor.description",
                null,
                Map.of(
                        "success_probability", new ValueSet(List.of( 3f, 6f, 9f, 12f, 15f), "skill.tyzs_skills.unit.percentage"),
                        "damage_percentage", new ValueSet(List.of(20f, 40f, 60f, 80f, 100f), "skill.tyzs_skills.unit.percentage_damage"))
        ));

        return finalList;
    }

    private static List<Skill> getMiscSkills(){
        List<Skill> finalList = new ArrayList<>();

        finalList.add(new Skill(
                true,
                "swift_learn",
                4,
                List.of(8, 12, 18, 24),
                Enums.SkillType.GENERIC,
                Enums.CategoryType.MISC,
                true,
                "tyzs_skills:textures/gui/skills/swift_learn.png",
                "skill.tyzs_skills.swift_learn.displayName",
                "skill.tyzs_skills.swift_learn.description",
                List.of(new Modifier("tyzs_skills:skill_xp_multiplier",
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                        List.of(15f, 30f, 45f, 60f),
                        "skill.tyzs_skills.unit.percentage")),
                null));

        finalList.add(new Skill(
                true,
                "green_thumb",
                5,
                List.of(2, 2, 3, 3, 4),
                Enums.SkillType.IMMUTABLE,
                Enums.CategoryType.MISC,
                true,
                "tyzs_skills:textures/gui/skills/green_thumb.png",
                "skill.tyzs_skills.green_thumb.displayName",
                "skill.tyzs_skills.green_thumb.description",
                null,
                Map.of(
                        "success_probability", new ValueSet(List.of(15f, 30f, 45f, 60f, 75f), "skill.tyzs_skills.unit.percentage"))
        ));

        finalList.add(new Skill(
                true,
                "nutrition",
                5,
                List.of(2, 3, 4, 5, 7),
                Enums.SkillType.IMMUTABLE,
                Enums.CategoryType.MISC,
                true,
                "tyzs_skills:textures/gui/skills/nutrition.png",
                "skill.tyzs_skills.nutrition.displayName",
                "skill.tyzs_skills.nutrition.description",
                null,
                Map.of(
                        "nutrition_bonus", new ValueSet(List.of(5f, 10f, 15f, 20f, 25f), "skill.tyzs_skills.unit.percentage"))
        ));


        finalList.add(new Skill(
                true,
                "experience_boost",
                4,
                List.of(6, 8, 12, 16),
                Enums.SkillType.IMMUTABLE,
                Enums.CategoryType.MISC,
                true,
                "tyzs_skills:textures/gui/skills/experience_boost.png",
                "skill.tyzs_skills.experience_boost.displayName",
                "skill.tyzs_skills.experience_boost.description",
                null,
                Map.of(
                        "bonus_percentage", new ValueSet(List.of(15f, 30f, 45f, 60f), "skill.tyzs_skills.unit.percentage"))
        ));

        finalList.add(new Skill(
                true,
                "excavation",
                10,
                List.of(2, 2, 3, 3, 4, 4, 5, 5, 6, 6),
                Enums.SkillType.GENERIC,
                Enums.CategoryType.MISC,
                true,
                "tyzs_skills:textures/gui/skills/excavation.png",
                "skill.tyzs_skills.excavation.displayName",
                "skill.tyzs_skills.excavation.description",
                List.of(new Modifier("minecraft:player.block_break_speed",
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                        List.of(20f, 40f, 60f, 80f, 100f, 120f, 140f, 160f, 180f, 200f),
                        "skill.tyzs_skills.unit.percentage")),
                null));

        finalList.add(new Skill(
                true,
                "stealth",
                5,
                List.of(4, 6, 8, 12, 16),
                Enums.SkillType.IMMUTABLE,
                Enums.CategoryType.MISC,
                true,
                "tyzs_skills:textures/gui/skills/stealth.png",
                "skill.tyzs_skills.stealth.displayName",
                "skill.tyzs_skills.stealth.description",
                null,
                Map.of(
                        "range_reduction", new ValueSet(List.of(10f, 20f, 30f, 40f, 50f), "skill.tyzs_skills.unit.percentage"))
        ));

        finalList.add(new Skill(
                true,
                "magnet",
                4,
                List.of(4, 6, 8, 12),
                Enums.SkillType.IMMUTABLE,
                Enums.CategoryType.MISC,
                true,
                "tyzs_skills:textures/gui/skills/magnet.png",
                "skill.tyzs_skills.magnet.displayName",
                "skill.tyzs_skills.magnet.description",
                null,
                Map.of(
                        "block_radius", new ValueSet(List.of(3f, 5f, 7f, 10f), "skill.tyzs_skills.unit.block_radius"))
        ));


        finalList.add(new Skill(
                true,
                "shepherd_s_blessing",
                15,
                List.of(2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9),
                Enums.SkillType.IMMUTABLE,
                Enums.CategoryType.MISC,
                true,
                "tyzs_skills:textures/gui/skills/shepherd_s_blessing.png",
                "skill.tyzs_skills.shepherd_s_blessing.displayName",
                "skill.tyzs_skills.shepherd_s_blessing.description",
                null,
                Map.of(
                        "growth_speed", new ValueSet(List.of(50f, 100f, 150f, 200f, 250f, 300f, 350f, 400f, 450f, 500f, 550f, 600f, 650f, 700f, 750f), "skill.tyzs_skills.unit.percentage"))
        ));

        finalList.add(new Skill(
                true,
                "spare_parts",
                5,
                List.of(5, 8, 12, 18, 24),
                Enums.SkillType.IMMUTABLE,
                Enums.CategoryType.MISC,
                true,
                "tyzs_skills:textures/gui/skills/spare_parts.png",
                "skill.tyzs_skills.spare_parts.displayName",
                "skill.tyzs_skills.spare_parts.description",
                null,
                Map.of(
                        "refund_chance", new ValueSet(List.of(4f, 8f, 12f, 16f, 20f), "skill.tyzs_skills.unit.percentage"))
        ));

        finalList.add(new Skill(
                true,
                "twist_of_fate",
                5,
                List.of(4, 6, 8, 12, 16),
                Enums.SkillType.IMMUTABLE,
                Enums.CategoryType.MISC,
                true,
                "tyzs_skills:textures/gui/skills/twist_of_fate.png",
                "skill.tyzs_skills.twist_of_fate.displayName",
                "skill.tyzs_skills.twist_of_fate.description",
                null,
                Map.of(
                        "loot_multiplier", new ValueSet(List.of(10f, 20f, 30f, 40f, 50f), "skill.tyzs_skills.unit.percentage"))
        ));

        finalList.add(new Skill(
                true,
                "keepsake",
                8,
                List.of(4, 6, 8, 12, 16, 20, 25, 30),
                Enums.SkillType.IMMUTABLE,
                Enums.CategoryType.MISC,
                true,
                "tyzs_skills:textures/gui/skills/keepsake.png",
                "skill.tyzs_skills.keepsake.displayName",
                "skill.tyzs_skills.keepsake.description",
                null,
                Map.of(
                        "saved_slots", new ValueSet(List.of(1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f), "skill.tyzs_skills.unit.slot"))
        ));

        finalList.add(new Skill(
                true,
                "haggler",
                4,
                List.of(2, 3, 4, 5),
                Enums.SkillType.IMMUTABLE,
                Enums.CategoryType.MISC,
                true,
                "tyzs_skills:textures/gui/skills/haggler.png",
                "skill.tyzs_skills.haggler.displayName",
                "skill.tyzs_skills.haggler.description",
                null,
                Map.of(
                        "block_radius", new ValueSet(List.of(10f, 15f, 20f, 25f), "skill.tyzs_skills.unit.block_radius"),
                        "villager_speed", new ValueSet(List.of(0f, 5f, 5f, 10f), "skill.tyzs_skills.unit.percentage"))
        ));

        return finalList;
    }

}
