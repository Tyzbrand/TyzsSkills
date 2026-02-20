package com.tyzsskills.server.active;

import com.tyzsskills.Config;
import com.tyzsskills.server.attachments.PlayerData;
import com.tyzsskills.server.attachments.StatsTracker;
import com.tyzsskills.server.payloads.*;
import com.tyzsskills.server.skills.SkillManager;
import com.tyzsskills.server.xp.XpManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public class AutoSyncClient {
    public static void SyncSkillList(ServerPlayer player){
        PacketDistributor.sendToPlayer(player, new SkillSyncPayload(SkillManager.Get().getAllSkills()));
    }

    public static void SyncSkillLevels(ServerPlayer player){
        var data = player.getData(PlayerData.DATA);
        for(var skill : SkillManager.Get().getAllSkills()) {
            var lvl = data.getSkillLevel(skill.GetID());
            if (lvl > 0) {
                PacketDistributor.sendToPlayer(player, new SkillLevelSyncPayload(skill.GetID(), lvl));
            }
        }
    }

    public static void SyncSkillBookmarks(ServerPlayer player){
        var data = player.getData(PlayerData.DATA);
        for(var entry : data.getBookmarks()) {
                PacketDistributor.sendToPlayer(player, new SkillBookmarksPayload(entry, true));
        }
    }

    public static void SyncConfig(ServerPlayer player){
        PacketDistributor.sendToPlayer(player, new ConfigSyncPayload(Config.REFUND_SYSTEM.get(),
                Config.REFUND_PERCENTAGE.get(),
                Config.TRAIT_UNLOCK_LEVEL.get(),
                Config.TRAIT_SYSTEM.get()));
    }

    public static void SyncMainData(ServerPlayer player){
        PacketDistributor.sendToPlayer(player, new XpUpdatePayload(XpManager.getXP(player), 0f));
        PacketDistributor.sendToPlayer(player, new LevelUpdatePayload(LevelManager.getLevel(player)));
        PacketDistributor.sendToPlayer(player, new SpUpdatePayload(SpManager.getSP(player)));
        PacketDistributor.sendToPlayer(player, new LevelDataUpdatePayload(XpManager.getLevelData(LevelManager.getLevel(player))));
        PacketDistributor.sendToPlayer(player, new PowerUpdatePayload(PowerManager.GetPower(player)));
    }

    public static void SyncStats(ServerPlayer player){
        var data = player.getData(StatsTracker.DATA);

        PacketDistributor.sendToPlayer(player, new StatsXpPayload(data.getAllTimeXp()));
        PacketDistributor.sendToPlayer(player, new StatsSpEarnedPayload(data.getTotalSpEarned()));
        PacketDistributor.sendToPlayer(player, new StatsSpSpentPayload(data.getTotalSpSpent()));
    }


}
