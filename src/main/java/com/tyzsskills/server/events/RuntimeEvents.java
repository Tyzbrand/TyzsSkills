package com.tyzsskills.server.events;

import com.tyzsskills.server.active.*;
import com.tyzsskills.server.effects.GenericEffects;
import com.tyzsskills.server.payloads.SkillSyncPayload;
import com.tyzsskills.server.xp.XpManager;
import com.tyzsskills.server.xp.xpEvents.XpBlock;
import com.tyzsskills.server.xp.xpEvents.XpEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.io.IOException;

public class RuntimeEvents {

    @SubscribeEvent
    public static void OnPlayerLogin(PlayerEvent.PlayerLoggedInEvent event){
        if(!(event.getEntity() instanceof ServerPlayer player)) return;

        AutoSyncClient.SyncMainData(player);
        AutoSyncClient.SyncSkillList(player);
        AutoSyncClient.SyncSkillLevels(player);
    }

    @SubscribeEvent
    public static void OnPlayerClone(PlayerEvent.Clone event){

        if(!(event.getOriginal() instanceof ServerPlayer oldPlayer) ||
                !(event.getEntity() instanceof ServerPlayer newPlayer)) return;

        if(event.isWasDeath()){
            XpManager.RestorePlayerXPData(oldPlayer, newPlayer);
            LevelManager.RestorePlayerLevelData(oldPlayer, newPlayer);
            SpManager.RestorePlayerSPData(oldPlayer, newPlayer);
            GenericEffects.RestaureEffects(oldPlayer, newPlayer);
        }
    }


    @SubscribeEvent
    public static void OnBlockBreak(BlockEvent.BreakEvent event){

        if(event.isCanceled()) return;

        if(event.getPlayer() instanceof ServerPlayer serverPlayer){
            XpBlock.BlockBreakProfit(event.getState(), serverPlayer);
        }
    }

    @SubscribeEvent
    public static void OnEntityDeath(LivingDeathEvent event){

        if(event.isCanceled()) return;

        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;
        XpEntity.EntityKillProfit(event.getEntity(), player);
    }

}
