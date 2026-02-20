package com.tyzsskills.impl.server.power;

import com.tyzsskills.impl.server.attachments.PlayerData;
import com.tyzsskills.impl.server.payloads.PowerUpdatePayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class PowerManager {

    public static void SetPower(ServerPlayer player, int amount){
        if(amount < 0) return;

        var playerData = player.getData(PlayerData.DATA);
        playerData.setPower(amount);

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

    //Util
    private static void UpdateClient(ServerPlayer player){
        PacketDistributor.sendToPlayer(player, new PowerUpdatePayload(GetPower(player)));
    }

    //Getters
    public static int GetPower(ServerPlayer player){
        return player.getData(PlayerData.DATA).getPower();
    }

}
