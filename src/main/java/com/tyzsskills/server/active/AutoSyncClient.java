package com.tyzsskills.server.active;

import com.tyzsskills.server.payloads.*;
import com.tyzsskills.server.xp.XpManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public class AutoSyncClient {
    public static void SyncSkillList(ServerPlayer player){
        PacketDistributor.sendToPlayer(player, new SkillSyncPayload(SkillManager.Get().GetAllSkills()));
    }

    public static void SyncSkillLevels(ServerPlayer player){
        for(var skill : SkillManager.Get().GetAllSkills()) {
            var data = player.getPersistentData();
            var key = skill.GetID() + "_lvl";

            var lvl = data.getInt(key);
            if (lvl > 0) {
                PacketDistributor.sendToPlayer(player, new SkillLevelSyncPayload(skill.GetID(), lvl));
            }
        }
    }

    public static void SyncMainData(ServerPlayer player){
        PacketDistributor.sendToPlayer(player, new XpUpdatePayload(XpManager.GetXP(player)));
        PacketDistributor.sendToPlayer(player, new LevelUpdatePayload(LevelManager.GetLevel(player)));
        PacketDistributor.sendToPlayer(player, new SpUpdatePayload(SpManager.GetSP(player)));
        PacketDistributor.sendToPlayer(player, new LevelDataUpdatePayload(XpManager.GetLevelData(LevelManager.GetLevel(player))));
    }


}
