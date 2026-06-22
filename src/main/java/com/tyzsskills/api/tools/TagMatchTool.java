package com.tyzsskills.api.tools;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class TagMatchTool {

    //BLOCKS
    private static final Map<String, Predicate<BlockState>> BLOCK_CACHE = new ConcurrentHashMap<>();

    public static boolean isBlockInList(@NotNull CompoundTag params, @NotNull String listKey, @NotNull BlockState state){
        if(params.contains(listKey, Tag.TAG_LIST)){
            var blackList = params.getList(listKey, Tag.TAG_STRING);
            for (int i = 0; i < blackList.size(); i++){
                if(TagMatchTool.doesBlockMatch(blackList.getString(i), state)) return true;
            }
        }
        return false;
    }

    public static boolean doesBlockMatch(@NotNull String entry, @NotNull BlockState state){
        if(entry.isEmpty()) return false;
        var test = BLOCK_CACHE.computeIfAbsent(entry, TagMatchTool::blockTest);
        return test.test(state);
    }

    private static Predicate<BlockState> blockTest(@NotNull String entry){
        if (entry.startsWith("#")) {
            var tagId = ResourceLocation.tryParse(entry.substring(1));
            if (tagId != null) {
                var tag = TagKey.create(Registries.BLOCK, tagId);
                return state -> state.is(tag);
            }
        } else {
            var blockId = ResourceLocation.tryParse(entry);
            if (blockId != null) {
                var targetBlock = BuiltInRegistries.BLOCK.get(blockId);
                return state -> state.is(targetBlock);
            }
        }

        return state -> false;
    }

    //ENTITIES
    private static final Map<String, Predicate<EntityType<?>>> ENTITY_CACHE = new ConcurrentHashMap<>();

    public static boolean isEntityInList(@NotNull CompoundTag params, @NotNull String listKey, @NotNull EntityType<?> type){
        if(params.contains(listKey, Tag.TAG_LIST)){
            var blackList = params.getList(listKey, Tag.TAG_STRING);
            for (int i = 0; i < blackList.size(); i++){
                if(TagMatchTool.doesEntityMatch(blackList.getString(i), type)) return true;
            }
        }
        return false;
    }

    public static boolean doesEntityMatch(@NotNull String entry, @NotNull EntityType<?> type){
        if(entry.isEmpty()) return false;
        var test = ENTITY_CACHE.computeIfAbsent(entry, TagMatchTool::entityTest);
        return test.test(type);
    }

    private static Predicate<EntityType<?>> entityTest(@NotNull String entry){
        if (entry.startsWith("#")) {
            var tagId = ResourceLocation.tryParse(entry.substring(1));
            if (tagId != null) {
                var tag = TagKey.create(Registries.ENTITY_TYPE, tagId);
                return type -> type.is(tag);
            }
        } else {
            var entityId = ResourceLocation.tryParse(entry);
            if (entityId != null) {
                var targetEntity = BuiltInRegistries.ENTITY_TYPE.get(entityId);
                return type -> type.equals(targetEntity);
            }
        }

        return type -> false;
    }

    //ITEMS
    private static final Map<String, Predicate<ItemStack>> ITEM_CACHE = new ConcurrentHashMap<>();

    public static boolean isItemInList(@NotNull CompoundTag params, @NotNull String listKey, @NotNull ItemStack stack){
        if(params.contains(listKey, Tag.TAG_LIST)){
            var blackList = params.getList(listKey, Tag.TAG_STRING);
            for (int i = 0; i < blackList.size(); i++){
                if(TagMatchTool.doesItemMatch(blackList.getString(i), stack)) return true;
            }
        }
        return false;
    }

    public static boolean doesItemMatch(@NotNull String entry, @NotNull ItemStack stack){
        if(entry.isEmpty()) return false;
        var test = ITEM_CACHE.computeIfAbsent(entry, TagMatchTool::itemTest);
        return test.test(stack);
    }

    private static Predicate<ItemStack> itemTest(@NotNull String entry){
        if (entry.startsWith("#")) {
            var tagId = ResourceLocation.tryParse(entry.substring(1));
            if (tagId != null) {
                var tag = TagKey.create(Registries.ITEM, tagId);
                return stack -> stack.is(tag);
            }
        } else {
            var itemId = ResourceLocation.tryParse(entry);
            if (itemId != null) {
                var targetItem = BuiltInRegistries.ITEM.get(itemId);
                return stack -> stack.is(targetItem);
            }
        }
        return stack -> false;
    }



}
