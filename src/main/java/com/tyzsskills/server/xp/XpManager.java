package com.tyzsskills.server.xp;

import com.google.gson.JsonObject;
import com.tyzsskills.server.active.AttributeRegistry;
import com.tyzsskills.server.active.LevelManager;
import com.tyzsskills.server.active.SpManager;
import com.tyzsskills.server.attachments.StatsTracker;
import com.tyzsskills.server.payloads.LevelToastPayload;
import com.tyzsskills.server.payloads.StatsSpEarnedPayload;
import com.tyzsskills.server.payloads.StatsXpPayload;
import com.tyzsskills.server.payloads.XpUpdatePayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;


public class XpManager {

    private static final String dataKey = "SKILL_XP";

    public record LevelData(float goal, int reward) {
    }

    private static final LevelData fallback = new LevelData(Float.MAX_VALUE, 0);

    private static final Map<Integer, LevelData> POOL = new HashMap<>();

    //Setup
    public static void LoadPool(JsonObject obj) {
        for (var key : obj.keySet()) {
            try {
                int level = Integer.parseInt(key);
                var data = obj.getAsJsonObject(key);

                float goal = data.has("goal") ? data.get("goal").getAsFloat() : fallback.goal();
                int reward = data.has("reward") ? data.get("reward").getAsInt() : fallback.reward();

                if (goal <= 0 || reward < 0 || level < -1) continue;
                POOL.put(level, new LevelData(goal, reward));
            } catch (Exception ex) {
                continue;
            }
        }
    }


    //Actifs
    private static void SetXPInternal(ServerPlayer player, float amount, float gains) {
        if (amount < 0f) return;

        var data = player.getPersistentData();
        data.putFloat(dataKey, amount);

        LevelUpCheck(player);

        UpdateClient(player, gains);

        if (gains > 0) {
            player.getData(StatsTracker.DATA).addXp(gains);
            PacketDistributor.sendToPlayer(player, new StatsXpPayload(gains));
        }

    }

    public static void SetXP(ServerPlayer player, float amount) {
        SetXPInternal(player, amount, 0f);
    }

    public static void AddXP(ServerPlayer player, float amount) {
        if (amount <= 0) return;
        SetXPInternal(player, GetXP(player) + amount, amount);
    }

    public static void RemoveXP(ServerPlayer player, float amount) {
        if (amount <= 0) return;
        var result = Math.max(0f, GetXP(player) - amount);
        SetXPInternal(player, result, 0f);
    }

    public static void RestorePlayerXPData(ServerPlayer oldPlayer, ServerPlayer newPlayer) {

        var oldData = oldPlayer.getPersistentData();
        var newData = newPlayer.getPersistentData();

        if (oldData.contains(dataKey)) {
            newData.putFloat(dataKey, oldData.getFloat(dataKey));
            UpdateClient(newPlayer, 0f);
        } else SetXP(newPlayer, 0f);
    }


    public static void ClearPool() {
        POOL.clear();
    }

    //Utilitaire
    private static void UpdateClient(ServerPlayer player, float gains) {
        PacketDistributor.sendToPlayer(player, new XpUpdatePayload(GetXP(player), gains));
    }

    public static void LevelUpCheck(ServerPlayer player) {

        int currentLevel = LevelManager.GetLevel(player);
        float currentXp = GetXP(player);

        int spBuffer = 0;
        int levelBuffer = 0;
        boolean flag = false;

        var multiplierAttribute = player.getAttributeValue(AttributeRegistry.SP_MULTIPLIER);

        while (true) {
            LevelData data = GetLevelData(currentLevel);

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
                SpManager.AddSP(player, spBuffer);
                PacketDistributor.sendToPlayer(player, new StatsSpEarnedPayload(spBuffer));
                player.getData(StatsTracker.DATA).addSpEarned(spBuffer);
            }
        }
        if (levelBuffer > 0) {
            LevelManager.AddLevel(player, levelBuffer);
            PacketDistributor.sendToPlayer(player, new LevelToastPayload(
                    currentLevel, spBuffer
            ));
        }

        player.getPersistentData().putFloat(dataKey, currentXp);

    }



    //Getters
    public static float GetXP(ServerPlayer player){return player.getPersistentData().getFloat(dataKey);}

    public static LevelData GetLevelData(int lvl){
        if(POOL.containsKey(lvl)) return POOL.get(lvl);
        else if(POOL.containsKey(-1)) return POOL.get(-1);

        return fallback;
    }


}
