package com.tyzsskills.impl.server.Level;

import com.google.gson.JsonObject;
import com.tyzsskills.Tyzsskills;
import com.tyzsskills.api.events.SkillLevelChangeEvent;
import com.tyzsskills.api.records.LevelData;
import com.tyzsskills.impl.server.active.AttributeRegistry;
import com.tyzsskills.impl.server.attachments.PlayerData;
import com.tyzsskills.impl.server.payloads.LevelToastPayload;
import com.tyzsskills.impl.server.payloads.UpdatePayloads;
import com.tyzsskills.impl.server.sp.SpManager;
import com.tyzsskills.impl.server.xp.XpManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@ApiStatus.Internal
public class LevelManager {

    private static final LevelData FALLBACK = new LevelData(Float.MAX_VALUE, 0);

    private static final Map<Integer, LevelData> POOL = new HashMap<>();
    public static void clearPool() {POOL.clear();}

    //CORE
    private static boolean setLevelInternal(@NotNull ServerPlayer player, int newLevel, boolean syncClient){
        int oldLevel = getLevel(player);
        if(oldLevel == newLevel) return false;

        var event = new SkillLevelChangeEvent(player, oldLevel, newLevel);
        NeoForge.EVENT_BUS.post(event);
        if(event.isCanceled()) return false;

        int finalLevel = event.getNewLevel();
        if(finalLevel == oldLevel) return false;

        player.getData(PlayerData.DATA).setLevel(finalLevel);

        if(syncClient) updateClient(player);

        return true;
    }


    //PUBLIC
    public static boolean tryAddLevel(@NotNull ServerPlayer player, int amount){
        if(amount <= 0) return false;
        return setLevelInternal(player, getLevel(player) + amount, true);
    }

    public static boolean tryRemoveLevel(@NotNull ServerPlayer player, int amount){
        if(amount <= 0 || getLevel(player) <= amount) return false;
        return setLevelInternal(player, getLevel(player) - amount, true);
    }

    public static void setLevel(@NotNull ServerPlayer player, int newLevel){
        if(newLevel >= 1) setLevelInternal(player, newLevel, true);
    }

    public static void resetLevel(@NotNull ServerPlayer player){setLevelInternal(player, 1, false);}

    public static float checkForLevelUp(@NotNull ServerPlayer player, float currentXp){
        var currentLevel = getLevel(player);

        int spBuffer = 0;
        int levelBuffer = 0;
        var multiplicator = player.getAttributeValue(AttributeRegistry.SP_MULTIPLIER);

        while (true){
            var data = getLevelData(currentLevel);

            if(currentXp >= data.goal()){
                currentXp -= data.goal();
                spBuffer += (int)(data.reward() * multiplicator);
                currentLevel++;
                levelBuffer++;
            }
            else break;
        }

        if(spBuffer > 0) SpManager.tryAddSp(player, spBuffer);
        if(levelBuffer > 0) {
            tryAddLevel(player, levelBuffer);
            PacketDistributor.sendToPlayer(player, new LevelToastPayload(currentLevel, spBuffer));
        }

        return currentXp;
    }

    //Util
    private static void updateClient(@NotNull ServerPlayer player){
        PacketDistributor.sendToPlayer(player, new UpdatePayloads.LevelPayload(getLevel(player)));
        PacketDistributor.sendToPlayer(player, new UpdatePayloads.LevelDataPayload(getCurrentLevelData(player)));
    }

    //Getter
    public static int getLevel(@NotNull ServerPlayer player){return Math.max(1, player.getData(PlayerData.DATA).getLevel());}
    public static @NotNull LevelData getLevelData(int level){
        if(POOL.containsKey(level)) return POOL.get(level);
        if(POOL.containsKey(-1)) return POOL.get(-1);
        return FALLBACK;
    }
    public static @NotNull LevelData getCurrentLevelData(@NotNull ServerPlayer player){return getLevelData(getLevel(player));}

    //UTILS
    public static void parsePool(@NotNull JsonObject obj) {
        for (var key : obj.keySet()) {
            try {
                int level = Integer.parseInt(key);
                var data = obj.getAsJsonObject(key);

                float goal = data.has("goal") ? data.get("goal").getAsFloat() : FALLBACK.goal();
                int reward = data.has("reward") ? data.get("reward").getAsInt() : FALLBACK.reward();

                if (goal <= 0 || reward < 0 || level < -1) continue;
                POOL.put(level, new LevelData(goal, reward));
            } catch (Exception e) {
                Tyzsskills.LOGGER.error("Unable to load level pool", e);
            }
        }
    }


}
