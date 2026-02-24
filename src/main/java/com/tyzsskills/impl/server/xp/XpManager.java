package com.tyzsskills.impl.server.xp;

import com.google.gson.JsonObject;
import com.tyzsskills.Config;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.events.SkillXPChangeEvent;
import com.tyzsskills.impl.server.active.AttributeRegistry;
import com.tyzsskills.impl.server.Level.LevelManager;
import com.tyzsskills.impl.server.attachments.LimitsTracker;
import com.tyzsskills.impl.server.sp.SpManager;
import com.tyzsskills.impl.server.attachments.PlayerData;
import com.tyzsskills.impl.server.attachments.StatsTracker;
import com.tyzsskills.impl.server.payloads.LevelToastPayload;
import com.tyzsskills.impl.server.payloads.StatsSpEarnedPayload;
import com.tyzsskills.impl.server.payloads.StatsXpPayload;
import com.tyzsskills.impl.server.payloads.XpUpdatePayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

@ApiStatus.Internal
public class XpManager {

    public record LevelData(float goal, int reward) {}

    private static final LevelData FALLBACK = new LevelData(Float.MAX_VALUE, 0);

    private static final Map<Integer, LevelData> POOL = new HashMap<>();

    //Setup
    public static void loadPool(JsonObject obj) {
        for (var key : obj.keySet()) {
            try {
                int level = Integer.parseInt(key);
                var data = obj.getAsJsonObject(key);

                float goal = data.has("goal") ? data.get("goal").getAsFloat() : FALLBACK.goal();
                int reward = data.has("reward") ? data.get("reward").getAsInt() : FALLBACK.reward();

                if (goal <= 0 || reward < 0 || level < -1) continue;
                POOL.put(level, new LevelData(goal, reward));
            } catch (Exception ex) {
                continue;
            }
        }
    }


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
            PacketDistributor.sendToPlayer(player, new StatsXpPayload(gains));
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

    public static void clearPool() {POOL.clear();}

    //Util
    private static void updateClient(ServerPlayer player, float gains, boolean triggersOverlay) {
        PacketDistributor.sendToPlayer(player, new XpUpdatePayload(getXP(player), gains, triggersOverlay, getLimitPercentage(player)));
    }

    public static void levelUpCheck(ServerPlayer player) {

        int currentLevel = LevelManager.getLevel(player);
        float currentXp = getXP(player);
        int levelLimit = Config.MAX_LEVEL.get();

        int spBuffer = 0;
        int levelBuffer = 0;
        boolean flag = false;

        var multiplierAttribute = player.getAttributeValue(AttributeRegistry.SP_MULTIPLIER);

        while (true) {
            if (levelLimit != -1 && currentLevel >= levelLimit) break;

            LevelData data = getLevelData(currentLevel);

            if (currentXp >= data.goal()) {

                currentXp -= data.goal();
                flag = true;

                currentLevel++;
                levelBuffer++;

                long spGains = Math.round(data.reward() * multiplierAttribute);
                spBuffer += (int) Math.max(spGains, 1);
            } else break;
        }

        if (flag) {

            if (spBuffer > 0) {
                SpManager.addSP(player, spBuffer);
                PacketDistributor.sendToPlayer(player, new StatsSpEarnedPayload(spBuffer));
                player.getData(StatsTracker.DATA).addSpEarned(spBuffer);
            }
        }
        if (levelBuffer > 0) {
            LevelManager.addLevel(player, levelBuffer);
            PacketDistributor.sendToPlayer(player, new LevelToastPayload(
                    currentLevel, spBuffer
            ));
        }

        player.getData(PlayerData.DATA).setXP(currentXp);

    }

    private static float checkLimit(ServerPlayer player, float amount){
        if(LevelManager.isLevelMax(player)) return 0f;

        double limit = Config.XP_DAILY_LIMIT.get();
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

    public static LevelData getLevelData(int lvl){
        if(POOL.containsKey(lvl)) return POOL.get(lvl);
        else if(POOL.containsKey(-1)) return POOL.get(-1);

        return FALLBACK;
    }

    public static float getLimitPercentage(ServerPlayer player){
        float limitFraction = -1f;
        double limitConfig = Config.XP_DAILY_LIMIT.get();

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
