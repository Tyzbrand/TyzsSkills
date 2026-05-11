package com.tyzsskills.impl.server.xp;

import com.google.gson.JsonObject;
import com.tyzsskills.impl.server.active.AttributeRegistry;
import com.tyzsskills.impl.server.active.ErrorManager;
import com.tyzsskills.impl.server.active.FileManager;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class XpGainRegistry {

    private static final Map<String, Float> blockValues = new HashMap<>();
    private static final Map<String, Float> entityValues = new HashMap<>();
    private static final Map<String, Float> foodValues = new HashMap<>();

    private static final Map<TagKey<Block>, Float> blockTagValues = new HashMap<>();
    private static final Map<TagKey<EntityType<?>>, Float> entityTagValues = new HashMap<>();

    private static final Map<Block, Float> blockCacheValues = new HashMap<>();
    private static final Map<EntityType<?>, Float> entityCacheValues = new HashMap<>();
    private static final Map<Item, Float> foodCacheValues = new HashMap<>();

    public static void clearAll(){
        blockValues.clear(); entityValues.clear(); foodValues.clear();
        blockTagValues.clear(); entityTagValues.clear();
        blockCacheValues.clear(); entityCacheValues.clear(); foodCacheValues.clear();
    }

    //LOAD
    public static void loadBlockMap(@NotNull JsonObject obj){
        blockValues.clear(); blockTagValues.clear();
        parseMap(obj, FileManager.BLOCK_VALUES_KEY, blockValues, blockTagValues, Registries.BLOCK);
    }
    public static void loadEntityMap(@NotNull JsonObject obj){
        entityValues.clear(); entityTagValues.clear();
        parseMap(obj, FileManager.ENTITY_VALUES_KEY, entityValues, entityTagValues, Registries.ENTITY_TYPE);
    }
    public static void loadFoodMap(@NotNull JsonObject obj){
        foodValues.clear();
        parseMap(obj, FileManager.FOOD_VALUES_KEY, foodValues, null, null);
    }

    //GETTERS
    public static float getBlockValue(@NotNull BlockState state, @NotNull ServerPlayer player){
        var block = state.getBlock();

        if(state.getBlock() instanceof CropBlock crop && !crop.isMaxAge(state)) return 0f;
        else if (state.getBlock() instanceof NetherWartBlock && state.getValue(NetherWartBlock.AGE) < 3) return 0f;

        if(blockCacheValues.containsKey(block)){return blockCacheValues.get(block);}

        var blockID = BuiltInRegistries.BLOCK.getKey(block).toString();
        var cacheValue = 0f;

        if(blockValues.containsKey(blockID)) {cacheValue = blockValues.get(blockID);}
        else{
            for(var entry : blockTagValues.entrySet()){
                if(state.is(entry.getKey())){cacheValue = entry.getValue(); break;}
            }
        }

        blockCacheValues.put(block, cacheValue);
        return cacheValue * (float)player.getAttributeValue(AttributeRegistry.SKILL_XP_MULTIPLIER);
    }

    public static float getEntityValue(@NotNull Entity entity, @NotNull ServerPlayer player){
        var type = entity.getType();
        if(entityCacheValues.containsKey(type)){return entityCacheValues.get(type);}

        var entityID = BuiltInRegistries.ENTITY_TYPE.getKey(type).toString();
        var cacheValue = 0f;

        if(entityValues.containsKey(entityID)) {cacheValue = entityValues.get(entityID);}
        else{
            for(var entry : entityTagValues.entrySet()){
                if(type.is(entry.getKey())){cacheValue = entry.getValue(); break;}
            }
        }

        entityCacheValues.put(type, cacheValue);
        return cacheValue * (float)player.getAttributeValue(AttributeRegistry.SKILL_XP_MULTIPLIER);
    }

    public static float getFoodValue(@NotNull ItemStack itemStack, @NotNull ServerPlayer player){
        var item = itemStack.getItem();

        if(foodCacheValues.containsKey(item)){return foodCacheValues.get(item);}

        var itemId = BuiltInRegistries.ITEM.getKey(item).toString();
        var cacheValue = 0f;

        if(foodValues.containsKey(itemId)) {cacheValue = foodValues.get(itemId);}
        else {
            var itemStats = item.getFoodProperties(itemStack, player);
            if(itemStats == null) return 1f;

            var itemNutrition = itemStats.nutrition();
            var itemSaturation = itemStats.saturation();

            cacheValue = (itemNutrition + itemSaturation);
        }

        foodCacheValues.put(item, cacheValue);
        return cacheValue * (float)player.getAttributeValue(AttributeRegistry.SKILL_XP_MULTIPLIER);
    }

    //UTILS
    private static <T> void parseMap(
            @NotNull JsonObject obj, @NotNull String filename,
            @NotNull Map<String, Float> valueMap,
            Map<TagKey<T>, Float> tagMap,
            ResourceKey<? extends Registry<T>> registryKey) {


        for (String categoryKey : obj.keySet()) {
            JsonObject category = obj.getAsJsonObject(categoryKey);

            if (!category.has("xp")) {
                ErrorManager.registerLoadError("loading '" + categoryKey + "[" + filename + "]", "Missing XP value");
                continue;
            }

            if (!category.has("id")) {
                ErrorManager.registerLoadError("loading '" + categoryKey + "[" + filename + "]", "Missing ID list");
                continue;
            }

            float xpValue = category.get("xp").getAsFloat();
            for (var iteration : category.getAsJsonArray("id")) {

                String entry = iteration.getAsString();
                if (entry.startsWith("#") && tagMap != null && registryKey != null) {
                    String tag = entry.substring(1);
                    try {
                        var location = ResourceLocation.parse(tag);
                        var tagKey = TagKey.create(registryKey, location);
                        tagMap.put(tagKey, xpValue);
                    } catch (Exception ex) {
                        ErrorManager.registerLoadError("parsing on [" + filename + "]: " + entry + "' in xp values", "Invalid ResourceLocation format");
                    }
                } else {
                    valueMap.put(entry, xpValue);
                }
            }
        }
    }
 }

