package com.tyzsskills.impl.server.Level;

import com.tyzsskills.impl.server.attachments.PlayerData;
import com.tyzsskills.impl.server.payloads.LevelDataUpdatePayload;
import com.tyzsskills.impl.server.payloads.LevelUpdatePayload;
import com.tyzsskills.impl.server.xp.XpManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class LevelManager {

    public static void setLevel(ServerPlayer player, int level){
        var playerData = player.getData(PlayerData.DATA);
        playerData.setLevel(level);

        updateClient(player);
    }

    public static void addLevel(ServerPlayer player, int level){
        if(level <= 0) return;
        setLevel(player, getLevel(player) + level);
    }

    public static void removeLevel(ServerPlayer player, int level){
        if(level <= 0) return;
        var result = Math.max(1, getLevel(player) - level);
        setLevel(player, result);
    }

    //Util
    private static void updateClient(ServerPlayer player){
        PacketDistributor.sendToPlayer(player, new LevelUpdatePayload(getLevel(player)));
        PacketDistributor.sendToPlayer(player, new LevelDataUpdatePayload(XpManager.getLevelData(getLevel(player))));
    }

    //Getter
    public static int getLevel(ServerPlayer player){return Math.max(1, player.getData(PlayerData.DATA).getLevel());}

}
