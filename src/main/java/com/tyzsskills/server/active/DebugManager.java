package com.tyzsskills.server.active;

import com.tyzsskills.server.payloads.LevelDataUpdatePayload;
import com.tyzsskills.server.skills.SkillManager;
import com.tyzsskills.server.xp.XpManager;
import com.tyzsskills.server.xp.xpEvents.XpBlock;
import com.tyzsskills.server.xp.xpEvents.XpEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.io.IOException;

public class DebugManager {
    public static void DebugReload(MinecraftServer server) throws IOException {
        SkillManager.Get().ClearSkills();
        XpBlock.ClearValues();
        XpEntity.ClearValues();
        XpManager.ClearPool();

        FileManager.Get().ReadJsons(server);
        FileManager.Get().ReadLevelPool(server);
        FileManager.Get().ReadXpValues(server);

        for(var player : server.getPlayerList().getPlayers()){
            AutoSyncClient.SyncSkillList(player);
            AutoSyncClient.SyncMainData(player);

            XpManager.LevelUpCheck(player);
        }
    }

    public static void DebugResetData(ServerPlayer player){
        XpManager.SetXP(player, 0f);
        LevelManager.SetLevel(player, 1);
        SpManager.SetSP(player, 0);

        var manager = SkillManager.Get();

        for (var skill : manager.GetAllSkills()){
            if(manager.GetPlayerSkillLevel(player, skill.GetID().toLowerCase()) > 0){
                manager.SetSkillLevel(player, skill.GetID().toLowerCase(), 0);
            }
        }

        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Player data has been fully reset"));
    }
}
