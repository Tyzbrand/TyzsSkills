package com.tyzsskills.impl.server.xp;

import com.tyzsskills.api.events.SkillXPChangeEvent;
import com.tyzsskills.impl.server.Level.LevelManager;
import com.tyzsskills.impl.server.payloads.UpdatePayloads;
import com.tyzsskills.impl.server.attachments.PlayerData;
import com.tyzsskills.impl.server.attachments.StatsTracker;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;


@ApiStatus.Internal
public class XpManager {

    //CORE
    private static boolean setXpInternal(@NotNull ServerPlayer player, float newAmount, boolean triggersOverlay,
                                         boolean updateStats, boolean syncClient){
        float oldAmount = getXP(player);
        if (oldAmount == newAmount) return false;

        var event = new SkillXPChangeEvent(player, oldAmount, newAmount);
        NeoForge.EVENT_BUS.post(event);
        if(event.isCanceled()) return false;

        float finalAmount = event.getNewAmount();
        if (finalAmount == oldAmount) return false;

        var diff = finalAmount - oldAmount;
        var gainsForClient = Math.max(0, diff);

        if(updateStats && diff > 0){player.getData(StatsTracker.DATA).addXp(diff);}

        float leftoverXp = LevelManager.checkForLevelUp(player, finalAmount);

        player.getData(PlayerData.DATA).setXP(leftoverXp);

        if(syncClient) updateClient(player, gainsForClient, triggersOverlay);

        return true;
    }


    //PUBLIC
    public static boolean tryAddXp(@NotNull ServerPlayer player, float amount){
        if(amount <= 0) return false;
        return setXpInternal(player, getXP(player) + amount, true, true, true);
    }

    public static boolean tryRemoveXp(@NotNull ServerPlayer player, float amount){
        if(amount <= 0 || getXP(player) < amount) return false;
        return setXpInternal(player, getXP(player) - amount, false, true, true);
    }

    public static void setXp(@NotNull ServerPlayer player, float newAmount, boolean triggerOverlay){
        if(newAmount >= 0) setXpInternal(player, newAmount, triggerOverlay, true, true);
    }

    public static void resetXp(@NotNull ServerPlayer player){
        setXpInternal(player, 0, false, false, false);
    }


    //Util
    private static void updateClient(@NotNull ServerPlayer player, float gains, boolean triggersOverlay) {
        PacketDistributor.sendToPlayer(player, new UpdatePayloads.XpPayload(getXP(player), gains, triggersOverlay));
    }


    //Getters
    public static float getXP(@NotNull ServerPlayer player){return player.getData(PlayerData.DATA).getXP();}


}
