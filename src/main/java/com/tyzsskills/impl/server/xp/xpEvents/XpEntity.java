package com.tyzsskills.impl.server.xp.xpEvents;

import com.google.gson.JsonObject;
import com.tyzsskills.impl.server.active.AttributeRegistry;
import com.tyzsskills.impl.server.active.ErrorManager;
import com.tyzsskills.impl.server.xp.XpManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.ApiStatus;


import java.util.HashMap;
import java.util.Map;

@ApiStatus.Internal
public class XpEntity {
    public static Map<String, Float> Xpvalues = new HashMap<>();
    public static Map<TagKey<EntityType<?>>, Float> XpTagValues = new HashMap<>();

    public static Map<EntityType<?>, Float> CACHE = new HashMap<>();


    //Setup
    public static void loadValues(JsonObject source){
        for(String categoryKey : source.keySet()){

            JsonObject category = source.getAsJsonObject(categoryKey);

            if(!category.has("xp")){
                ErrorManager.registerLoadError("loading '" + categoryKey + "' in entity xp values", "Missing 'xp' value");
                continue;
            }

            if(!category.has("entities")){
                ErrorManager.registerLoadError("loading '" + categoryKey + "' in entity xp values", "Missing 'entity' list");
                continue;
            }

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
                    catch (Exception ex){
                        System.err.println("TyzSkills: Error when loading tags '" + entry + "' : " + ex.getMessage());
                        ErrorManager.registerLoadError("parsing entity/tag '" + entry + "' in entity xp values", "Invalid ResourceLocation format");
                    }
                }
                else{Xpvalues.put(entry, xpValue);}
            }
        }
    }

    public static void clearValues(){
        Xpvalues.clear();
        XpTagValues.clear();
        CACHE.clear();
    }

    //Actifs
    public static void entityKillProfit(Entity entity, ServerPlayer player){
        var amount = getEntityValue(entity) * (float)player.getAttributeValue(AttributeRegistry.SKILL_XP_MULTIPLIER);
        if(amount > 0) XpManager.addXP(player, amount);
    }


    //Getters
    public static float getEntityValue(Entity entity) {

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
