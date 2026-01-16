package com.tyzsskills.server.events;

import com.tyzsskills.Config;
import com.tyzsskills.server.active.AttributeRegistry;
import com.tyzsskills.server.attachments.BlockMarker;
import com.tyzsskills.server.attachments.ExplorationProgression;
import com.tyzsskills.server.xp.XpManager;
import com.tyzsskills.server.xp.xpEvents.XpBlock;
import com.tyzsskills.server.xp.xpEvents.XpEntity;
import com.tyzsskills.server.xp.xpEvents.XpFood;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
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
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class XpGainsEvents {

    //Xp gains
    @SubscribeEvent
    public static void OnBlockBreak(BlockEvent.BreakEvent event){
        if(event.isCanceled()) return;
        if(!(event.getPlayer() instanceof ServerPlayer player)) return;
        if(player.isCreative() && !Config.EARN_XP_IN_CREATIVE.get()) return;

        if(Config.PREVENT_PLACED_BLOCK_XP.get()) {
            Level level = (net.minecraft.world.level.Level) event.getLevel();
            var pos = event.getPos();

            if (BlockMarker.IsPlayerPlaced(level, pos)) {
                BlockMarker.RemoveBlock(level, pos);
                return;
            }
        }
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
    public static void OnEntityFish(ItemFishedEvent event){
        if(event.isCanceled()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if(player.isCreative() && !Config.EARN_XP_IN_CREATIVE.get()) return;

        float value = (float)Config.FISHING_XP_VALUE.getAsDouble();
        if(value <= 0) return;

        value *= (float)player.getAttributeValue(AttributeRegistry.SKILL_XP_MULTIPLIER);
        XpManager.AddXP(player, value);
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
        XpManager.AddXP(player, value);
    }

    @SubscribeEvent
    public static void OnBabySpawn(BabyEntitySpawnEvent event){
        if (!(event.getCausedByPlayer() instanceof ServerPlayer player)) return;
        if(player.isCreative() && !Config.EARN_XP_IN_CREATIVE.get()) return;

        float value = (float)Config.BREEDING_XP_VALUE.getAsDouble();
        if(value <= 0) return;

        value *= (float)player.getAttributeValue(AttributeRegistry.SKILL_XP_MULTIPLIER);
        XpManager.AddXP(player, value);
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
            XpManager.AddXP(player, finalValue);
        });
    }

    @SubscribeEvent
    public static void OnPlayerTick(PlayerTickEvent.Post event){
        if (event.getEntity().tickCount % 100 != 0) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if(player.isCreative() && !Config.EARN_XP_IN_CREATIVE.get()) return;

        var data = player.getData(ExplorationProgression.DATA);

        double biomeValue = Config.NEW_BIOME_XP_VALUE.get();
        double structureValue = Config.NEW_STRUCTURE_XP_VALUE.get();
        double dimensionValue = Config.NEW_DIMENSION_XP_VALUE.get();


        if(biomeValue > 0.0){
            var biomeKey = player.level().getBiome(player.blockPosition()).getKey();
            if(biomeKey != null){
                String biomeID = biomeKey.location().toString();

                if(!data.hasDiscoveredBiome(biomeID)){
                    data.addBiome(biomeID);
                    biomeValue *= player.getAttributeValue(AttributeRegistry.SKILL_XP_MULTIPLIER);
                    XpManager.AddXP(player, (float)biomeValue);
                }
            }
        }

        if(dimensionValue > 0.0){
            String dimensionID = player.level().dimension().location().toString();
            if(!data.hasDiscoveredDimension(dimensionID)){
                data.addDimension(dimensionID);
                dimensionValue *= player.getAttributeValue(AttributeRegistry.SKILL_XP_MULTIPLIER);
                XpManager.AddXP(player, (float)dimensionValue);
            }
        }

        if(structureValue > 0.0){
            var structureMap = player.serverLevel().structureManager().getAllStructuresAt(player.blockPosition());

            if(!structureMap.isEmpty()){
                var structureRegistry = player.serverLevel().registryAccess().registryOrThrow(Registries.STRUCTURE);

                for(var structure : structureMap.keySet()){
                    var structureKey = structureRegistry.getKey(structure);
                    if (structureKey == null) continue;

                    String structureId = structureKey.toString();

                    if(!data.hasDiscoveredStructure(structureId)){
                        data.addStructure(structureId);
                        double finalValue = structureValue * player.getAttributeValue(AttributeRegistry.SKILL_XP_MULTIPLIER);
                        XpManager.AddXP(player, (float)finalValue);
                    }
                }
            }
        }







    }
}
