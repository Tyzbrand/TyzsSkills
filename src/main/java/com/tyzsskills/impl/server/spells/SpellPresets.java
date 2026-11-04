package com.tyzsskills.impl.server.spells;

import com.tyzsskills.api.records.SpellPrefab;
import com.tyzsskills.api.records.SpellProperty;
import com.tyzsskills.impl.server.effects.spellEffects.DashEffect;

import java.util.List;



public class SpellPresets {
    public static List<SpellPrefab> getDefaultSpells(){
        return List.of(DASH, JUMP, HEAL, DAMAGE, RADAR);
    }


    private static final SpellPrefab DASH =
            SpellPrefab.builder("dash", 10)
                    .withIcon("minecraft:textures/item/elytra.png")
                    .withBehavior(new DashEffect())
                    .addProperty(new SpellProperty("cooldown", List.of(5, 4, 3), 8, List.of(6f, 3f, 2f), "Cooldown", "unknown", "sec"))
                    .build();

    private static final SpellPrefab JUMP =
            SpellPrefab.builder("jump", 10)
                    .withIcon("minecraft:textures/item/rabbit_foot.png")
                    .addProperty(new SpellProperty("cooldown", List.of(5, 4, 3), 8, List.of(6f, 3f, 2f), "Cooldown", "unknown", "sec"))
                    .build();

    private static final SpellPrefab HEAL =
            SpellPrefab.builder("heal", 10)
                    .withIcon("minecraft:textures/item/golden_apple.png")
                    .addProperty(new SpellProperty("cooldown", List.of(5, 4, 3), 8, List.of(6f, 3f, 2f), "Cooldown", "unknown", "sec"))
                    .build();

    private static final SpellPrefab DAMAGE =
            SpellPrefab.builder("damage", 10)
                    .withIcon("minecraft:textures/item/arrow.png")
                    .addProperty(new SpellProperty("cooldown", List.of(5, 4, 3), 8, List.of(6f, 3f, 2f), "Cooldown", "unknown", "sec"))
                    .build();

    private static final SpellPrefab RADAR =
            SpellPrefab.builder("radar", 10)
                    .addProperty(new SpellProperty("cooldown", List.of(5, 4, 3), 8, List.of(6f, 3f, 2f), "Cooldown", "unknown", "sec"))
                    .build();


}
