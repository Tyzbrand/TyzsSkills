package com.tyzsskills.impl.server.active;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.events.PlayerResetEvent;
import com.tyzsskills.api.events.SkillReloadEvent;
import com.tyzsskills.impl.server.Level.LevelManager;
import com.tyzsskills.impl.server.attachments.ExplorationProgression;
import com.tyzsskills.impl.server.attachments.LimitsTracker;
import com.tyzsskills.impl.server.attachments.StatsTracker;
import com.tyzsskills.impl.server.effects.GenericEffects;
import com.tyzsskills.impl.server.payloads.ResetPayload;
import com.tyzsskills.impl.server.skills.SkillManager;
import com.tyzsskills.impl.server.sp.SpManager;
import com.tyzsskills.impl.server.xp.XpManager;
import com.tyzsskills.impl.server.xp.xpEvents.XpBlock;
import com.tyzsskills.impl.server.xp.xpEvents.XpEntity;
import com.tyzsskills.impl.server.xp.xpEvents.XpFood;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.logging.log4j.core.jmx.Server;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

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

        for(var player : server.getPlayerList().getPlayers()){
            checkForInconsistencies(player);

            XpManager.levelUpCheck(player);
            GenericEffects.restoreEffects(player);
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

    //------------CHECKS------------
    public static void checkForInconsistencies(@NotNull ServerPlayer player){
        var manager = SkillManager.get();
        for (var skill : manager.getAllSkills()){
            var id = skill.getID();
            if(!manager.isSkillLoaded(skill.getID())) continue;

            var lvl = manager.getPlayerSkillLevel(player, skill.getID());
            var maxLvl = skill.getMaximumLevel();
            if(lvl <= maxLvl) continue;

            var prices = skill.getPrices();

            var spToRefund = 0;
            for(int i = lvl - 1; i >= maxLvl; i--){
                if(i >= prices.size()) continue;
                spToRefund += prices.get(i);
            }

            manager.setSkillLevel(player, id, maxLvl);

            if(spToRefund <= 0) continue;

            SpManager.addSP(player, spToRefund);
        }
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
