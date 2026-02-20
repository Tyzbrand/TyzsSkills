package com.tyzsskills.server.xp.xpEvents;

import com.google.gson.JsonObject;
import com.tyzsskills.server.active.AttributeRegistry;
import com.tyzsskills.server.active.ErrorManager;
import com.tyzsskills.server.xp.XpManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class XpFood {

    private static final Map<String, Float> xpValues = new HashMap<>();

    private static final Map<Item, Float> CACHE = new HashMap<>();

    public static void LoadValues(JsonObject source){
        for(String categoryKey : source.keySet()){

            JsonObject category = source.getAsJsonObject(categoryKey);

            if(!category.has("xp")){
                ErrorManager.RegisterLoadError("loading '" + categoryKey + "' in food xp values", "Missing 'xp' value");
                continue;
            }

            if(!category.has("food")){
                ErrorManager.RegisterLoadError("loading '" + categoryKey + "' in food xp values", "Missing 'food' list");
                continue;
            }

            float xpValue = category.get("xp").getAsFloat();

            for (var iteration : category.getAsJsonArray("food")){
                String entry = iteration.getAsString();
                xpValues.put(entry, xpValue);
            }
        }
    }

    public static void ClearValues(){
        xpValues.clear();
        CACHE.clear();
    }

    //Actifs
    public static void FoodEatProfit(ItemStack itemStack, ServerPlayer player){
        var amount = GetFoodValue(itemStack, player) * (float)player.getAttributeValue(AttributeRegistry.SKILL_XP_MULTIPLIER);
        if(amount > 0) XpManager.addXP(player, amount);
    }



    //Getters
    public static float GetFoodValue(ItemStack itemStack, ServerPlayer player) {

        var item = itemStack.getItem();

        if(CACHE.containsKey(item)){return CACHE.get(item);}

        var itemId = BuiltInRegistries.ITEM.getKey(item).toString();
        var cacheValue = 0f;

        if(xpValues.containsKey(itemId)) {cacheValue = xpValues.get(itemId);}
        else {
            var itemStats = item.getFoodProperties(itemStack, player);
            if(itemStats == null) return 1f;

            var itemNutrition = itemStats.nutrition();
            var itemSaturation = itemStats.saturation();

            cacheValue = (itemNutrition + itemSaturation);

        }

        CACHE.put(item, cacheValue);
        return cacheValue;
    }


}
