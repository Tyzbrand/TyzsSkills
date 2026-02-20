package com.tyzsskills.server.events;

import com.tyzsskills.server.active.*;
import com.tyzsskills.server.attachments.BlockMarker;
import com.tyzsskills.server.effects.GenericEffects;
import com.tyzsskills.server.xp.xpEvents.XpBlock;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.player.*;
import net.neoforged.neoforge.event.level.BlockEvent;

public class RuntimeEvents {

    @SubscribeEvent
    public static void OnPlayerLogin(PlayerEvent.PlayerLoggedInEvent event){
        if(!(event.getEntity() instanceof ServerPlayer player)) return;

        CompatibilityManager.processMigration(player);
        CompatibilityManager.processMigrationV2(player);

        AutoSyncClient.SyncMainData(player);
        AutoSyncClient.SyncSkillList(player);
        AutoSyncClient.SyncSkillLevels(player);
        AutoSyncClient.SyncConfig(player);
        AutoSyncClient.SyncSkillBookmarks(player);
        AutoSyncClient.SyncStats(player);

        if (player.hasPermissions(2) && ErrorManager.HasErrors()) {
            ErrorManager.PrintErrors(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event){

        if(!(event.getOriginal() instanceof ServerPlayer) ||
                !(event.getEntity() instanceof ServerPlayer newPlayer)) return;

        GenericEffects.RestaureEffects(newPlayer);
        SkillEffectsEvents.OnPlayerClone(event);
    }

    @SubscribeEvent
    public static void OnBlockPlace(BlockEvent.EntityPlaceEvent event){
        if(event.isCanceled()) return;
        if(!(event.getEntity() instanceof ServerPlayer)) return;

        if(event.getState().getBlock() instanceof CropBlock ||
                event.getState().getBlock() instanceof NetherWartBlock) return;

        if(XpBlock.GetBlockValue(event.getState()) > 0
        || event.getState().is(BlockTags.LEAVES) || event.getState().is(BlockTags.LOGS) || event.getState().is(Tags.Blocks.ORES)){
            BlockMarker.MarkBlock((net.minecraft.world.level.Level)event.getLevel(), event.getPos());
        }
    }

}
