package com.tyzsskills.impl.server.categories;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tyzsskills.api.records.Category;
import com.tyzsskills.impl.server.active.ErrorManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

public class CategoryLoader {
    private static final List<Category> CATEGORIES = new ArrayList<>();
    private static final List<Category> view = Collections.unmodifiableList(CATEGORIES);
    public static void clearCategories(){CATEGORIES.clear();}

    public static void loadCategories(@NotNull JsonArray array){
        CATEGORIES.clear();

        for (JsonElement element : array) {
            if (!element.isJsonObject()) continue;
            JsonObject categoryData = element.getAsJsonObject();

            try {
                var id = categoryData.has("id") ? categoryData.get("id").getAsString() : "unknown" + CATEGORIES.size();
                var displayName = categoryData.has("displayName") ? categoryData.get("displayName").getAsString() : id;
                var icon = categoryData.has("icon") ? categoryData.get("icon").getAsString() : "minecraft:barrier";
                var order = categoryData.has("order") ? categoryData.get("order").getAsInt() : 99;

                CATEGORIES.add(new Category(id.toLowerCase(), displayName, icon, order));
            } catch (Exception ex) {
                ErrorManager.registerLoadError("parsing category entry", ex.getMessage());
            }
        }
    }

    public static @NotNull @UnmodifiableView List<Category> getCategories(){return view;}
}
