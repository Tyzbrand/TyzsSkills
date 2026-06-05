package com.tyzsskills.impl.server.events;

import com.tyzsskills.Config;
import com.tyzsskills.api.records.LevelData;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.server.Level.LevelManager;
import com.tyzsskills.impl.server.active.*;
import com.tyzsskills.impl.server.attachments.BlockMarker;
import com.tyzsskills.impl.server.attachments.PlayerData;
import com.tyzsskills.impl.server.attachments.StatsTracker;
import com.tyzsskills.impl.server.categories.CategoryLoader;
import com.tyzsskills.impl.server.effects.GenericEffects;
import com.tyzsskills.impl.server.payloads.UpdatePayloads;
import com.tyzsskills.impl.server.skills.SkillManager;
import com.tyzsskills.impl.server.sp.SpManager;
import com.tyzsskills.impl.server.xp.XpGainRegistry;
import com.tyzsskills.impl.server.xp.XpManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.player.*;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

@ApiStatus.Internal
public class RuntimeEvents {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event){
        if(!(event.getEntity() instanceof ServerPlayer player)) return;

        CompatibilityManager.processMigration(player);
        CompatibilityManager.processMigrationV2(player);
        CompatibilityManager.processMigrationV3(player);

        PacketDistributor.sendToPlayer(player, UpdatePayloads.getInitPayload(player));

        DebugManager.checkForInconsistencies(player);

        GenericEffects.restoreEffects(player);

        if (player.hasPermissions(2) && ErrorManager.hasErrors()) {
            ErrorManager.printErrors(player);
        }

    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event){

        if(!(event.getOriginal() instanceof ServerPlayer oldPlayer) ||
                !(event.getEntity() instanceof ServerPlayer newPlayer)) return;

        var oldData = oldPlayer.getData(PlayerData.DATA).serializeNBT(oldPlayer.registryAccess());
        newPlayer.getData(PlayerData.DATA).deserializeNBT(newPlayer.registryAccess(), oldData);

        if(event.isWasDeath()) {
            DeathPenalties.applySpPenalty(newPlayer);
            DeathPenalties.applyLvlPenalty(newPlayer);
            DeathPenalties.applyXpPenalty(newPlayer);
            DeathPenalties.applySkillPenalty(newPlayer);
        }

        GenericEffects.restoreEffects(newPlayer);
        SkillEffectsEvents.onPlayerClone(event);
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event){
        if(event.isCanceled()) return;
        if(!(event.getEntity() instanceof ServerPlayer player)) return;

        if(event.getState().getBlock() instanceof CropBlock ||
                event.getState().getBlock() instanceof NetherWartBlock) return;

        if(XpGainRegistry.getBlockValue(event.getState() , player) > 0
        || event.getState().is(BlockTags.LEAVES) || event.getState().is(BlockTags.LOGS) || event.getState().is(Tags.Blocks.ORES)){
            BlockMarker.MarkBlock((net.minecraft.world.level.Level)event.getLevel(), event.getPos());
        }
    }

    //UTILS
}
