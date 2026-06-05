package com.tyzsskills.impl.server.Level;

import com.tyzsskills.Config;
import com.tyzsskills.api.events.SkillLevelChangeEvent;
import com.tyzsskills.impl.server.attachments.PlayerData;
import com.tyzsskills.impl.server.payloads.UpdatePayloads;
import com.tyzsskills.impl.server.xp.XpManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class LevelManager {

    //CORE
    private static void setLevelInternal(ServerPlayer player, int level, boolean applyLimits){
        int oldLevel = getLevel(player);

        var event = new SkillLevelChangeEvent(player, oldLevel, level);
        NeoForge.EVENT_BUS.post(event);

        if(event.isCanceled()) return;

        int targetedLvl = event.getNewLevel();
        if(targetedLvl == oldLevel) return;

        int gain = targetedLvl - oldLevel;

        if(gain > 0 && applyLimits){
            var allowedGain = checkLimit(player, gain);
            if(allowedGain <= 0) return;
            targetedLvl = oldLevel + allowedGain;
        }

        var playerData = player.getData(PlayerData.DATA);
        playerData.setLevel(targetedLvl);

        updateClient(player);
    }

    //PUBLIC
    public static void setLevel(ServerPlayer player, int level, boolean applyLimits){
        setLevelInternal(player, level, applyLimits);
    }
    public static void setLevel(ServerPlayer player, int level){
        setLevel(player, level, false);
    }

    public static void addLevel(ServerPlayer player, int level, boolean applyLimits){
        if(level <= 0) return;
        setLevelInternal(player, level + getLevel(player), applyLimits);
    }
    public static void addLevel(ServerPlayer player, int level){
        addLevel(player, level, true);
    }

    public static void removeLevel(ServerPlayer player, int level){
        if(level <= 0) return;
        var result = Math.max(1, getLevel(player) - level);
        setLevelInternal(player, result, false);
    }

    //Util
    private static void updateClient(ServerPlayer player){
        PacketDistributor.sendToPlayer(player, new UpdatePayloads.LevelPayload(getLevel(player)));
        PacketDistributor.sendToPlayer(player, new UpdatePayloads.LevelDataPayload(XpManager.getLevelData(getLevel(player))));
    }

    private static int checkLimit(ServerPlayer player, int amount){
        int currentLvl = getLevel(player);
        int finalAmount = amount;
        int limit = Config.MAX_LEVEL.get();

        if(limit != -1){
            var remaining = limit - currentLvl;
            finalAmount = Math.min(finalAmount, Math.max(0, remaining));
        }

        return finalAmount;
    }

    //Getter
    public static int getLevel(ServerPlayer player){return Math.max(1, player.getData(PlayerData.DATA).getLevel());}
    public static boolean isLevelMax(ServerPlayer player){
        int limit = Config.MAX_LEVEL.get();
        return limit != -1 && getLevel(player) >= limit;
    }

}
