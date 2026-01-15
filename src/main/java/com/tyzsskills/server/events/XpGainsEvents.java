package com.tyzsskills.server.events;

import com.tyzsskills.Config;
import com.tyzsskills.server.active.AttributeRegistry;
import com.tyzsskills.server.attachments.BlockMarker;
import com.tyzsskills.server.xp.XpManager;
import com.tyzsskills.server.xp.xpEvents.XpBlock;
import com.tyzsskills.server.xp.xpEvents.XpEntity;
import com.tyzsskills.server.xp.xpEvents.XpFood;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

public class XpGainsEvents {

    //Xp gains
    @SubscribeEvent
    public static void OnBlockBreak(BlockEvent.BreakEvent event){
        if(event.isCanceled()) return;

        if(event.getPlayer() instanceof ServerPlayer serverPlayer){
            if(Config.PREVENT_PLACED_BLOCK_XP.get()) {
                Level level = (net.minecraft.world.level.Level) event.getLevel();
                var pos = event.getPos();

                if (BlockMarker.IsPlayerPlaced(level, pos)) {
                    BlockMarker.RemoveBlock(level, pos);
                    return;
                }
            }
            XpBlock.BlockBreakProfit(event.getState(), serverPlayer);
        }
    }

    @SubscribeEvent
    public static void OnEntityDeath(LivingDeathEvent event){

        if(event.isCanceled()) return;

        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;
        XpEntity.EntityKillProfit(event.getEntity(), player);
    }

    @SubscribeEvent
    public static void OnEntityFish(ItemFishedEvent event){
        float value = (float)Config.FISHING_XP_VALUE.getAsDouble();
        if(value <= 0) return;

        if(event.isCanceled()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        value *= (float)player.getAttributeValue(AttributeRegistry.SKILL_XP_MULTIPLIER);
        XpManager.AddXP(player, value);
    }

    @SubscribeEvent
    public static void OnEntityEat(LivingEntityUseItemEvent.Finish event){
        if(!Config.EARN_XP_BY_EATING.get()) return;

        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if(event.getItem().getFoodProperties(player) == null) return;

        XpFood.FoodEatProfit(event.getItem(), player);
    }

    @SubscribeEvent
    public static void OnItemCrafted(PlayerEvent.ItemCraftedEvent event){
        float value = (float)Config.CRAFTING_XP_VALUE.getAsDouble();
        if(value <= 0) return;

        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        value *= (float)player.getAttributeValue(AttributeRegistry.SKILL_XP_MULTIPLIER);
        XpManager.AddXP(player, value);
    }

    @SubscribeEvent
    public static void OnBabySpawn(BabyEntitySpawnEvent event){
        float value = (float)Config.BREEDING_XP_VALUE.getAsDouble();
        if(value <= 0) return;

        if (!(event.getCausedByPlayer() instanceof ServerPlayer player)) return;
        value *= (float)player.getAttributeValue(AttributeRegistry.SKILL_XP_MULTIPLIER);
        XpManager.AddXP(player, value);
    }

    @SubscribeEvent
    public static void OnAdvancement(AdvancementEvent.AdvancementEarnEvent event) {
        float taskValue = (float)Config.ADVANCEMENT_TASK_XP_VALUE.getAsDouble();
        float goalValue = (float)Config.ADVANCEMENT_GOAL_XP_VALUE.getAsDouble();
        float challengeValue = (float)Config.ADVANCEMENT_CHALLENGE_XP_VALUE.getAsDouble();

        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (event.getAdvancement().id().toString().startsWith("minecraft:recipes/")) return;

        event.getAdvancement().value().display().ifPresent(info -> {
            float finalValue = switch (info.getType()) {
                case TASK -> taskValue;
                case GOAL -> goalValue;
                case CHALLENGE -> challengeValue;
            };

            if(finalValue <= 0) return;

            finalValue *= (float)player.getAttributeValue(AttributeRegistry.SKILL_XP_MULTIPLIER);
            XpManager.AddXP(player, finalValue);
        });
    }
}
