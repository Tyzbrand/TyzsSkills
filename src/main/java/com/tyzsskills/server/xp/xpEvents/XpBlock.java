package com.tyzsskills.server.xp.xpEvents;

import com.google.gson.JsonObject;
import com.tyzsskills.server.xp.XpManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XpBlock {
    public static Map<String, Float> Xpvalues = new HashMap<>();
    public static Map<TagKey<Block>, Float> XpTagValues = new HashMap<>();

    public static Map<Block, Float> CACHE = new HashMap<>();


    //Setup
    public static void LoadValues(JsonObject source){
        for(String categoryKey : source.keySet()){

            JsonObject category = source.getAsJsonObject(categoryKey);
            if(!category.has("xp") || !category.has("blocks")) continue;

            float xpValue = category.get("xp").getAsFloat();
            for (var iteration : category.getAsJsonArray("blocks")){

                String entry = iteration.getAsString();
                if(entry.startsWith("#")){
                    String tag = entry.substring(1);
                    try{
                        var location = ResourceLocation.parse(tag);
                        var tagKey = TagKey.create(Registries.BLOCK, location);
                        XpTagValues.put(tagKey, xpValue);
                    }
                    catch (Exception ex){System.err.println("TyzSkills: Error when loading tags '" + entry + "' : " + ex.getMessage());}
                }
                else{Xpvalues.put(entry, xpValue);}
            }
        }
    }

    public static void ClearValues(){
        Xpvalues.clear();
        XpTagValues.clear();
        CACHE.clear();
    }

    //Actifs
    public static void BlockBreakProfit(BlockState state, ServerPlayer player){
        var amount = GetBlockValue(state);
        if(amount > 0) XpManager.AddXP(player, amount);
    }



    //Getters
    public static boolean AreValuesLoaded() {return !Xpvalues.isEmpty() || !XpTagValues.isEmpty();}

    public static float GetBlockValue(BlockState state) {

        var block = state.getBlock();

        if(CACHE.containsKey(block)){return CACHE.get(block);}

        var blockID = BuiltInRegistries.BLOCK.getKey(block).toString();
        var cacheValue = 0f;

        if(Xpvalues.containsKey(blockID)) {cacheValue = Xpvalues.get(blockID);}
        else{
            for(var entry : XpTagValues.entrySet()){
                if(state.is(entry.getKey())){cacheValue = entry.getValue(); break;}
            }
        }

        CACHE.put(block, cacheValue);
        return cacheValue;
    }

}
