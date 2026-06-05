package com.tyzsskills.impl.server.sp;

import com.tyzsskills.api.events.SkillPointChangeEvent;
import com.tyzsskills.impl.server.attachments.PlayerData;
import com.tyzsskills.impl.server.attachments.StatsTracker;
import com.tyzsskills.impl.server.payloads.UpdatePayloads;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class SpManager {


    //INTERNAL
    private static boolean setSpInternal(@NotNull ServerPlayer player, int newAmount, boolean syncClient, boolean updateStats) {
        int oldAmount = getSP(player);
        if(oldAmount == newAmount) return false;

        //Event
        var event = new SkillPointChangeEvent(player, oldAmount, newAmount);
        NeoForge.EVENT_BUS.post(event);
        if(event.isCanceled()) return false;

        int finalAmount = event.getNewAmount();
        if(finalAmount == oldAmount) return false;

        player.getData(PlayerData.DATA).setSP(finalAmount);

        //Stats
        if(updateStats){
            var diff = finalAmount - oldAmount;
            if(diff > 0) player.getData(StatsTracker.DATA).addSpEarned(diff);
            else player.getData(StatsTracker.DATA).addSpSpent(Math.abs(diff));
        }

        //Client
        if(syncClient) updateClient(player);

        return true;
    }


    //PUBLIC
    public static boolean tryAddSp(@NotNull ServerPlayer player, int amount) {
        if(amount <= 0) return false;
        return setSpInternal(player, getSP(player) + amount, true, true);
    }

    public static boolean tryRemoveSp(@NotNull ServerPlayer player, int amount){
        if(amount <= 0 || amount > getSP(player)) return false;
        return setSpInternal(player, getSP(player) - amount, true, true);
    }

    public static void setSp(@NotNull ServerPlayer player, int newAmount){if(newAmount >= 0) setSpInternal(player, newAmount, true, false);}

    public static void resetSp(@NotNull ServerPlayer player){setSpInternal(player, 0, false, false);}

    //Util
    private static void updateClient(ServerPlayer player){PacketDistributor.sendToPlayer(player, new UpdatePayloads.SpPayload(getSP(player)));}

    //Getters
    public static int getSP(ServerPlayer player){
            return player.getData(PlayerData.DATA).getSP();
        }
}


