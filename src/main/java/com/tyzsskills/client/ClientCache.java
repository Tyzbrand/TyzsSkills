package com.tyzsskills.client;

import com.tyzsskills.client.screen.MainGUI;
import com.tyzsskills.server.model.PassiveSkill;
import com.tyzsskills.server.model.Skill;
import com.tyzsskills.server.xp.XpManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientCache {

    private static int clientLevel = 1;
    private static int clientSP = 0;
    private static float clientXP = 0f;

    private static XpManager.LevelData clientLevelData = new XpManager.LevelData(100f, 1);

    private static List<PassiveSkill> clientSkills = new ArrayList<>();

    private static MainGUI.ContainerType currentContainerType = MainGUI.ContainerType.SKILLS;


    public static void UpdateClientCacheLevel(int level){
        clientLevel = level;
        //Minecraft.getInstance().player.displayClientMessage(Component.literal("Client Level Update: " + level), false);
    }

    public static void UpdateClientCacheSP(int sp){
        clientSP = sp;
        //Minecraft.getInstance().player.displayClientMessage(Component.literal("Client SP Update: " + sp), false);
    }

    public static void UpdateClientCacheXP(float xp){
        clientXP = xp;
        //Minecraft.getInstance().player.displayClientMessage(Component.literal("Client XP Update: " + xp), false);
    }

    public static void UpdateClientCacheLevelData(XpManager.LevelData data){
        clientLevelData = data;
        Minecraft.getInstance().player.displayClientMessage(Component.literal("Client LevelData Update: " + data.goal() + "xp, " + data.reward() + "sp"), false);
    }

    public static void SetContainerType(MainGUI.ContainerType type){
        currentContainerType = MainGUI.ContainerType.SKILLS; //Temporaire le temps de gérer les quetes
    }

    public static void ClearCache(){
        clientLevel = 1;
        clientSP = 0;
        clientXP = 0f;
        clientLevelData = new XpManager.LevelData(100f, 1);
    }


    //getters
    public static float GetXP(){return clientXP;}
    public static int GetSP(){return clientSP;}
    public static int GetLvl(){return clientLevel;}
    public static float GetXPGOAL(){return clientLevelData.goal();}
    public static MainGUI.ContainerType GetContainerType(){return currentContainerType;}
}
