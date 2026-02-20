package com.tyzsskills.impl.server.active;

import com.tyzsskills.impl.server.payloads.PowerUpdatePayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class PowerManager {

    private static final String dataKey = "TRAIT_POWER";

    public static void SetPower(ServerPlayer player, int amount){
        if(amount < 0) return;

        var playerData = player.getPersistentData();
        playerData.putInt(dataKey, amount);

        UpdateClient(player);
    }

    public static void AddPower(ServerPlayer player, int amount){
        if(amount <= 0) return;
        SetPower(player, GetPower(player) + amount);
    }

    public static void RemovePower(ServerPlayer player, int amount){
        if(amount <= 0) return;
        var result = Math.max(0, GetPower(player) - amount);
        SetPower(player, result);
    }

    public static void RestorePlayerPowerData(ServerPlayer oldPlayer, ServerPlayer newPlayer){

        var oldData = oldPlayer.getPersistentData();
        var newData = newPlayer.getPersistentData();

        if(oldData.contains(dataKey)) {
            newData.putInt(dataKey, oldData.getInt(dataKey));
            UpdateClient(newPlayer);
        }
        else SetPower(newPlayer, 0);
    }

    //Utilitaire
    private static void UpdateClient(ServerPlayer player){
        PacketDistributor.sendToPlayer(player, new PowerUpdatePayload(GetPower(player)));
    }

    //Getters
    public static int GetPower(ServerPlayer player){
        return player.getPersistentData().getInt(dataKey);
    }

}
