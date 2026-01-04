package com.tyzsskills.server.active;

import net.minecraft.server.level.ServerPlayer;

public class LevelManager {

    private static final String dataKey = "SKILL_LEVEL";

    public static void SetLevel(ServerPlayer player, int level){
        if(level <= 0) return;

        var playerData = player.getPersistentData();
        playerData.putInt(dataKey, level);
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

        if(oldData.contains(dataKey)) newData.putInt(dataKey, oldData.getInt(dataKey));
        else SetLevel(newPlayer, 1);
    }

    public static void EnsureDefaultLevel(ServerPlayer player){
        if(!player.getPersistentData().contains(dataKey)) SetLevel(player,1);
    }

    //Getters
    public static int GetLevel(ServerPlayer player){
        return player.getPersistentData().getInt(dataKey);
    }

}
