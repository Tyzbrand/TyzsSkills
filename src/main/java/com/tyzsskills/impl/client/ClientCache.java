package com.tyzsskills.impl.client;

import com.tyzsskills.Config;
import com.tyzsskills.api.Enums;
import com.tyzsskills.impl.client.screen.XpTriggerOverlay;
import com.tyzsskills.impl.server.model.*;
import com.tyzsskills.impl.server.xp.XpManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.*;

public class ClientCache {

    private static int clientLevel = 1;
    private static int clientSP = 0;
    private static float clientXP = 0f;
    private static int clientPower = 0;

    private static float clientXpLimit = 0f;

    private static float clientAllTimeXP = 0f;
    private static float clientSessionXP = 0f;
    private static int clientSpEarned = 0;
    private static int clientSpSpent = 0;
    private static int clientOwnedSkills = 0;

    private static XpManager.LevelData clientLevelData = new XpManager.LevelData(100f, 1);

    private final static Map<String, Skill> clientSkills = new HashMap<>();
    private final static Map<String, Integer> clientSkillLevels = new HashMap<>();
    private final static Map<String, Object> clientConfigMap = new HashMap<>();
    private final static HashSet<String> clientBookmarks = new HashSet<>();

    private static Enums.ContainerType currentContainerType = Enums.ContainerType.SKILLS;
    private static Enums.CategoryType currentContainerCategory = Enums.CategoryType.ALL;


    public static void UpdateClientCacheLevel(int level){
        clientLevel = level;
        if(Config.SHOW_DEBUG_MESSAGES.get()){
            Minecraft.getInstance().player.displayClientMessage(Component.literal("Client Level Update: " + level), false);
        }
    }

    public static void UpdateClientCacheSP(int sp){
        clientSP = sp;
        if(Config.SHOW_DEBUG_MESSAGES.get()){
            Minecraft.getInstance().player.displayClientMessage(Component.literal("Client SP Update: " + sp), false);
        }
    }

    public static void UpdateClientCachePower(int power){
        clientPower = power;
        if(Config.SHOW_DEBUG_MESSAGES.get()){
            Minecraft.getInstance().player.displayClientMessage(Component.literal("Client Power Update: " + power), false);
        }
    }

    public static void UpdateClientCacheXP(float xp, float gained, boolean triggersOverlay, float limit){
        if(gained > 0) {
            XpTriggerOverlay.AddXp(gained, triggersOverlay);
            clientSessionXP += gained;
        }
        clientXP = xp;
        clientXpLimit = limit;

        if(Config.SHOW_DEBUG_MESSAGES.get()){
            Minecraft.getInstance().player.displayClientMessage(Component.literal("Client XP Update: " + xp + ", limit: " + limit), false);
        }
    }

    public static void UpdateClientStatXP(float amount){
        if(amount > 0f)clientAllTimeXP += amount;

        if(Config.SHOW_DEBUG_MESSAGES.get()){
            Minecraft.getInstance().player.displayClientMessage(Component.literal("Client XP stats Update"), false);
        }
    }

    public static void UpdateClientStatSpEarned(int amount){
        if(amount > 0)clientSpEarned += amount;

        if(Config.SHOW_DEBUG_MESSAGES.get()){
            Minecraft.getInstance().player.displayClientMessage(Component.literal("Client SP e stats Update"), false);
        }
    }

    public static void UpdateClientStatSpSpent(int amount){
        if(amount > 0)clientSpSpent += amount;

        if(Config.SHOW_DEBUG_MESSAGES.get()){
            Minecraft.getInstance().player.displayClientMessage(Component.literal("Client SP s stats Update"), false);
        }
    }

    public static void UpdateClientCacheLevelData(XpManager.LevelData data){
        clientLevelData = data;

        if(Config.SHOW_DEBUG_MESSAGES.get()){
            Minecraft.getInstance().player.displayClientMessage(Component.literal("Client LevelData Update: " + data.goal() + "xp, " + data.reward() + "sp"), false);
        }
    }

    public static void SetContainerType(Enums.ContainerType type){
        if(!GetConfigBool(Config.TRAIT_SYSTEM_KEY, true) && type == Enums.ContainerType.TRAITS) return;
        if(GetConfigInt(Config.TRAIT_UNLOCK_LEVEL_KEY, 20) > clientLevel && type == Enums.ContainerType.TRAITS) return;
        currentContainerType = type;
    }

    public static void SetCategoryType(Enums.CategoryType category){
        if(!GetConfigBool(Config.TRAIT_SYSTEM_KEY, true) && category == Enums.CategoryType.TRAITS) return;
        if(GetConfigInt(Config.TRAIT_UNLOCK_LEVEL_KEY, 20) > clientLevel && category == Enums.CategoryType.TRAITS) return;
        currentContainerCategory = category;
    }

    public static void UpdateSkills(List<Skill> skills){
        clientSkills.clear();
        for(var skill : skills){clientSkills.put(skill.getID(), skill);}

        if(Config.SHOW_DEBUG_MESSAGES.get()){
            Minecraft.getInstance().player.displayClientMessage(Component.literal("Client skills sync: " + clientSkills.size() + " skills cached" ), false);
        }

    }

    public static void UpdateSkillLevels(String id, int lvl){
        clientSkillLevels.put(id.toLowerCase(), lvl);

        int owned = 0;
        for(var skill : clientSkills.values()){
            if(skill instanceof Trait) continue;
            if(!clientSkillLevels.containsKey(skill.getID().toLowerCase())) continue;
            owned += clientSkillLevels.get(skill.getID().toLowerCase());
        }
        clientOwnedSkills = owned;

        if(Config.SHOW_DEBUG_MESSAGES.get()){
            Minecraft.getInstance().player.displayClientMessage(Component.literal("New skill level: " + id + " level " + lvl ), false);
        }
    }

    public static void SyncConfig(Map<String, Object> syncedMap){
        clientConfigMap.clear();
        clientConfigMap.putAll(syncedMap);

        if(Config.SHOW_DEBUG_MESSAGES.get()){
            Minecraft.getInstance().player.displayClientMessage(Component.literal("Synced config: " + clientConfigMap.size() + " entries"), false);
            for (var entry : clientConfigMap.entrySet()) Minecraft.getInstance().player.displayClientMessage(Component.literal(entry.getKey() + "->" + entry.getValue()), false);
        }

    }

    public static void SyncBookmark(String id, boolean state){
        if(!state) clientBookmarks.remove(id.toLowerCase());
        else clientBookmarks.add(id.toLowerCase());

        if(Config.SHOW_DEBUG_MESSAGES.get()){
            Minecraft.getInstance().player.displayClientMessage(Component.literal("Synced bookmark: " + id + ": " + state), false);
        }
    }




    public static void PredictBookmark(Skill skill){
        String id = skill.getID();
        if(isSkillBookmarked(id)) clientBookmarks.remove(id);
        else clientBookmarks.add(id);
    }

    public static void PredictBuy(Skill skill) {
        String id = skill.getID().toLowerCase();
        int currentLvl = GetSkillLevel(id);

        if(skill instanceof Trait){
            if(!GetConfigBool(Config.TRAIT_SYSTEM_KEY, true)) return;
            if(clientLevel < GetConfigInt(Config.TRAIT_UNLOCK_LEVEL_KEY, 20)) return;
        }

        if (currentLvl >= skill.getMaximumLevel()) return;

        var prices = skill.getPrices();
        if (currentLvl >= prices.size()) return;
        int price = prices.get(currentLvl);

        clientSP -= price;
        UpdateSkillLevels(id, currentLvl + 1);

        if(skill instanceof Trait trait) clientPower += trait.getPowerWeight();
    }

    public static void PredictRefund(Skill skill) {
        String id = skill.getID().toLowerCase();
        int currentLvl = GetSkillLevel(id);

        if(skill instanceof Trait){
            if(!GetConfigBool(Config.TRAIT_SYSTEM_KEY, true)) return;
            if(clientLevel < GetConfigInt(Config.TRAIT_UNLOCK_LEVEL_KEY, 20)) return;
        }

        if (currentLvl <= 0) return;

        UpdateSkillLevels(id, currentLvl - 1);

        double percentage = GetConfigDouble(Config.REFUND_PERCENTAGE_KEY, 0);

        List<Integer> prices = skill.getPrices();
        if (currentLvl - 1 < prices.size()) {
            int initialPrice = prices.get(currentLvl - 1);
            int refundAmount = Math.max(1, (int)(initialPrice * (percentage / 100.0)));
            clientSP += refundAmount;
            if(skill instanceof Trait trait) clientPower -= trait.getPowerWeight();
        }


    }

    public static void ClearCache(Enums.ResetType type){
        switch (type){
            case ALL -> {
                resetMetadata();
                resetSkills();
                resetStats();
                resetLimits();
            }
            case METADATA -> resetMetadata();
            case SKILLS -> resetSkills();
            case STATS -> resetStats();
            case LIMITS -> resetLimits();
            case SHUTDOWN -> {
                resetMetadata();
                resetSkills();
                resetStats();
                resetLimits();
                shutDownReset();
            }
        }
    }


    //getters
    public static float GetXP(){return clientXP;}
    public static int GetSP(){return clientSP;}
    public static int GetLvl(){return clientLevel;}
    public static float GetXPGOAL(){return clientLevelData.goal();}
    public static int GetReward(){return clientLevelData.reward();}
    public static Enums.ContainerType GetContainerType(){return currentContainerType;}
    public static Enums.CategoryType GetCategoryType(){return currentContainerCategory;}
    public static int getLimitPercentage(){return (int)(clientXpLimit * 100);}

    public static List<Skill> GetAllSkills(){return new ArrayList<>(clientSkills.values());}
    public static List<String> GetAllSkillIDs(){return new ArrayList<>(clientSkills.keySet());}
    public static List<String> GetAllBookmarkedIDs(){return new ArrayList<>(clientBookmarks);}
    public static List<String> getAllActiveSkills(){return List.copyOf(clientSkillLevels.keySet());}

    public static int GetSkillLevel(String id){return clientSkillLevels.getOrDefault(id.toLowerCase(), 0);}
    public static Skill GetSkill(String id){return clientSkills.getOrDefault(id.toLowerCase(), null);}
    public static boolean isSkillBookmarked(String id){return clientBookmarks.contains(id.toLowerCase());}
    public static int GetPower(){return clientPower;}

    public static float GetAllTimeXp(){return clientAllTimeXP;}
    public static float GetSessionXp(){return clientSessionXP;}
    public static int GetSpEarned(){return clientSpEarned;}
    public static int GetSpSpent(){return clientSpSpent;}
    public static int GetUnlockedSkills(){return clientOwnedSkills;}
    public static int GetSkillCount(){
        int count = 0;
        for(var skill : clientSkills.values()){
            if(skill instanceof Trait) continue;
            count += skill.getMaximumLevel();
        }
        return count;
    }

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

    public static int GetConfigInt(String id, int fallback){
        var value = clientConfigMap.get(id);
        if(value instanceof Integer nbr) return nbr;
        else return fallback;
    }




    //UTIL
    public static int ParseColor(String hexString, int fallback) {
        if (hexString == null || hexString.isEmpty()) return fallback;
        try {
            String clean = hexString.replace("#", "");
            if (clean.length() == 6) clean = "FF" + clean;
            return (int) Long.parseLong(clean, 16);
        }
        catch (NumberFormatException e) {return fallback;}
    }

    private static void resetMetadata(){
        clientLevel = 1;
        clientSP = 0;
        clientXP = 0f;
        clientPower = 0;
        clientLevelData = new XpManager.LevelData(100f, 1);
    }

    private static void resetSkills(){
        clientSkillLevels.clear();
    }

    private static void resetStats(){
        clientSessionXP = 0f;
        clientAllTimeXP = 0f;
        clientSpEarned = 0;
        clientSpSpent = 0;
    }

    private static void resetLimits(){
        clientXpLimit = 0f;
    }

    private static void shutDownReset(){
        clientConfigMap.clear();
        clientBookmarks.clear();
        currentContainerCategory = Enums.CategoryType.ALL;
        currentContainerType = Enums.ContainerType.SKILLS;
        clientSkills.clear();
    }
}
