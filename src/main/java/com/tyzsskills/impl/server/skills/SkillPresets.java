package com.tyzsskills.impl.server.skills;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.model.SkillConfiguration;
import com.tyzsskills.api.records.Modifier;
import com.tyzsskills.api.records.SkillPrefab;
import com.tyzsskills.api.records.ValueSet;
import com.tyzsskills.impl.server.effects.skillEffects.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SkillPresets {

    public static List<SkillPrefab> getDefaultSkills(){
        List<SkillPrefab> finalList = new ArrayList<>();

        finalList.addAll(getAbilitySkills());
        finalList.addAll(getFightSkills());
        finalList.addAll(getMiscSkills());
        finalList.addAll(getNatureSkills());

        return finalList;
    }

    private static List<SkillPrefab> getAbilitySkills(){
        List<SkillPrefab> finalList = new ArrayList<>();

        finalList.add(SkillPrefab.generic("health_boost", "abilities", List.of(2, 3, 4, 5, 7, 9, 11, 14, 17, 20))
                        .addModifier(new Modifier("minecraft:generic.max_health",
                                AttributeModifier.Operation.ADD_VALUE,
                                List.of(2.0f, 4.0f, 6.0f, 8.0f, 10.0f, 12.0f, 14.0f, 16.0f, 18.0f, 20.0f),
                                "skill.tyzs_skills.unit.half_hearts"))
                .build());

        finalList.add(SkillPrefab.generic("oxygen_boost", "abilities", List.of(2, 3, 4, 5))
                .addModifier(new Modifier("minecraft:generic.oxygen_bonus",
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                        List.of(25f, 50f, 75f, 100f),
                        "skill.tyzs_skills.unit.percentage"))
                .build());

        finalList.add(SkillPrefab.generic("block_reach", "abilities", List.of(4, 6, 8, 12))
                .addModifier(new Modifier("minecraft:player.block_interaction_range",
                        AttributeModifier.Operation.ADD_VALUE,
                        List.of(1f, 2f, 3f, 4f),
                        "skill.tyzs_skills.unit.blocks"))
                .build());

        finalList.add(SkillPrefab.generic("attack_damage", "abilities", List.of(5, 8, 12))
                .addModifier(new Modifier("minecraft:generic.attack_damage",
                        AttributeModifier.Operation.ADD_VALUE,
                        List.of(1f, 2f, 3f),
                        "skill.tyzs_skills.unit.damage"))
                .build());

        finalList.add(SkillPrefab.generic("sneak_speed", "abilities", List.of(2, 2, 3, 3, 4))
                .addModifier(new Modifier("minecraft:player.sneaking_speed",
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                        List.of(20f, 40f, 60f, 80f, 100f),
                        "skill.tyzs_skills.unit.percentage"))
                .build());

        finalList.add(SkillPrefab.generic("speed_boost", "abilities", List.of(2, 3, 4, 5, 7))
                .addModifier(new Modifier("minecraft:generic.movement_speed",
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                        List.of(15f, 30f, 45f, 60f, 75f),
                        "skill.tyzs_skills.unit.percentage"))
                .build());


        finalList.add(SkillPrefab.generic("step_height", "abilities", List.of(2, 2, 3))
                .addModifier(new Modifier("minecraft:generic.step_height",
                        AttributeModifier.Operation.ADD_VALUE,
                        List.of(0.5f, 1f, 1.5f),
                        "skill.tyzs_skills.unit.blocks"))
                .build());

        finalList.add(SkillPrefab.generic("swim_speed", "abilities", List.of(2, 3, 4, 5, 7))
                .addModifier(new Modifier("neoforge:swim_speed",
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                        List.of(15f, 30f, 45f, 60f, 75f),
                        "skill.tyzs_skills.unit.percentage"))
                .build());

        finalList.add(SkillPrefab.generic("excavation", "abilities", List.of(2, 2, 3, 3, 4, 4, 5, 5, 6, 6))
                .addModifier(new Modifier("minecraft:player.block_break_speed",
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                        List.of(20f, 40f, 60f, 80f, 100f, 120f, 140f, 160f, 180f, 200f),
                        "skill.tyzs_skills.unit.percentage"))
                .build());

        return finalList;
    }

    private static List<SkillPrefab> getFightSkills(){
        List<SkillPrefab> finalList = new ArrayList<>();

        finalList.add(SkillPrefab.immutable("venomous_attack", "combat", List.of(2, 3, 4, 5, 7))
                .addCustomValue("success_probability", new ValueSet(List.of(3f, 8f, 14f, 25f, 40f), "skill.tyzs_skills.unit.percentage"))
                .withBehavior(new VenomousAttackEffect())
                .withConfig(new SkillConfiguration(makeTag(tag -> {tag.put("entity_blacklist", new ListTag());})))
                .build());

        finalList.add(SkillPrefab.immutable("critical_hit", "combat", List.of(2, 3, 4, 5, 7))
                .addCustomValue("success_probability", new ValueSet(List.of(5f, 10f, 15f, 20f, 25f), "skill.tyzs_skills.unit.percentage"))
                .withBehavior(new CriticalHitEffect())
                .withConfig(new SkillConfiguration(makeTag(tag -> {tag.put("entity_blacklist", new ListTag());})))
                .build());

        finalList.add(SkillPrefab.immutable("damage_deal", "combat", List.of(2, 3, 4, 5, 7))
                .addCustomValue("success_probability", new ValueSet(List.of(3f, 7f, 11f, 15f, 20f), "skill.tyzs_skills.unit.percentage"))
                .withBehavior(new DamageDealEffect())
                .withConfig(new SkillConfiguration(makeTag(tag -> {tag.put("entity_blacklist", new ListTag());})))
                .build());

        finalList.add(SkillPrefab.immutable("bloodlust", "combat", List.of(5, 8, 12, 18))
                .addCustomValue("health_percentage", new ValueSet(List.of(3f, 7f, 11f, 15f), "skill.tyzs_skills.unit.percentage"))
                .withBehavior(new BloodlustEffect())
                .withConfig(new SkillConfiguration(makeTag(tag -> {tag.put("entity_blacklist", new ListTag());})))
                .build());

        finalList.add(SkillPrefab.immutable("resistance", "combat", List.of(5, 8, 12, 18, 24, 32))
                .addCustomValue("damage_resistance", new ValueSet(List.of(7f, 15f, 20f, 30f, 35f, 45f), "skill.tyzs_skills.unit.percentage"))
                .withBehavior(new ResistanceEffect())
                .build());

        finalList.add(SkillPrefab.immutable("backstab", "combat", List.of(2, 3, 4, 5, 7))
                .addCustomValue("damage_buff", new ValueSet(List.of(5f, 10f, 15f, 20f, 25f), "skill.tyzs_skills.unit.percentage"))
                .withBehavior(new BackstabEffect())
                .withConfig(new SkillConfiguration(makeTag(tag -> {tag.put("entity_blacklist", new ListTag());})))
                .build());

        finalList.add(SkillPrefab.immutable("rage", "combat", List.of(2, 3, 4, 5, 7))
                .addCustomValue("damage_buff", new ValueSet(List.of(10f, 20f, 30f, 40f, 50f), "skill.tyzs_skills.unit.percentage_damage"))
                .addCustomValue("health_threshold", new ValueSet(List.of(10f, 10f, 20f, 20f, 30f), "skill.tyzs_skills.unit.percentage"))
                .withBehavior(new RageEffect())
                .build());

        finalList.add(SkillPrefab.immutable("adrenaline", "combat", List.of(2, 4, 6))
                .addCustomValue("health_threshold", new ValueSet(List.of(10f, 20f, 30f), "skill.tyzs_skills.unit.percentage"))
                .addCustomValue("effect_duration", new ValueSet(List.of(3f, 5f, 8f), "skill.tyzs_skills.unit.seconds"))
                .withBehavior(new AdrenalineEffect())
                .build());

        finalList.add(SkillPrefab.immutable("resilience", "combat", List.of(2, 3, 4, 5, 7, 9, 11, 14, 17))
                .addCustomValue("effect_time_reduction", new ValueSet(List.of(15f, 25f, 35f, 45f, 55f, 65f, 75f, 85f, 95f), "skill.tyzs_skills.unit.percentage"))
                .withBehavior(new ResilienceEffect())
                .build());

        finalList.add(SkillPrefab.immutable("jinxed", "combat", List.of(5, 8, 12))
                .addCustomValue("success_probability", new ValueSet(List.of(3f, 5f, 7f), "skill.tyzs_skills.unit.percentage"))
                .withBehavior(new JinxedEffect())
                .build());

        finalList.add(SkillPrefab.immutable("true_strike", "combat", List.of(10, 15, 20))
                .addCustomValue("success_probability", new ValueSet(List.of(3f, 6f, 9f), "skill.tyzs_skills.unit.percentage"))
                .withBehavior(new TrueStrikeEffect())
                .withConfig(new SkillConfiguration(makeTag(tag -> {tag.put("entity_blacklist", new ListTag());})))
                .build());

        finalList.add(SkillPrefab.immutable("sunder_armor", "combat", List.of(4, 6, 8, 12, 16))
                .addCustomValue("success_probability", new ValueSet(List.of( 3f, 6f, 9f, 12f, 15f), "skill.tyzs_skills.unit.percentage"))
                .addCustomValue("damage_percentage", new ValueSet(List.of(20f, 40f, 60f, 80f, 100f), "skill.tyzs_skills.unit.percentage_damage"))
                .withBehavior(new SunderArmorEffect())
                .build());

        return finalList;
    }

    private static List<SkillPrefab> getMiscSkills(){
        List<SkillPrefab> finalList = new ArrayList<>();

        finalList.add(SkillPrefab.generic("swift_learn", "misc", List.of(8, 12, 18, 24))
                .addModifier(new Modifier("tyzs_skills:skill_xp_multiplier",
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                        List.of(15f, 30f, 45f, 60f),
                        "skill.tyzs_skills.unit.percentage"))
                .build());

        finalList.add(SkillPrefab.immutable("nutrition", "misc", List.of(2, 3, 4, 5, 7))
                .addCustomValue("nutrition_bonus", new ValueSet(List.of(5f, 10f, 15f, 20f, 25f), "skill.tyzs_skills.unit.percentage"))
                .withBehavior(new NutritionEffect())
                .withConfig(new SkillConfiguration(makeTag(tag -> {tag.put("food_blacklist", new ListTag());})))
                .build());

        finalList.add(SkillPrefab.immutable("experience_boost", "misc", List.of(4, 6, 8, 12, 16))
                .addCustomValue("bonus_percentage", new ValueSet(List.of(20f, 40f, 60f, 80f, 100f), "skill.tyzs_skills.unit.percentage"))
                .withBehavior(new ExperienceBoostEffect())
                .build());

        finalList.add(SkillPrefab.immutable("stealth", "misc", List.of(4, 6, 8, 12, 16))
                .addCustomValue("range_reduction", new ValueSet(List.of(10f, 20f, 30f, 40f, 50f), "skill.tyzs_skills.unit.percentage"))
                .withBehavior(new StealthEffect())
                .build());

        finalList.add(SkillPrefab.immutable("magnet", "misc", List.of(4, 6, 8, 12))
                .addCustomValue("block_radius", new ValueSet(List.of(3f, 5f, 7f, 10f), "skill.tyzs_skills.unit.block_radius"))
                .withBehavior(new MagnetEffect())
                .withConfig(new SkillConfiguration(makeTag(tag -> {tag.put("item_blacklist", new ListTag());})))
                .build());

        finalList.add(SkillPrefab.immutable("spare_parts", "misc", List.of(5, 8, 12, 18, 24))
                .addCustomValue("refund_chance", new ValueSet(List.of(4f, 8f, 12f, 16f, 20f), "skill.tyzs_skills.unit.percentage"))
                .addCustomValue("max_materials", new ValueSet(List.of(1f, 1f, 2f, 2f, 3f), "skill.tyzs_skills.unit.item"))
                .withBehavior(new SparePartsEffect())
                .withConfig(new SkillConfiguration(makeTag(tag -> {
                    tag.put("product_blacklist", new ListTag());
                    tag.put("ingredient_blacklist", new ListTag());})))
                .build());

        finalList.add(SkillPrefab.immutable("twist_of_fate", "misc", List.of(4, 6, 8, 12, 16))
                .addCustomValue("loot_multiplier", new ValueSet(List.of(10f, 20f, 30f, 40f, 50f), "skill.tyzs_skills.unit.percentage"))
                .withBehavior(new TwistOfFateEffect())
                .withConfig(new SkillConfiguration(makeTag(tag -> {
                    tag.put("container_blacklist", new ListTag());
                    tag.put("item_blacklist", new ListTag());})))
                .build());

        finalList.add(SkillPrefab.immutable("keepsake", "misc", List.of(4, 6, 8, 12, 16, 20, 25, 30, 35))
                .addCustomValue("saved_slots", new ValueSet(List.of(1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f), "skill.tyzs_skills.unit.slot"))
                .withBehavior(new KeepsakeEffect())
                .withConfig(new SkillConfiguration(makeTag(tag -> {tag.put("item_blacklist", new ListTag());})))
                .build());

        finalList.add(SkillPrefab.immutable("haggler", "misc", List.of(2, 3, 4, 5))
                .addCustomValue("block_radius", new ValueSet(List.of(10f, 15f, 20f, 25f), "skill.tyzs_skills.unit.block_radius"))
                .addCustomValue("villager_speed", new ValueSet(List.of(0f, 5f, 5f, 10f), "skill.tyzs_skills.unit.percentage"))
                .withBehavior(new HagglerEffect())
                .build());

        return finalList;
    }

    private static List<SkillPrefab> getNatureSkills(){
        List<SkillPrefab> finalList = new ArrayList<>();

        finalList.add(SkillPrefab.immutable("green_thumb", "nature", List.of(2, 2, 3, 3, 4))
                .addCustomValue("success_probability", new ValueSet(List.of(15f, 30f, 45f, 60f, 75f), "skill.tyzs_skills.unit.percentage"))
                .withBehavior(new GreenThumbEffect())
                .withConfig(new SkillConfiguration(makeTag(tag -> {tag.put("block_blacklist", new ListTag());})))
                .build());

        finalList.add(SkillPrefab.immutable("shepherd_s_blessing", "nature", List.of(2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9))
                .addCustomValue("growth_speed", new ValueSet(List.of(50f, 100f, 150f, 200f, 250f, 300f, 350f, 400f, 450f, 500f, 550f, 600f, 650f, 700f, 750f), "skill.tyzs_skills.unit.percentage"))
                .withBehavior(new ShepherdsBlessingEffect())
                .withConfig(new SkillConfiguration(makeTag(tag -> {tag.put("entity_blacklist", new ListTag());})))
                .build());

        return finalList;
    }

    //Utils
    private static @NotNull CompoundTag makeTag(Consumer<CompoundTag> populator){
        var specificParameters = new CompoundTag();
        populator.accept(specificParameters);
        return specificParameters;
    }
    private static @NotNull ListTag buildList (List<String> source){
        var tagList = new ListTag();
        for (var entry : source) tagList.add(StringTag.valueOf(entry));
        return tagList;
    }
}
