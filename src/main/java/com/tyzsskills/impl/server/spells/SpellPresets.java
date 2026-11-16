package com.tyzsskills.impl.server.spells;

import com.tyzsskills.api.records.SpellPrefab;
import com.tyzsskills.api.records.SpellProperty;
import com.tyzsskills.impl.server.effects.spellEffects.*;

import java.util.List;


public class SpellPresets {
    public static List<SpellPrefab> getDefaultSpells(){
        return List.of(DASH, VOID_LINK, RETREAT, REWIND, FORESIGHT);
    }


    private static final SpellPrefab DASH =
            SpellPrefab.builder("dash", 10)
                    .withIcon("minecraft:textures/item/elytra.png")
                    .withBehavior(new DashEffect())
                    .addProperty(new SpellProperty(
                            "cooldown",
                            List.of(1, 2, 3),
                            12f, List.of(8f, 6f, 4f),
                            "property.tyzs_skills.cooldown.displayName",
                            "property.tyzs_skills.cooldown.description",
                            "skill.tyzs_skills.unit.seconds"))
                    .addProperty(new SpellProperty(
                            "dash_speed",
                            List.of(1, 2, 3),
                            1,
                            List.of(1.25f, 1.75f, 2.5f),
                            "property.tyzs_skills.dash_speed.displayName",
                            "property.tyzs_skills.dash_speed.description",
                            "skill.tyzs_skills.unit.power"))
                    .build();

    private static final SpellPrefab VOID_LINK =
            SpellPrefab.builder("void_link", 10)
                    .withIcon("minecraft:textures/item/ender_pearl.png")
                    .withBehavior(new VoidLinkEffect())
                    .addProperty(new SpellProperty(
                            "cooldown",
                            List.of(1, 2, 3),
                            18f, List.of(15f, 12f, 9f),
                            "property.tyzs_skills.cooldown.displayName",
                            "property.tyzs_skills.cooldown.description",
                            "skill.tyzs_skills.unit.seconds"))
                    .build();

    private static final SpellPrefab RETREAT =
            SpellPrefab.builder("retreat", 10)
                    .withBehavior(new RetreatEffect())
                    .addProperty(new SpellProperty(
                            "cooldown",
                            List.of(1, 2),
                            60f, List.of(45f, 30f),
                            "property.tyzs_skills.cooldown.displayName",
                            "property.tyzs_skills.cooldown.description",
                            "skill.tyzs_skills.unit.seconds"))
                    .addProperty(new SpellProperty(
                            "teleportation_delay",
                            List.of(1, 2),
                            11f, List.of(8f, 5f),
                            "property.tyzs_skills.teleportation_delay.displayName",
                            "property.tyzs_skills.teleportation_delay.description",
                            "skill.tyzs_skills.unit.seconds"))
                    .build();

    private static final SpellPrefab REWIND =
            SpellPrefab.builder("rewind", 10)
                    .withBehavior(new RewindEffect())
                    .addProperty(new SpellProperty(
                            "cooldown",
                            List.of(1, 2, 3),
                            60f, List.of(45f, 30f, 1f),
                            "property.tyzs_skills.cooldown.displayName",
                            "property.tyzs_skills.cooldown.description",
                            "skill.tyzs_skills.unit.seconds"))
                    .addProperty(new SpellProperty(
                            "teleportation_delay",
                            List.of(1, 2),
                            5f, List.of(10f, 15f),
                            "property.tyzs_skills.teleportation_delay.displayName",
                            "property.tyzs_skills.teleportation_delay.description",
                            "skill.tyzs_skills.unit.seconds"))
                    .build();

    private static final SpellPrefab FORESIGHT =
            SpellPrefab.builder("foresight", 10)
                    .withBehavior(new ForesightEffect())
                    .addProperty(new SpellProperty(
                            "cooldown",
                            List.of(1, 2),
                            25f, List.of(17f, 10f),
                            "property.tyzs_skills.cooldown.displayName",
                            "property.tyzs_skills.cooldown.description",
                            "skill.tyzs_skills.unit.seconds"))
                    .addProperty(new SpellProperty(
                            "effect_duration",
                            List.of(1, 2, 3),
                            3f, List.of(5f, 7f, 10f),
                            "property.tyzs_skills.foresight_glow_duration.displayName",
                            "property.tyzs_skills.foresight_glow_duration.description",
                            "skill.tyzs_skills.unit.seconds"))
                    .addProperty(new SpellProperty(
                            "block_radius",
                            List.of(1, 2, 3, 4, 5),
                            5f, List.of(8f, 11f, 14f, 17f, 20f),
                            "property.tyzs_skills.foresight_radius.displayName",
                            "property.tyzs_skills.foresight_radius.description",
                            "skill.tyzs_skills.unit.block_radius"))
                    .build();
}
