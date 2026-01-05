package com.tyzsskills.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class ClientCache {

    private static int clientLevel = 1;
    private static int clientSP = 0;
    private static float clientXP = 0f;

    public static void UpdateClientMainCache(int level, int sp, float xp){
        clientLevel = level;
        clientSP = sp;
        clientXP = xp;

        Minecraft.getInstance().player.displayClientMessage(Component.literal("Client Update " +
                "lvl: " + level +
                " SP: " + sp +
                " XP: " + xp), false);
    }

    public static void ClearCache(){
        clientLevel = 1;
        clientSP = 0;
        clientXP = 0f;
    }
}
