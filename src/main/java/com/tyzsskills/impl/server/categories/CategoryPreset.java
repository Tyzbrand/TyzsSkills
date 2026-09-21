package com.tyzsskills.impl.server.categories;

import com.tyzsskills.api.records.Category;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CategoryPreset {

    @NotNull
    public static List<Category> getCategoryPreset(){
        var list = new ArrayList<Category>();

        list.add(new Category("all", "gui.tyzs_skills.Tab.all", "tyzs_skills:textures/gui/icons/all_icon.png", 0));
        list.add(new Category("abilities", "gui.tyzs_skills.Tab.abilities", "minecraft:textures/item/iron_boots.png", 1));
        list.add(new Category("combat", "gui.tyzs_skills.Tab.fight", "minecraft:textures/item/netherite_sword.png", 2));
        list.add(new Category("misc", "gui.tyzs_skills.Tab.misc", "minecraft:textures/item/water_bucket.png", 3));
        list.add(new Category("bookmarks", "gui.tyzs_skills.Tab.bookmarks", "tyzs_skills:textures/gui/icons/bookmark_icon.png", 4));

        return list;
    }
}
