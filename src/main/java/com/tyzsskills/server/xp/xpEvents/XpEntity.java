package com.tyzsskills.server.xp.xpEvents;

import com.google.gson.JsonObject;
import com.tyzsskills.server.active.AttributeRegistry;
import com.tyzsskills.server.xp.XpManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;


import java.util.HashMap;
import java.util.Map;

public class XpEntity {
    public static Map<String, Float> Xpvalues = new HashMap<>();
    public static Map<TagKey<EntityType<?>>, Float> XpTagValues = new HashMap<>();

    public static Map<EntityType<?>, Float> CACHE = new HashMap<>();


    //Setup
    public static void LoadValues(JsonObject source){
        for(String categoryKey : source.keySet()){

            JsonObject category = source.getAsJsonObject(categoryKey);
            if(!category.has("xp") || !category.has("entities")) continue;

            float xpValue = category.get("xp").getAsFloat();
            for (var iteration : category.getAsJsonArray("entities")){

                String entry = iteration.getAsString();
                if(entry.startsWith("#")){
                    String tag = entry.substring(1);
                    try{
                        var location = ResourceLocation.parse(tag);
                        var tagKey = TagKey.create(Registries.ENTITY_TYPE, location);
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
    public static void EntityKillProfit(Entity entity, ServerPlayer player){
        var amount = GetEntityValue(entity) * (float)player.getAttributeValue(AttributeRegistry.SKILL_XP_MULTIPLIER);
        if(amount > 0) XpManager.AddXP(player, amount);
    }


    //Getters
    public static boolean AreValuesLoaded() {return !Xpvalues.isEmpty() || !XpTagValues.isEmpty();}

    public static float GetEntityValue(Entity entity) {

        var type = entity.getType();

        if(CACHE.containsKey(type)){return CACHE.get(type);}

        var entityID = BuiltInRegistries.ENTITY_TYPE.getKey(type).toString();
        var cacheValue = 0f;

        if(Xpvalues.containsKey(entityID)) {cacheValue = Xpvalues.get(entityID);}
        else{
            for(var entry : XpTagValues.entrySet()){
                if(type.is(entry.getKey())){cacheValue = entry.getValue(); break;}
            }
        }

        CACHE.put(type, cacheValue);
        return cacheValue;
    }

}
