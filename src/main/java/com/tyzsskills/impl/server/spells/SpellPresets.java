package com.tyzsskills.impl.server.spells;

import com.tyzsskills.api.records.SpellPrefab;
import com.tyzsskills.api.records.SpellProperty;
import com.tyzsskills.impl.server.effects.spellEffects.DashEffect;
import com.tyzsskills.impl.server.effects.spellEffects.WirelessEffect;

import java.util.List;



public class SpellPresets {
    public static List<SpellPrefab> getDefaultSpells(){
        return List.of(DASH, WIRELESS);
    }


    private static final SpellPrefab DASH =
            SpellPrefab.builder("dash", 10)
                    .withIcon("minecraft:textures/item/elytra.png")
                    .withBehavior(new DashEffect())
                    .addProperty(new SpellProperty("cooldown", List.of(5, 4, 3), 8, List.of(6f, 3f, 2f), "Cooldown", "unknown", "sec"))
                    .build();

    private static final SpellPrefab WIRELESS =
            SpellPrefab.builder("wireless", 10)
                    .withIcon("minecraft:textures/item/ender_pearl.png")
                    .withBehavior(new WirelessEffect())
                    .addProperty(new SpellProperty("cooldown", List.of(5, 4, 3), 8, List.of(6f, 3f, 2f), "Cooldown", "unknown", "sec"))
                    .build();
}
