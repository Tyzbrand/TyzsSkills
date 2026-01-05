package com.tyzsskills.server.events;

import com.tyzsskills.server.active.*;
import com.tyzsskills.server.xp.XpManager;
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
        LevelManager.EnsureDefaultLevel((ServerPlayer) event.getEntity());
        XpManager.EnsureDefaultXP((ServerPlayer) event.getEntity());
        SpManager.EnsureDefaultSP((ServerPlayer) event.getEntity());
    }

    @SubscribeEvent
    public static void OnPlayerClone(PlayerEvent.Clone event){

        if(event.isWasDeath()){
            var oldPlayer = (ServerPlayer) event.getOriginal();
            var newPlayer = (ServerPlayer) event.getEntity();

            XpManager.RestorePlayerXPData(oldPlayer, newPlayer);
            LevelManager.RestorePlayerLevelData(oldPlayer, newPlayer);
            SpManager.RestorePlayerSPData(oldPlayer, newPlayer);
        }
    }

    @SubscribeEvent
    public static void OnWorldLoad(LevelEvent.Load event) throws IOException {

        if(!(event.getLevel() instanceof ServerLevel serverLevel)) return;

        var server = serverLevel.getServer();
        if(!SkillManager.Get().AreSkillsLoaded()) FileManager.Get().ReadSkills(server);
    }

    @SubscribeEvent
    public static void OnBlockBreak(BlockEvent.BreakEvent event){
        if(event.getPlayer() instanceof ServerPlayer){

        }
    }

}
