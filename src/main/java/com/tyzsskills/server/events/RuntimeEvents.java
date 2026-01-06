package com.tyzsskills.server.events;

import com.tyzsskills.server.active.*;
import com.tyzsskills.server.xp.XpManager;
import com.tyzsskills.server.xp.xpEvents.XpBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

import java.io.IOException;

public class RuntimeEvents {

    @SubscribeEvent
    public static void OnPlayerLogin(PlayerEvent.PlayerLoggedInEvent event){
        if(!(event.getEntity() instanceof ServerPlayer player)) return;
        LevelManager.EnsureDefaultLevel(player);
        XpManager.EnsureDefaultXP(player);
        SpManager.EnsureDefaultSP(player);
    }

    @SubscribeEvent
    public static void OnPlayerClone(PlayerEvent.Clone event){

        if(!(event.getOriginal() instanceof ServerPlayer oldPlayer) ||
                !(event.getEntity() instanceof ServerPlayer newPlayer)) return;

        if(event.isWasDeath()){
            XpManager.RestorePlayerXPData(oldPlayer, newPlayer);
            LevelManager.RestorePlayerLevelData(oldPlayer, newPlayer);
            SpManager.RestorePlayerSPData(oldPlayer, newPlayer);
        }
    }

    @SubscribeEvent
    public static void OnWorldLoad(LevelEvent.Load event) throws IOException {

        if(!(event.getLevel() instanceof ServerLevel serverLevel)) return;

        var server = serverLevel.getServer();
        if(!SkillManager.Get().AreSkillsLoaded()) FileManager.Get().ReadJsons(server);
        if(!XpBlock.AreValuesLoaded()) FileManager.Get().ReadBlocksXpValues(server);
    }

    @SubscribeEvent
    public static void OnBlockBreak(BlockEvent.BreakEvent event){

        if(event.getPlayer() instanceof ServerPlayer serverPlayer){
            XpBlock.BlockBreakProfit(event.getState(), serverPlayer);
        }
    }

}
