package com.tyzsskills.server.active;

import com.tyzsskills.server.payloads.LevelDataUpdatePayload;
import com.tyzsskills.server.payloads.LevelUpdatePayload;
import com.tyzsskills.server.xp.XpManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public class LevelManager {

    private static final String dataKey = "SKILL_LEVEL";

    public static void SetLevel(ServerPlayer player, int level){
        if(level <= 0) return;

        var playerData = player.getPersistentData();
        playerData.putInt(dataKey, level);

        UpdateClient(player);
    }

    public static void AddLevel(ServerPlayer player, int level){
        if(level <= 0) return;
        SetLevel(player, GetLevel(player) + level);
    }

    public static void RemoveLevel(ServerPlayer player, int level){
        if(level <= 0) return;
        var result = Math.max(1, GetLevel(player) - level);
        SetLevel(player, result);
    }

    public static void RestorePlayerLevelData(ServerPlayer oldPlayer, ServerPlayer newPlayer){

        var oldData = oldPlayer.getPersistentData();
        var newData = newPlayer.getPersistentData();

        if(oldData.contains(dataKey)) {
            newData.putInt(dataKey, oldData.getInt(dataKey));
            UpdateClient(newPlayer);
        }
        else SetLevel(newPlayer, 1);
    }

    //Utilitaire
    private static void UpdateClient(ServerPlayer player){
        PacketDistributor.sendToPlayer(player, new LevelUpdatePayload(GetLevel(player)));
        PacketDistributor.sendToPlayer(player, new LevelDataUpdatePayload(XpManager.GetLevelData(GetLevel(player))));
    }

    //Getters
    public static int GetLevel(ServerPlayer player){return Math.max(1, player.getPersistentData().getInt(dataKey));}

}
