package com.tyzsskills.impl.server.categories;

import com.google.gson.JsonObject;
import com.tyzsskills.api.model.Category;
import com.tyzsskills.impl.server.active.ErrorManager;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CategoryLoader {
    private static final Map<String, Category> CATEGORIES = new HashMap<>();
    public static void clearCategories(){CATEGORIES.clear();}

    public static void loadCategories(@NotNull JsonObject obj){
        CATEGORIES.clear();

        for (String key : obj.keySet()) {
            try {
                JsonObject categoryData = obj.getAsJsonObject(key);

                var displayName = categoryData.has("displayName") ? categoryData.get("displayName").getAsString() : key;
                var icon = categoryData.has("icon") ? categoryData.get("icon").getAsString() : "minecraft:barrier";
                var order = categoryData.has("order") ? categoryData.get("order").getAsInt() : 99;

                CATEGORIES.put(key, new Category(displayName, icon, order, key));

            } catch (Exception ex) {
                ErrorManager.registerLoadError("parsing category [" + key + "]", "Unable to read file");
            }
        }
    }

    @NotNull
    public static Map<String, Category> getCategories(){return Map.copyOf(CATEGORIES);}
}
