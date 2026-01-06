package com.tyzsskills.server.xp;

import com.tyzsskills.server.payloads.XpUpdatePayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;


public class XpManager {

    private static final String dataKey = "SKILL_XP";

    public static void SetXP(ServerPlayer player, float amount){
        if(amount < 0) return;

        var playerData = player.getPersistentData();
        playerData.putFloat(dataKey, amount);

        UpdateClient(player);
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

        if(oldData.contains(dataKey)) {
            newData.putFloat(dataKey, oldData.getFloat(dataKey));
            UpdateClient(newPlayer);
        }
        else SetXP(newPlayer, 0f);
    }

    public static void EnsureDefaultXP(ServerPlayer player){
        if(!player.getPersistentData().contains(dataKey)) SetXP(player,0f);
        else UpdateClient(player);
    }

    //Utilitaire
    private static void UpdateClient(ServerPlayer player){
        PacketDistributor.sendToPlayer(player, new XpUpdatePayload(GetXP(player)));
    }

    //Getters
    public static float GetXP(ServerPlayer player){
        return player.getPersistentData().getFloat(dataKey);
    }

}
