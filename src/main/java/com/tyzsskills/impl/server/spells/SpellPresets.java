package com.tyzsskills.impl.server.spells;

import com.tyzsskills.api.records.SpellPrefab;
import com.tyzsskills.api.records.SpellProperty;
import com.tyzsskills.impl.server.effects.spellEffects.DashEffect;
import com.tyzsskills.impl.server.effects.spellEffects.WirelessEffect;

import java.util.List;



public class SpellPresets {
    public static List<SpellPrefab> getDefaultSpells(){
        return List.of(DASH, WIRELESS, TEMP1, TEMP2, TEMP3, TEMP4, TEMP5, TEMP6, TEMP7);
    }


    private static final SpellPrefab DASH =
            SpellPrefab.builder("dash", 10)
                    .withIcon("minecraft:textures/item/elytra.png")
                    .withBehavior(new DashEffect())
                    .withDisplayName("Dash")
                    .withDescription("Missing")
                    .addProperty(new SpellProperty("cooldown", List.of(3, 4, 5), 8, List.of(6f, 3f, 2f), "Cooldown", "Reduit le temps d'attente entre chaque utilisation", "sec"))
                    .addProperty(new SpellProperty("vitesse", List.of(3, 4, 5), 8, List.of(6f, 3f, 2f), "Vitesse", "Augmente la vitesse de dash", "m.s"))
                    .addProperty(new SpellProperty("distance", List.of(3, 4, 5), 8, List.of(6f, 3f, 2f), "Distance", "Augmente la distance de dash", "block(s)"))
                    .build();

    private static final SpellPrefab WIRELESS =
            SpellPrefab.builder("wireless", 10)
                    .withIcon("minecraft:textures/item/ender_pearl.png")
                    .withBehavior(new WirelessEffect())
                    .addProperty(new SpellProperty("cooldown", List.of(5, 4, 3), 8, List.of(6f, 3f, 2f), "Cooldown", "unknown", "sec"))
                    .build();

    private static final SpellPrefab TEMP1 =
            SpellPrefab.builder("temp1", 10)
                    .withIcon("minecraft:textures/item/stone_sword.png")
                    .withBehavior(new DashEffect())
                    .addProperty(new SpellProperty("cooldown", List.of(5, 4, 3), 8, List.of(6f, 3f, 2f), "Cooldown", "unknown", "sec"))
                    .build();

    private static final SpellPrefab TEMP2 =
            SpellPrefab.builder("temp2", 10)
                    .withIcon("minecraft:textures/item/stone_shovel.png")
                    .withBehavior(new DashEffect())
                    .addProperty(new SpellProperty("cooldown", List.of(5, 4, 3), 8, List.of(6f, 3f, 2f), "Cooldown", "unknown", "sec"))
                    .build();

    private static final SpellPrefab TEMP3 =
            SpellPrefab.builder("temp3", 10)
                    .withIcon("minecraft:textures/item/stone_pickaxe.png")
                    .withBehavior(new DashEffect())
                    .addProperty(new SpellProperty("cooldown", List.of(5, 4, 3), 8, List.of(6f, 3f, 2f), "Cooldown", "unknown", "sec"))
                    .build();

    private static final SpellPrefab TEMP4 =
            SpellPrefab.builder("temp4", 10)
                    .withIcon("minecraft:textures/item/stone_axe.png")
                    .withBehavior(new DashEffect())
                    .addProperty(new SpellProperty("cooldown", List.of(5, 4, 3), 8, List.of(6f, 3f, 2f), "Cooldown", "unknown", "sec"))
                    .build();

    private static final SpellPrefab TEMP5 =
            SpellPrefab.builder("temp5", 10)
                    .withIcon("minecraft:textures/item/diamond.png")
                    .withBehavior(new DashEffect())
                    .addProperty(new SpellProperty("cooldown", List.of(5, 4, 3), 8, List.of(6f, 3f, 2f), "Cooldown", "unknown", "sec"))
                    .build();

    private static final SpellPrefab TEMP6 =
            SpellPrefab.builder("temp6", 10)
                    .withIcon("minecraft:textures/item/emerald.png")
                    .withBehavior(new DashEffect())
                    .addProperty(new SpellProperty("cooldown", List.of(5, 4, 3), 8, List.of(6f, 3f, 2f), "Cooldown", "unknown", "sec"))
                    .build();

    private static final SpellPrefab TEMP7 =
            SpellPrefab.builder("temp7", 10)
                    .withIcon("minecraft:textures/item/iron_ingot.png")
                    .withBehavior(new DashEffect())
                    .addProperty(new SpellProperty("cooldown", List.of(5, 4, 3), 8, List.of(6f, 3f, 2f), "Cooldown", "unknown", "sec"))
                    .build();

}
