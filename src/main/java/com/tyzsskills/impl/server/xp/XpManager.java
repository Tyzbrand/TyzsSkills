package com.tyzsskills.impl.server.xp;

import com.google.gson.JsonObject;
import com.tyzsskills.impl.server.active.AttributeRegistry;
import com.tyzsskills.impl.server.Level.LevelManager;
import com.tyzsskills.impl.server.sp.SpManager;
import com.tyzsskills.impl.server.attachments.PlayerData;
import com.tyzsskills.impl.server.attachments.StatsTracker;
import com.tyzsskills.impl.server.payloads.LevelToastPayload;
import com.tyzsskills.impl.server.payloads.StatsSpEarnedPayload;
import com.tyzsskills.impl.server.payloads.StatsXpPayload;
import com.tyzsskills.impl.server.payloads.XpUpdatePayload;
import net.minecraft.server.level.ServerPlayer;
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


    //Actifs
    private static void setXPInternal(ServerPlayer player, float amount, float gains) {
        var data = player.getData(PlayerData.DATA);
        data.setXP(amount);

        levelUpCheck(player);

        updateClient(player, gains);

        if (gains > 0) {
            player.getData(StatsTracker.DATA).addXp(gains);
            PacketDistributor.sendToPlayer(player, new StatsXpPayload(gains));
        }
    }

    public static void setXP(ServerPlayer player, float amount) {
        setXPInternal(player, amount, 0f);
    }

    public static void addXP(ServerPlayer player, float amount) {
        if (amount <= 0) return;
        setXPInternal(player, getXP(player) + amount, amount);
    }

    public static void removeXP(ServerPlayer player, float amount) {
        if (amount <= 0) return;
        var result = Math.max(0f, getXP(player) - amount);
        setXPInternal(player, result, 0f);
    }

    public static void clearPool() {POOL.clear();}

    //Util
    private static void updateClient(ServerPlayer player, float gains) {
        PacketDistributor.sendToPlayer(player, new XpUpdatePayload(getXP(player), gains));
    }

    public static void levelUpCheck(ServerPlayer player) {

        int currentLevel = LevelManager.getLevel(player);
        float currentXp = getXP(player);

        int spBuffer = 0;
        int levelBuffer = 0;
        boolean flag = false;

        var multiplierAttribute = player.getAttributeValue(AttributeRegistry.SP_MULTIPLIER);

        while (true) {
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



    //Getters
    public static float getXP(ServerPlayer player){return player.getData(PlayerData.DATA).getXP();}

    public static LevelData getLevelData(int lvl){
        if(POOL.containsKey(lvl)) return POOL.get(lvl);
        else if(POOL.containsKey(-1)) return POOL.get(-1);

        return FALLBACK;
    }


}
