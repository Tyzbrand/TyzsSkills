package com.tyzsskills.server.active;

import net.minecraft.server.level.ServerPlayer;


public class XpManager {

    private static final String dataKey = "SKILL_XP";

    public static void SetXP(ServerPlayer player, float amount){
        if(amount < 0) return;

        var playerData = player.getPersistentData();
        playerData.putFloat(dataKey, amount);
    }

    public static void AddXP(ServerPlayer player, float amount){
        if(amount <= 0) return;
        SetXP(player, GetXP(player) + amount);
    }

    public static void RemoveXP(ServerPlayer player, float amount){
        if(amount <= 0) return;
        var result = Math.max(0f, GetXP(player) - amount);
        SetXP(player, result);
    }

    public static void RestorePlayerXPData(ServerPlayer oldPlayer, ServerPlayer newPlayer){

        var oldData = oldPlayer.getPersistentData();
        var newData = newPlayer.getPersistentData();

        if(oldData.contains(dataKey)) newData.putFloat(dataKey, oldData.getFloat(dataKey));
    }

    public static void EnsureDefaultXP(ServerPlayer player){
        if(!player.getPersistentData().contains(dataKey)) SetXP(player,0f);
    }

    //Getters
    public static float GetXP(ServerPlayer player){
        return player.getPersistentData().getFloat(dataKey);
    }

}
