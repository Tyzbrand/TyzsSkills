package com.tyzsskills.impl.server.core;

import com.tyzsskills.Settings;
import com.tyzsskills.impl.server.attachments.PlayerData;
import com.tyzsskills.impl.server.payloads.UpdatePayloads;
import com.tyzsskills.impl.server.sp.SpManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.TimeZone;

@ApiStatus.Internal
public class LimitManager {
    //IMPLEMENTATION
    public static float clampXpGain(@NotNull ServerPlayer player, float amount){
        if(amount <= 0f) return 0f;

        float xpLimit = Settings.Server.XP_LIMIT.get();
        if(xpLimit <= 0f) return amount;

        return Math.clamp(xpLimit - getXpQuota(player), 0f, amount);
    }

    public static int clampSpGain(@NotNull ServerPlayer player, int amount){
        if(amount <= 0) return 0;

        var allowed = amount;

        int cap = Settings.Server.SP_CAP.get();
        if (cap > 0) allowed = Math.clamp(cap - SpManager.getSP(player), 0, allowed);

        int spLimit = Settings.Server.SP_LIMIT.get();
        if(spLimit > 0) allowed = (int)Math.clamp(spLimit - getSpQuota(player), 0f, allowed);

        return allowed;
    }

    public static void recordXpGain(ServerPlayer player, float amount) {
        if(amount <= 0f) return;
        syncBucket(player);
        player.getData(PlayerData.DATA).earnXp(amount);
        updateClient(player);
    }

    public static void recordSpGain(ServerPlayer player, int amount) {
        if(amount <= 0) return;
        syncBucket(player);
        player.getData(PlayerData.DATA).earnSp(amount);
        updateClient(player);
    }

    public static void resetLimits(@NotNull ServerPlayer player, long newBucket){
        player.getData(PlayerData.DATA).resetLimits(newBucket);
    }

    //CORE
    private static long getCurrentBucket(@NotNull ServerPlayer player){
        long interval = Settings.Server.LIMIT_RESET_INTERVAL.get();
        if(interval == 0) return 0L;

        return switch (Settings.Server.LIMIT_TYPE.get()){
            case REAL_TIME -> {
                var now = System.currentTimeMillis();
                var offset = TimeZone.getDefault().getOffset(now);
                yield (now + offset) / (interval * 60000L);
            }
            case GAME_TIME -> player.server.overworld().getGameTime() / (interval * 1200L);
        };
    }

    private static void syncBucket(@NotNull ServerPlayer player){
        var currentBucket = getCurrentBucket(player);
        if(currentBucket != player.getData(PlayerData.DATA).getLastResetBucket()) resetLimits(player, currentBucket);
    }

    private static void updateClient(@NotNull ServerPlayer player){
        if(Settings.Server.XP_LIMIT.get() <= 0f && Settings.Server.SP_LIMIT.get() <= 0) return;
        var data = player.getData(PlayerData.DATA);
        PacketDistributor.sendToPlayer(player, new UpdatePayloads.LimitsPayload(data.getXpQuota(), data.getSpQuota(), data.getLastResetBucket()));
    }

    //GETTERS
    public static boolean isLevelCapped(int level){
        int cap = Settings.Server.LEVEL_CAP.get();
        return cap > 0 && level >= cap;
    }

    public static long getLastResetBucket(@NotNull ServerPlayer player){
        syncBucket(player);
        return player.getData(PlayerData.DATA).getLastResetBucket();
    }

    public static float getXpQuota(@NotNull ServerPlayer player){
        syncBucket(player);
        return player.getData(PlayerData.DATA).getXpQuota();
    }

    public static int getSpQuota(@NotNull ServerPlayer player){
        syncBucket(player);
        return player.getData(PlayerData.DATA).getSpQuota();
    }
}
