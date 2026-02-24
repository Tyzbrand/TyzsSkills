package com.tyzsskills.impl.server.active;

import com.tyzsskills.api.events.PlayerResetEvent;
import com.tyzsskills.api.events.SkillReloadEvent;
import com.tyzsskills.impl.server.Level.LevelManager;
import com.tyzsskills.impl.server.attachments.ExplorationProgression;
import com.tyzsskills.impl.server.attachments.StatsTracker;
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
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;

@ApiStatus.Internal
public class DebugManager {
    public static void DebugReload(MinecraftServer server) throws IOException {
        ErrorManager.ClearErrors();

        SkillManager.Get().clearSkills();
        XpBlock.ClearValues();
        XpEntity.ClearValues();
        XpFood.ClearValues();
        XpManager.clearPool();

        FileManager.Get().ReadJsons(server);
        FileManager.Get().ReadLevelPool(server);
        FileManager.Get().ReadXpValues(server);
        FileManager.Get().ReadCustomSkills(server);

        var manager = SkillManager.Get();

        for(var player : server.getPlayerList().getPlayers()){
            AutoSyncClient.SyncSkillList(player);
            AutoSyncClient.SyncConfig(player);
            AutoSyncClient.SyncMainData(player);
            XpManager.levelUpCheck(player);

            if (player.hasPermissions(2) && ErrorManager.HasErrors()) {
                ErrorManager.PrintErrors(player);
            }
        }

        for(var player : server.getPlayerList().getPlayers()){
            for (var skill : manager.getAllSkills()){
                if(manager.getPlayerSkillLevel(player, skill.getID().toLowerCase()) > skill.getMaximumLevel()){
                    manager.setSkillLevel(player, skill.getID().toLowerCase(), skill.getMaximumLevel());
                }
            }
        }

        NeoForge.EVENT_BUS.post(new SkillReloadEvent());
    }

    public static void DebugResetData(ServerPlayer player){
        LevelManager.setLevel(player, 1);
        SpManager.setSP(player, 0);
        PowerManager.SetPower(player, 0);
        XpManager.setXP(player, 0f);
        player.getData(ExplorationProgression.DATA).resetPlayerData();
        player.getData(StatsTracker.DATA).resetStats();

        var manager = SkillManager.Get();

        for (var skill : manager.getAllSkills()){
            if(manager.getPlayerSkillLevel(player, skill.getID().toLowerCase()) > 0){
                manager.setSkillLevel(player, skill.getID().toLowerCase(), 0);
            }
        }

        NeoForge.EVENT_BUS.post(new PlayerResetEvent(player));
    }
}
