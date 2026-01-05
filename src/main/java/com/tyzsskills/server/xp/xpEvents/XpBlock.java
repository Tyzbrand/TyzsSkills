package com.tyzsskills.server.xp.xpEvents;

import com.google.gson.JsonObject;
import com.tyzsskills.server.xp.XpManager;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XpBlock {
    public static Map<String, Float> Xpvalues = new HashMap<>();


    //Setup
    public static void LoadValues(JsonObject source){
        for(String categoryKey : source.keySet()){

            JsonObject category = source.getAsJsonObject(categoryKey);
            if(!category.has("xp") || !category.has("blocks")) continue;

            float xpValue = category.get("xp").getAsFloat();
            for (var iteration : category.getAsJsonArray("blocks")){
                Xpvalues.put(iteration.getAsString(), xpValue);
            }
        }
    }

    public static void ClearValues(){
        Xpvalues.clear();
    }

    //Actifs
    public static void BlockBreakProfit(String blockID, ServerPlayer player){
        var amount = GetBlockValue(blockID);

        if(amount > 0) XpManager.AddXP(player, amount);
    }



    //Getters
    public static boolean AreValuesLoaded() {return !Xpvalues.isEmpty();}
    public static float GetBlockValue(String id) {return Xpvalues.getOrDefault(id, 0f);}

}
