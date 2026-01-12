package com.tyzsskills.client;

import com.tyzsskills.client.screen.MainGUI;
import com.tyzsskills.server.model.*;
import com.tyzsskills.server.xp.XpManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientCache {

    private static int clientLevel = 1;
    private static int clientSP = 0;
    private static float clientXP = 0f;

    private static XpManager.LevelData clientLevelData = new XpManager.LevelData(100f, 1);

    private final static List<Skill> clientSkills = new ArrayList<>();
    private final static Map<String, Integer> clientSkillLevels = new HashMap<>();
    private final static Map<String, Object> clientConfigMap = new HashMap<>();

    private static MainGUI.ContainerType currentContainerType = MainGUI.ContainerType.SKILLS;
    private static MainGUI.CategoryType currentContainerCatgory = MainGUI.CategoryType.ALL;


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

    public static void SetContainerType(MainGUI.ContainerType type){
        currentContainerType = MainGUI.ContainerType.SKILLS; //Temporaire le temps de gérer les quetes
    }

    public static void SetCategoryType(MainGUI.CategoryType category){
        currentContainerCatgory = category; //Temporaire le temps de gérer les autres categories
    }

    public static void UpdateSkills(List<Skill> skills){
        clientSkills.clear();
        clientSkills.addAll(skills);
        Minecraft.getInstance().player.displayClientMessage(Component.literal("Client skills sync: " + clientSkills.size() + " skills cached" ), false);
    }

    public static void UpdateSkillLevels(String id, int lvl){
        clientSkillLevels.put(id, lvl);
        Minecraft.getInstance().player.displayClientMessage(Component.literal("New skill level: " + id + " level " + lvl ), false);
    }

    public static void SyncConfig(Map<String, Object> syncedMap){
        clientConfigMap.clear();
        clientConfigMap.putAll(syncedMap);
        Minecraft.getInstance().player.displayClientMessage(Component.literal("Synced config: " + clientConfigMap.size() + " entries"), false);
    }

    public static void ClearCache(){
        clientLevel = 1;
        clientSP = 0;
        clientXP = 0f;
        clientLevelData = new XpManager.LevelData(100f, 1);
        clientSkills.clear();
        clientSkillLevels.clear();
        clientConfigMap.clear();
    }


    //getters
    public static float GetXP(){return clientXP;}
    public static int GetSP(){return clientSP;}
    public static int GetLvl(){return clientLevel;}
    public static float GetXPGOAL(){return clientLevelData.goal();}
    public static MainGUI.ContainerType GetContainerType(){return currentContainerType;}
    public static MainGUI.CategoryType GetCategoryType(){return currentContainerCatgory;}
    public static List<Skill> GetAllSkills(){return new ArrayList<>(clientSkills);}
    public static int GetSkillLevel(String id){return clientSkillLevels.getOrDefault(id, 0);}

    //getters config
    public static boolean GetConfigBool(String id, boolean fallback){
        var value = clientConfigMap.get(id);
        if(value instanceof Boolean bool) return bool;
        else return fallback;
    }

    public static double GetConfigDouble(String id, double fallback){
        var value = clientConfigMap.get(id);
        if(value instanceof Double dbl) return dbl;
        else if(value instanceof Number nbr) return nbr.doubleValue();
        else return fallback;
    }
}
