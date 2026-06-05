package com.tyzsskills.impl.server.xp;

import com.google.gson.JsonObject;
import com.tyzsskills.Config;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.events.SkillXPChangeEvent;
import com.tyzsskills.api.records.LevelData;
import com.tyzsskills.impl.server.active.AttributeRegistry;
import com.tyzsskills.impl.server.Level.LevelManager;
import com.tyzsskills.impl.server.attachments.LimitsTracker;
import com.tyzsskills.impl.server.payloads.UpdatePayloads;
import com.tyzsskills.impl.server.sp.SpManager;
import com.tyzsskills.impl.server.attachments.PlayerData;
import com.tyzsskills.impl.server.attachments.StatsTracker;
import com.tyzsskills.impl.server.payloads.LevelToastPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;


@ApiStatus.Internal
public class XpManager {

    //CORE
    private static void setXPInternal(ServerPlayer player, float amount, boolean triggersOverlay, boolean applyLimits) {
        float oldAmount = getXP(player);

        var event = new SkillXPChangeEvent(player, oldAmount, amount);
        NeoForge.EVENT_BUS.post(event);

        if(event.isCanceled()) return;

        float targetedAmount = event.getNewAmount();
        if (oldAmount == targetedAmount) return;

        float gain = targetedAmount - oldAmount;
        if(gain > 0 && applyLimits){
            var allowedGain = checkLimit(player, gain);
            if(allowedGain <= 0) return;
            targetedAmount = oldAmount + allowedGain;
            player.getData(LimitsTracker.DATA).incrXp(allowedGain);
        }

        var data = player.getData(PlayerData.DATA);
        data.setXP(targetedAmount);

        levelUpCheck(player);

        float gains = targetedAmount - oldAmount;
        if(gains < 0) gains = 0;

        updateClient(player, gains, triggersOverlay);

        if (triggersOverlay && gains > 0) {
            player.getData(StatsTracker.DATA).addXp(gains);
        }
    }

    //PUBLIC
    public static void setXP(ServerPlayer player, float amount, boolean applyLimits) {
        setXPInternal(player, amount, false, applyLimits);
    }
    public static void setXP(ServerPlayer player, float amount){
        setXP(player, amount, false);
    }

    public static void addXP(ServerPlayer player, float amount, boolean triggerOverlay, boolean applyLimits) {
        if (amount <= 0) return;
        setXPInternal(player, getXP(player) + amount, triggerOverlay, applyLimits);
    }
    public static void addXP(ServerPlayer player, float amount){
        addXP(player, amount, true, true);
    }

    public static void removeXP(ServerPlayer player, float amount) {
        if (amount <= 0) return;
        var result = Math.max(0f, getXP(player) - amount);
        setXPInternal(player, result, false, false);
    }



    //Util
    private static void updateClient(ServerPlayer player, float gains, boolean triggersOverlay) {
        PacketDistributor.sendToPlayer(player, new UpdatePayloads.XpPayload(getXP(player), gains, triggersOverlay, getLimitPercentage(player)));
    }



    private static float checkLimit(ServerPlayer player, float amount){
        if(LevelManager.isLevelMax(player)) return 0f;

        double limit = Config.XP_LIMIT.get();
        if(limit == -1.0) return amount;

        var data = player.getData(LimitsTracker.DATA);

        long currentTime = System.currentTimeMillis();
        long cycleDuration = Config.CYCLE_DURATION.get() * 1000;

        if(currentTime - data.getLastCycleTimestamp() >= cycleDuration){
            data.startNewCycle(currentTime);
        }

        float maxXP;
        if(Config.LIMIT_TYPE.get() == Enums.LimitType.FIXED) maxXP = (float)limit;
        else{
            var currentGoal = getLevelData(LevelManager.getLevel(player)).goal();
            maxXP = currentGoal * ((float)limit / 100f);
        }

        var remaining = maxXP - data.getXpInCycle();
        return Math.max(0f, Math.min(amount, remaining));
    }

    //Getters
    public static float getXP(ServerPlayer player){return player.getData(PlayerData.DATA).getXP();}


    public static float getLimitPercentage(ServerPlayer player){
        float limitFraction = -1f;
        double limitConfig = Config.XP_LIMIT.get();

        if (limitConfig != -1.0) {
            var data = player.getData(LimitsTracker.DATA);


            float maxXP;
            if (Config.LIMIT_TYPE.get() == Enums.LimitType.FIXED) {
                maxXP = (float) limitConfig;
            } else {
                float currentGoal = getLevelData(LevelManager.getLevel(player)).goal();
                maxXP = currentGoal * ((float) limitConfig / 100f);
            }

            if (maxXP > 0) {
                limitFraction = Math.min(1.0f, data.getXpInCycle() / maxXP);
            }
        }
        return limitFraction;
    }


}
