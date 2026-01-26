package com.tyzsskills.server.events;

import com.tyzsskills.Config;
import com.tyzsskills.server.active.*;
import com.tyzsskills.server.attachments.BlockMarker;
import com.tyzsskills.server.attachments.ExplorationProgression;
import com.tyzsskills.server.effects.GenericEffects;
import com.tyzsskills.server.model.Skill;
import com.tyzsskills.server.skills.SkillManager;
import com.tyzsskills.server.xp.XpManager;
import com.tyzsskills.server.xp.xpEvents.XpBlock;
import com.tyzsskills.server.xp.xpEvents.XpEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.PlayLevelSoundEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.*;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;

public class RuntimeEvents {

    @SubscribeEvent
    public static void OnPlayerLogin(PlayerEvent.PlayerLoggedInEvent event){
        if(!(event.getEntity() instanceof ServerPlayer player)) return;

        CompatibilityManager.ProcessMigration(player);

        var data = player.getData(ExplorationProgression.DATA);
        if(!data.hasDiscoveredAnyDimension()){
            String dimensionID = player.level().dimension().location().toString();
            data.addDimension(dimensionID);
        }

        AutoSyncClient.SyncMainData(player);
        AutoSyncClient.SyncSkillList(player);
        AutoSyncClient.SyncSkillLevels(player);
        AutoSyncClient.SyncConfig(player);
        AutoSyncClient.SyncSkillBookmarks(player);
        AutoSyncClient.SyncStats(player);
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event){

        if(!(event.getOriginal() instanceof ServerPlayer oldPlayer) ||
                !(event.getEntity() instanceof ServerPlayer newPlayer)) return;

        if(event.isWasDeath()){
            XpManager.RestorePlayerXPData(oldPlayer, newPlayer);
            LevelManager.RestorePlayerLevelData(oldPlayer, newPlayer);
            SpManager.RestorePlayerSPData(oldPlayer, newPlayer);
            SkillManager.Get().RestaureSkillData(oldPlayer, newPlayer);

            GenericEffects.RestaureEffects(newPlayer);
        }

        SkillEffectsEvents.OnPlayerClone(event);
    }

    @SubscribeEvent
    public static void OnBlockPlace(BlockEvent.EntityPlaceEvent event){
        if(event.isCanceled() || !Config.PREVENT_PLACED_BLOCK_XP.get()) return;
        if(!(event.getEntity() instanceof ServerPlayer)) return;

        if(event.getState().getBlock() instanceof CropBlock ||
                event.getState().getBlock() instanceof NetherWartBlock) return;


        if(XpBlock.GetBlockValue(event.getState()) > 0){
            BlockMarker.MarkBlock((net.minecraft.world.level.Level)event.getLevel(), event.getPos());
        }
    }

}
