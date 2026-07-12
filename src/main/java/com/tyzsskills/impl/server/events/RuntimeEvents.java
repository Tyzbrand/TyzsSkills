package com.tyzsskills.impl.server.events;

import com.tyzsskills.Config;
import com.tyzsskills.Tyzsskills;
import com.tyzsskills.api.records.LevelData;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.server.Level.LevelManager;
import com.tyzsskills.impl.server.active.*;
import com.tyzsskills.impl.server.attachments.BlockMarker;
import com.tyzsskills.impl.server.attachments.PlayerData;
import com.tyzsskills.impl.server.attachments.StatsTracker;
import com.tyzsskills.impl.server.categories.CategoryLoader;
import com.tyzsskills.impl.server.commands.MainCommand;
import com.tyzsskills.impl.server.effects.GenericEffects;
import com.tyzsskills.impl.server.payloads.PayloadSecurity;
import com.tyzsskills.impl.server.payloads.UpdatePayloads;
import com.tyzsskills.impl.server.skills.SkillManager;
import com.tyzsskills.impl.server.sp.SpManager;
import com.tyzsskills.impl.server.xp.XpGainRegistry;
import com.tyzsskills.impl.server.xp.XpManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.*;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ApiStatus.Internal
public class RuntimeEvents {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event){
        event.getDispatcher().register(MainCommand.register());
    }

    @SubscribeEvent
    public static void onServerBeforeStart(ServerAboutToStartEvent event) {
        var server = event.getServer();

        try{
            FileManager.init(server);

            FileManager.writeDefaultSkills(server);
            FileManager.writeDefaultData(server);

            FileManager.readSkills(server);
            FileManager.readData(server);
        }
        catch (IOException e) {
            Tyzsskills.LOGGER.error("CRITICAL ERROR: Unable to load files during server start", e);
            throw new RuntimeException(e);
        }
    }

    @SubscribeEvent
    public static void onServerStop(ServerStoppingEvent event){
        SkillManager.clearSkills();

        XpGainRegistry.clearAll();
        LevelManager.clearPool();
        CategoryLoader.clearCategories();

        ErrorManager.clearErrors();
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event){
        if(!(event.getEntity() instanceof ServerPlayer player)) return;

        CompatibilityManager.processMigration(player);
        CompatibilityManager.processMigrationV2(player);
        CompatibilityManager.processMigrationV3(player);

        PacketDistributor.sendToPlayer(player, UpdatePayloads.getInitPayload(player));

        DebugManager.SANITIZER.repairInconsistencies(player);

        GenericEffects.restoreEffects(player);

        if (player.hasPermissions(2) && ErrorManager.hasErrors()) {
            ErrorManager.printErrors(player);
        }

    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event){
        if(!(event.getEntity() instanceof ServerPlayer player)) return;

        PayloadSecurity.unregisterPlayer(player);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
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
}
