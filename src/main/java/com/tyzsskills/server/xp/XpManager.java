package com.tyzsskills.server.xp;

import com.google.gson.JsonObject;
import com.tyzsskills.server.active.LevelManager;
import com.tyzsskills.server.active.SpManager;
import com.tyzsskills.server.payloads.XpUpdatePayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;


public class XpManager {

    private static final String dataKey = "SKILL_XP";

    private record LevelData(float goal, int reward) {}
    private static LevelData fallback = new LevelData(100f, 1);

    private static final Map<Integer, LevelData> POOL = new HashMap<>();

    //Setup
    public static void LoadPool(JsonObject obj){
        for(var key : obj.keySet()){
            try{
                int level = Integer.parseInt(key);
                var data = obj.getAsJsonObject(key);

                float goal = data.has("goal")? data.get("goal").getAsFloat() : fallback.goal();
                int reward = data.has("reward")? data.get("reward").getAsInt() : fallback.reward();

                if(goal <= 0 || reward < 0 || level < -1) continue;
                POOL.put(level, new LevelData(goal, reward));
            }
            catch (Exception ex){continue;}
        }
    }



    //Actifs
    public static void SetXP(ServerPlayer player, float amount){
        if(amount < 0) return;

        var playerData = player.getPersistentData();
        playerData.putFloat(dataKey, amount);

        if(!LevelUpCheck(player)) UpdateClient(player);
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

    public static void ClearPool(){
        POOL.clear();
    }

    //Utilitaire
    private static void UpdateClient(ServerPlayer player){
        PacketDistributor.sendToPlayer(player, new XpUpdatePayload(GetXP(player)));
    }

    private static boolean LevelUpCheck(ServerPlayer player){

        boolean flag = false; //Est ce que ça a changé

        int currentLevel = LevelManager.GetLevel(player);
        float currentXp = GetXP(player);

        int spBuffer = 0;
        int levelBuffer = 0;

        while (true){
            LevelData data = GetLevelData(currentLevel);

            if(currentXp >= data.goal()){

                currentXp -= data.goal();
                flag = true;

                currentLevel++;
                levelBuffer++;

                spBuffer += data.reward();
            }
            else break;
        }

        if(flag) {

            if(spBuffer > 0){SpManager.AddSP(player, spBuffer);}
            if(levelBuffer > 0){LevelManager.AddLevel(player, levelBuffer);}

            player.getPersistentData().putFloat(dataKey, currentXp);
            UpdateClient(player);
        }
        return flag;
    }


    //Getters
    public static float GetXP(ServerPlayer player){
        return player.getPersistentData().getFloat(dataKey);
    }

    private static LevelData GetLevelData(int lvl){
        if(POOL.containsKey(lvl)) return POOL.get(lvl);
        else if(POOL.containsKey(-1)) return POOL.get(-1);

        return fallback;
    }


}
