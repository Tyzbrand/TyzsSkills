package com.tyzsskills.client;

import com.tyzsskills.server.xp.XpManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;

public class ClientCache {

    private static int clientLevel = 1;
    private static int clientSP = 0;
    private static float clientXP = 0f;

    private static XpManager.LevelData clientLevelData = new XpManager.LevelData(100f, 1);



    public static void UpdateClientCacheLevel(int level){
        clientLevel = level;
        Minecraft.getInstance().player.displayClientMessage(Component.literal("Client Level Update: " + level), false);
    }

    public static void UpdateClientCacheSP(int sp){
        clientSP = sp;
        Minecraft.getInstance().player.displayClientMessage(Component.literal("Client SP Update: " + sp), false);
    }

    public static void UpdateClientCacheXP(float xp){
        clientXP = xp;
        Minecraft.getInstance().player.displayClientMessage(Component.literal("Client XP Update: " + xp), false);
    }

    public static void UpdateClientCacheLevelData(XpManager.LevelData data){
        clientLevelData = data;
        Minecraft.getInstance().player.displayClientMessage(Component.literal("Client LevelData Update: " + data.goal() + "xp, " + data.reward() + "sp"), false);
    }

    public static void ClearCache(){
        clientLevel = 1;
        clientSP = 0;
        clientXP = 0f;
        clientLevelData = new XpManager.LevelData(100f, 1);
    }
}
