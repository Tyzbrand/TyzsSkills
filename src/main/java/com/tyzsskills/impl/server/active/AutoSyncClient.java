package com.tyzsskills.impl.server.active;

import com.tyzsskills.Config;
import com.tyzsskills.impl.server.Level.LevelManager;
import com.tyzsskills.impl.server.attachments.PlayerData;
import com.tyzsskills.impl.server.attachments.StatsTracker;
import com.tyzsskills.impl.server.payloads.*;
import com.tyzsskills.impl.server.skills.SkillManager;
import com.tyzsskills.impl.server.sp.SpManager;
import com.tyzsskills.impl.server.xp.XpManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class AutoSyncClient {
    public static void syncSkillList(ServerPlayer player){
        PacketDistributor.sendToPlayer(player, new SkillSyncPayload(SkillManager.get().getAllSkills()));
    }

    public static void syncSkillLevels(ServerPlayer player){
        var data = player.getData(PlayerData.DATA);
        for(var skill : SkillManager.get().getAllSkills()) {
            var lvl = data.getSkillLevel(skill.getID());
            if (lvl > 0) {
                PacketDistributor.sendToPlayer(player, new SkillLevelSyncPayload(skill.getID(), lvl));
            }
        }
    }

    public static void syncSkillBookmarks(ServerPlayer player){
        var data = player.getData(PlayerData.DATA);
        for(var entry : data.getBookmarks()) {
                PacketDistributor.sendToPlayer(player, new SkillBookmarksPayload(entry, true));
        }
    }

    public static void syncConfig(ServerPlayer player){
        PacketDistributor.sendToPlayer(player, new ConfigSyncPayload(Config.REFUND_SYSTEM.get(),
                Config.REFUND_PERCENTAGE.get(),
                Config.XP_LIMIT.get(),
                Config.PURCHASE_SYSTEM.get()));
    }

    public static void syncMainData(ServerPlayer player){
        PacketDistributor.sendToPlayer(player, new XpUpdatePayload(XpManager.getXP(player), 0f, false, XpManager.getLimitPercentage(player)));
        PacketDistributor.sendToPlayer(player, new LevelUpdatePayload(LevelManager.getLevel(player)));
        PacketDistributor.sendToPlayer(player, new SpUpdatePayload(SpManager.getSP(player)));
        PacketDistributor.sendToPlayer(player, new LevelDataUpdatePayload(XpManager.getLevelData(LevelManager.getLevel(player))));
    }

    public static void syncStats(ServerPlayer player){
        var data = player.getData(StatsTracker.DATA);

        PacketDistributor.sendToPlayer(player, new StatsXpPayload(data.getAllTimeXp()));
        PacketDistributor.sendToPlayer(player, new StatsSpEarnedPayload(data.getTotalSpEarned()));
        PacketDistributor.sendToPlayer(player, new StatsSpSpentPayload(data.getTotalSpSpent()));
    }


}
