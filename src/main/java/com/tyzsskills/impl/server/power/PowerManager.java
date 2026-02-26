package com.tyzsskills.impl.server.power;

import com.tyzsskills.api.events.SkillPowerChangeEvent;
import com.tyzsskills.impl.server.attachments.PlayerData;
import com.tyzsskills.impl.server.payloads.PowerUpdatePayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class PowerManager {

    public static void setPower(ServerPlayer player, int amount){
        if(amount < 0) return;

        int oldPower = getPower(player);

        var event = new SkillPowerChangeEvent(player, oldPower, amount);
        NeoForge.EVENT_BUS.post(event);

        if(event.isCanceled()) return;

        int finalPower = event.getNewPower();
        if(finalPower == oldPower) return;

        var playerData = player.getData(PlayerData.DATA);
        playerData.setPower(finalPower);

        updateClient(player);
    }

    public static void addPower(ServerPlayer player, int amount){
        if(amount <= 0) return;
        setPower(player, getPower(player) + amount);
    }

    public static void removePower(ServerPlayer player, int amount){
        if(amount <= 0) return;
        var result = Math.max(0, getPower(player) - amount);
        setPower(player, result);
    }

    //Util
    private static void updateClient(ServerPlayer player){
        PacketDistributor.sendToPlayer(player, new PowerUpdatePayload(getPower(player)));
    }

    //Getters
    public static int getPower(ServerPlayer player){
        return player.getData(PlayerData.DATA).getPower();
    }

}
