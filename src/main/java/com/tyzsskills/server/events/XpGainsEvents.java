package com.tyzsskills.server.events;

import com.tyzsskills.Config;
import com.tyzsskills.server.active.AttributeRegistry;
import com.tyzsskills.server.attachments.BlockMarker;
import com.tyzsskills.server.attachments.ExplorationProgression;
import com.tyzsskills.server.xp.XpManager;
import com.tyzsskills.server.xp.xpEvents.XpBlock;
import com.tyzsskills.server.xp.xpEvents.XpEntity;
import com.tyzsskills.server.xp.xpEvents.XpFood;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class XpGainsEvents {

    //Xp gains
    @SubscribeEvent (priority = EventPriority.LOWEST)
    public static void OnBlockBreak(BlockEvent.BreakEvent event){
        if(event.isCanceled()) return;
        if(!(event.getPlayer() instanceof ServerPlayer player)) return;
        if(player.isCreative() && !Config.EARN_XP_IN_CREATIVE.get()) return;

        Level level = (net.minecraft.world.level.Level) event.getLevel();
        var pos = event.getPos();
        var playerPlaced = BlockMarker.IsPlayerPlaced(level, pos);

        if(playerPlaced) BlockMarker.RemoveBlock(level, pos);

        if(Config.PREVENT_PLACED_BLOCK_XP.get() && playerPlaced) return;
        XpBlock.BlockBreakProfit(event.getState(), player);
    }

    @SubscribeEvent
    public static void OnEntityDeath(LivingDeathEvent event){
        if(event.isCanceled()) return;
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;
        if(player.isCreative() && !Config.EARN_XP_IN_CREATIVE.get()) return;

        XpEntity.EntityKillProfit(event.getEntity(), player);
    }

    @SubscribeEvent
    public static void OnEntityWakeup(PlayerWakeUpEvent event){
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if(!(player.level() instanceof ServerLevel level)) return;
        if(player.isCreative() && !Config.EARN_XP_IN_CREATIVE.get()) return;

        long timeOfDay = level.getDayTime() % 24000L;
        if (timeOfDay > 1000L) return;

        double sleepValue = Config.SLEEPING_XP_VALUE.get();
        if(sleepValue <= 0) return;

        long currentDay = player.level().getDayTime() / 24000L;

        var data = player.getData(ExplorationProgression.DATA);

        if(!data.hasAlreadySlept(currentDay)){
            data.setSleepDay(currentDay);
            sleepValue *= player.getAttributeValue(AttributeRegistry.SKILL_XP_MULTIPLIER);
            XpManager.addXP(player, (float)sleepValue);
        }
    }

    @SubscribeEvent
    public static void OnEntityFish(ItemFishedEvent event){
        if(event.isCanceled()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if(player.isCreative() && !Config.EARN_XP_IN_CREATIVE.get()) return;

        float value = (float)Config.FISHING_XP_VALUE.getAsDouble();
        if(value <= 0) return;

        value *= (float)player.getAttributeValue(AttributeRegistry.SKILL_XP_MULTIPLIER);
        XpManager.addXP(player, value);
    }

    @SubscribeEvent
    public static void OnEntityEat(LivingEntityUseItemEvent.Finish event){
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if(player.isCreative() && !Config.EARN_XP_IN_CREATIVE.get()) return;
        if(!Config.EARN_XP_BY_EATING.get()) return;
        if(event.getItem().getFoodProperties(player) == null) return;


        XpFood.FoodEatProfit(event.getItem(), player);
    }

    @SubscribeEvent
    public static void OnItemCrafted(PlayerEvent.ItemCraftedEvent event){
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if(player.isCreative() && !Config.EARN_XP_IN_CREATIVE.get()) return;

        float value = (float)Config.CRAFTING_XP_VALUE.getAsDouble();
        if(value <= 0) return;

        value *= (float)player.getAttributeValue(AttributeRegistry.SKILL_XP_MULTIPLIER);
        XpManager.addXP(player, value);
    }

    @SubscribeEvent
    public static void OnBabySpawn(BabyEntitySpawnEvent event){
        if (!(event.getCausedByPlayer() instanceof ServerPlayer player)) return;
        if(player.isCreative() && !Config.EARN_XP_IN_CREATIVE.get()) return;

        float value = (float)Config.BREEDING_XP_VALUE.getAsDouble();
        if(value <= 0) return;

        value *= (float)player.getAttributeValue(AttributeRegistry.SKILL_XP_MULTIPLIER);
        XpManager.addXP(player, value);
    }

    @SubscribeEvent
    public static void OnAdvancement(AdvancementEvent.AdvancementEarnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if(player.isCreative() && !Config.EARN_XP_IN_CREATIVE.get()) return;
        if (event.getAdvancement().id().toString().startsWith("minecraft:recipes/")) return;

        float taskValue = (float)Config.ADVANCEMENT_TASK_XP_VALUE.getAsDouble();
        float goalValue = (float)Config.ADVANCEMENT_GOAL_XP_VALUE.getAsDouble();
        float challengeValue = (float)Config.ADVANCEMENT_CHALLENGE_XP_VALUE.getAsDouble();

        event.getAdvancement().value().display().ifPresent(info -> {
            float finalValue = switch (info.getType()) {
                case TASK -> taskValue;
                case GOAL -> goalValue;
                case CHALLENGE -> challengeValue;
            };

            if(finalValue <= 0) return;

            finalValue *= (float)player.getAttributeValue(AttributeRegistry.SKILL_XP_MULTIPLIER);
            XpManager.addXP(player, finalValue);
        });
    }


    @SubscribeEvent
    public static void OnPlayerTick(PlayerTickEvent.Post event){
        if (event.getEntity().tickCount % 100 != 0) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if(player.isCreative() && !Config.EARN_XP_IN_CREATIVE.get()) return;

        var data = player.getData(ExplorationProgression.DATA);

        double biomeValue = Config.NEW_BIOME_XP_VALUE.get();
        double dimensionValue = Config.NEW_DIMENSION_XP_VALUE.get();


        if(biomeValue > 0.0){
            var biomeKey = player.level().getBiome(player.blockPosition()).getKey();
            if(biomeKey != null){
                String biomeID = biomeKey.location().toString();

                if(!data.hasDiscoveredBiome(biomeID)){
                    data.addBiome(biomeID);
                    biomeValue *= player.getAttributeValue(AttributeRegistry.SKILL_XP_MULTIPLIER);
                    XpManager.addXP(player, (float)biomeValue);
                }
            }
        }

        if(dimensionValue > 0.0){
            String dimensionID = player.level().dimension().location().toString();

            if(!data.hasDiscoveredAnyDimension()){
                data.addDimension(dimensionID);
                return;
            }

            if(!data.hasDiscoveredDimension(dimensionID)){
                data.addDimension(dimensionID);
                dimensionValue *= player.getAttributeValue(AttributeRegistry.SKILL_XP_MULTIPLIER);
                XpManager.addXP(player, (float)dimensionValue);
            }
        }
    }
}
