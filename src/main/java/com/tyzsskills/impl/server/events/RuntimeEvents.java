package com.tyzsskills.impl.server.events;

import com.tyzsskills.impl.server.active.*;
import com.tyzsskills.impl.server.attachments.BlockMarker;
import com.tyzsskills.impl.server.effects.GenericEffects;
import com.tyzsskills.impl.server.xp.xpEvents.XpBlock;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.player.*;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class RuntimeEvents {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event){
        if(!(event.getEntity() instanceof ServerPlayer player)) return;

        CompatibilityManager.processMigration(player);
        CompatibilityManager.processMigrationV2(player);

        AutoSyncClient.syncMainData(player);
        AutoSyncClient.syncSkillList(player);
        AutoSyncClient.syncSkillLevels(player);
        AutoSyncClient.syncConfig(player);
        AutoSyncClient.syncSkillBookmarks(player);
        AutoSyncClient.syncStats(player);

        if (player.hasPermissions(2) && ErrorManager.hasErrors()) {
            ErrorManager.printErrors(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event){

        if(!(event.getOriginal() instanceof ServerPlayer) ||
                !(event.getEntity() instanceof ServerPlayer newPlayer)) return;

        GenericEffects.restoreEffects(newPlayer);
        SkillEffectsEvents.onPlayerClone(event);
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event){
        if(event.isCanceled()) return;
        if(!(event.getEntity() instanceof ServerPlayer)) return;

        if(event.getState().getBlock() instanceof CropBlock ||
                event.getState().getBlock() instanceof NetherWartBlock) return;

        if(XpBlock.getBlockValue(event.getState()) > 0
        || event.getState().is(BlockTags.LEAVES) || event.getState().is(BlockTags.LOGS) || event.getState().is(Tags.Blocks.ORES)){
            BlockMarker.MarkBlock((net.minecraft.world.level.Level)event.getLevel(), event.getPos());
        }
    }

}
