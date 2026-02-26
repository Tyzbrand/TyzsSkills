package com.tyzsskills.impl.server.active;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.events.PlayerResetEvent;
import com.tyzsskills.api.events.SkillReloadEvent;
import com.tyzsskills.impl.server.Level.LevelManager;
import com.tyzsskills.impl.server.attachments.ExplorationProgression;
import com.tyzsskills.impl.server.attachments.LimitsTracker;
import com.tyzsskills.impl.server.attachments.StatsTracker;
import com.tyzsskills.impl.server.payloads.ResetPayload;
import com.tyzsskills.impl.server.power.PowerManager;
import com.tyzsskills.impl.server.skills.SkillManager;
import com.tyzsskills.impl.server.sp.SpManager;
import com.tyzsskills.impl.server.xp.XpManager;
import com.tyzsskills.impl.server.xp.xpEvents.XpBlock;
import com.tyzsskills.impl.server.xp.xpEvents.XpEntity;
import com.tyzsskills.impl.server.xp.xpEvents.XpFood;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;

@ApiStatus.Internal
public class DebugManager {

    //------------RELOAD------------
    public static void reload(MinecraftServer server) throws IOException {
        ErrorManager.clearErrors();

        SkillManager.get().clearSkills();
        XpBlock.clearValues();
        XpEntity.clearValues();
        XpFood.clearValues();
        XpManager.clearPool();

        FileManager.get().readJsons(server);
        FileManager.get().readLevelPool(server);
        FileManager.get().readXpValues(server);
        FileManager.get().readCustomSkills(server);

        var manager = SkillManager.get();

        for(var player : server.getPlayerList().getPlayers()){
            XpManager.levelUpCheck(player);

            for (var skill : manager.getAllSkills()){
                if(manager.getPlayerSkillLevel(player, skill.getID().toLowerCase()) > skill.getMaximumLevel()){
                    manager.setSkillLevel(player, skill.getID().toLowerCase(), skill.getMaximumLevel());
                }
            }
        }

        for(var player : server.getPlayerList().getPlayers()){

            PacketDistributor.sendToPlayer(player, new ResetPayload(Enums.ResetType.ALL));

            AutoSyncClient.syncSkillList(player);
            AutoSyncClient.syncConfig(player);
            AutoSyncClient.syncMainData(player);
            AutoSyncClient.syncStats(player);
            AutoSyncClient.syncSkillBookmarks(player);
            AutoSyncClient.syncSkillLevels(player);

            if (player.hasPermissions(2) && ErrorManager.hasErrors()) {
                ErrorManager.printErrors(player);
            }
        }

        NeoForge.EVENT_BUS.post(new SkillReloadEvent());
    }


    //------------RESET------------

    public static void reset(ServerPlayer player, Enums.ResetType type){
        switch (type){
            case ALL -> {
                resetSkills(player);
                resetLimits(player);
                resetMetaData(player);
                resetStats(player);
            }
            case METADATA -> resetMetaData(player);
            case STATS -> resetStats(player);
            case SKILLS -> resetSkills(player);
            case LIMITS -> resetLimits(player);

        }
        NeoForge.EVENT_BUS.post(new PlayerResetEvent(player));
        PacketDistributor.sendToPlayer(player, new ResetPayload(type));
    }

    private static void resetMetaData(ServerPlayer player){
        LevelManager.setLevel(player, 1);
        SpManager.setSP(player, 0);
        PowerManager.setPower(player, 0);
        XpManager.setXP(player, 0f);
        player.getData(ExplorationProgression.DATA).resetPlayerData();
    }

    private static void resetStats(ServerPlayer player){
        player.getData(StatsTracker.DATA).resetStats();
    }

    private static void resetLimits(ServerPlayer player){
        player.getData(LimitsTracker.DATA).resetLimits();
    }

    private static void resetSkills(ServerPlayer player){
        var manager = SkillManager.get();

        for (var skill : manager.getAllSkills()){
            if(manager.getPlayerSkillLevel(player, skill.getID().toLowerCase()) > 0){
                manager.setSkillLevel(player, skill.getID().toLowerCase(), 0);
            }
        }
    }
}
