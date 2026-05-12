package com.tyzsskills.impl.server.categories;

import com.tyzsskills.api.model.Category;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class CategoryPreset {

    @NotNull
    public static Map<String, Category> getCategoryPreset(){
        var map = new HashMap<String, Category>();

        map.put("abilities", new Category("Abilities", "minecraft:textures/item/iron_boots.png", 1));
        map.put("combat", new Category("Combat", "minecraft:textures/item/netherite_sword.png", 2));
        map.put("misc", new Category("Misc", "minecraft:textures/item/water_bucket.png", 3));

        return map;
    }
}
