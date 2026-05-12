package com.tyzsskills.impl.client;

import com.tyzsskills.Config;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.model.Category;
import com.tyzsskills.api.records.LevelData;
import com.tyzsskills.api.records.SkillContext;
import com.tyzsskills.impl.client.screen.XpTriggerOverlay;
import com.tyzsskills.impl.client.tools.SortingTools;
import com.tyzsskills.impl.server.model.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ClientCache {

    private static int clientLevel = 1;
    private static int clientSP = 0;
    private static float clientXP = 0f;

    private static float clientXpLimit = 0f;

    private static float clientAllTimeXP = 0f;
    private static float clientSessionXP = 0f;
    private static int clientSpEarned = 0;
    private static int clientSpSpent = 0;
    private static int clientOwnedSkills = 0;

    private static LevelData clientLevelData = new LevelData(100f, 1);

    private final static Map<String, Category> clientCategories = new HashMap<>();

    private final static Map<String, Skill> clientSkills = new HashMap<>();
    private final static Map<String, Integer> clientSkillLevels = new HashMap<>();
    private final static Map<String, Object> clientConfigMap = new HashMap<>();
    private final static HashSet<String> clientBookmarks = new HashSet<>();




    public static void updateClientCacheLevel(int level){
        clientLevel = level;
        if(Config.SHOW_DEBUG_MESSAGES.get()){
            Minecraft.getInstance().player.displayClientMessage(Component.literal("Client Level Update: " + level), false);
        }
    }

    public static void updateClientCacheSP(int sp){
        clientSP = sp;
        if(Config.SHOW_DEBUG_MESSAGES.get()){
            Minecraft.getInstance().player.displayClientMessage(Component.literal("Client SP Update: " + sp), false);
        }
    }

    public static void updateClientCacheXP(float xp, float gained, boolean triggersOverlay, float limit){
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

    public static void updateClientStatXP(float amount){
        if(amount > 0f)clientAllTimeXP += amount;

        if(Config.SHOW_DEBUG_MESSAGES.get()){
            Minecraft.getInstance().player.displayClientMessage(Component.literal("Client XP stats Update"), false);
        }
    }

    public static void updateClientStatSpEarned(int amount){
        if(amount > 0)clientSpEarned += amount;

        if(Config.SHOW_DEBUG_MESSAGES.get()){
            Minecraft.getInstance().player.displayClientMessage(Component.literal("Client SP e stats Update"), false);
        }
    }

    public static void updateClientStatSpSpent(int amount){
        if(amount > 0)clientSpSpent += amount;

        if(Config.SHOW_DEBUG_MESSAGES.get()){
            Minecraft.getInstance().player.displayClientMessage(Component.literal("Client SP s stats Update"), false);
        }
    }

    public static void updateClientCacheLevelData(LevelData data){
        clientLevelData = data;

        if(Config.SHOW_DEBUG_MESSAGES.get()){
            Minecraft.getInstance().player.displayClientMessage(Component.literal("Client LevelData Update: " + data.goal() + "xp, " + data.reward() + "sp"), false);
        }
    }


    public static void updateClientCacheCategories(Map<String, Category> map){
        clientCategories.clear();
        clientCategories.putAll(map);

        var sortedCategories = new ArrayList<>(map.values());
        sortedCategories.sort(Comparator.comparingInt(Category::order));

        var sortedIds = sortedCategories.stream()
                .map(Category::id)
                .toList();

        SortingTools.registerCategories(sortedIds);

        if(Config.SHOW_DEBUG_MESSAGES.get()){
            Minecraft.getInstance().player.displayClientMessage(Component.literal("Client categories Update: " + clientCategories.size() + " loaded."), false);
        }
    }


    public static void updateSkills(List<Skill> skills){
        clientSkills.clear();
        for(var skill : skills){clientSkills.put(skill.getID(), skill);}

        if(Config.SHOW_DEBUG_MESSAGES.get()){
            Minecraft.getInstance().player.displayClientMessage(Component.literal("Client skills sync: " + clientSkills.size() + " skills cached" ), false);
        }

    }

    public static void updateSkillLevels(String id, int lvl){
        id = id.toLowerCase();

        if(lvl <= 0) clientSkillLevels.remove(id);
        else clientSkillLevels.put(id.toLowerCase(), lvl);

        int owned = 0;
        for(var skill : clientSkills.values()){
            if(!clientSkillLevels.containsKey(skill.getID().toLowerCase())) continue;
            owned += clientSkillLevels.get(skill.getID().toLowerCase());
        }
        clientOwnedSkills = owned;

        if(Config.SHOW_DEBUG_MESSAGES.get()){
            Minecraft.getInstance().player.displayClientMessage(Component.literal("New skill level: " + id + " level " + lvl ), false);
        }
    }

    public static void syncConfig(Map<String, Object> syncedMap){
        clientConfigMap.clear();
        clientConfigMap.putAll(syncedMap);

        if(Config.SHOW_DEBUG_MESSAGES.get()){
            Minecraft.getInstance().player.displayClientMessage(Component.literal("Synced config: " + clientConfigMap.size() + " entries"), false);
            for (var entry : clientConfigMap.entrySet()) Minecraft.getInstance().player.displayClientMessage(Component.literal(entry.getKey() + "->" + entry.getValue()), false);
        }

    }

    public static void syncBookmark(String id, boolean state){
        if(!state) clientBookmarks.remove(id.toLowerCase());
        else clientBookmarks.add(id.toLowerCase());

        if(Config.SHOW_DEBUG_MESSAGES.get()){
            Minecraft.getInstance().player.displayClientMessage(Component.literal("Synced bookmark: " + id + ": " + state), false);
        }
    }


    //PREDICTIONS
    public static void predictBookmark(Skill skill){
        String id = skill.getID();
        if(isSkillBookmarked(id)) clientBookmarks.remove(id);
        else clientBookmarks.add(id);
    }

    public static void predictBuy(Skill skill) {
        String id = skill.getID().toLowerCase();
        int currentLvl = getSkillLevel(id);

        if(!skill.canBuy(getCurrentContext(id), getConfigBool(Config.PURCHASE_SYSTEM_KEY, true))) return;
        int price = skill.getPrices().get(currentLvl);

        clientSP -= price;
        updateSkillLevels(id, currentLvl + 1);
    }

    public static void predictBuyMax(Skill skill) {
        String id = skill.getID().toLowerCase();
        int currentLvl = getSkillLevel(id);

        var bulkResult = skill.checkBulkBuy(getCurrentContext(id), getConfigBool(Config.PURCHASE_SYSTEM_KEY, true));

        if (bulkResult.levelToAdd() > 0) {
            clientSP -= bulkResult.spToWithdraw();
            updateSkillLevels(id, currentLvl + bulkResult.levelToAdd());
        }
    }

    public static void predictRefund(Skill skill) {
        String id = skill.getID().toLowerCase();
        int currentLvl = getSkillLevel(id);

        if(!skill.canRefund(getCurrentContext(id), getConfigBool(Config.REFUND_SYSTEM_KEY, false))) return;

        updateSkillLevels(id, currentLvl - 1);
        double percentage = getConfigDouble(Config.REFUND_PERCENTAGE_KEY, 0);

        List<Integer> prices = skill.getPrices();
        if (currentLvl - 1 < prices.size()) {
            int initialPrice = prices.get(currentLvl - 1);
            int refundAmount = Math.max(1, (int)(initialPrice * (percentage / 100.0)));
            clientSP += refundAmount;
        }
    }

    public static void predictRefundMax(Skill skill) {
        String id = skill.getID().toLowerCase();

        var spToRefund = skill.checkBulkRefund(getCurrentContext(skill.getID()),
                (float) getConfigDouble(Config.REFUND_PERCENTAGE_KEY, 30D), getConfigBool(Config.REFUND_SYSTEM_KEY, false));

        if (spToRefund > 0) {
            clientSP += spToRefund;
        }
        updateSkillLevels(id, 0);
    }

    public static void clearCache(Enums.ResetType type){
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
    public static float getXP(){return clientXP;}
    public static int getSP(){return clientSP;}
    public static int getLvl(){return clientLevel;}
    public static float getXpGoal(){return clientLevelData.goal();}
    public static int getReward(){return clientLevelData.reward();}
    public static int getLimitPercentage(){return (int)(clientXpLimit * 100);}

    public static List<Skill> getAllSkills(){return new ArrayList<>(clientSkills.values());}
    public static List<String> getAllSkillIDs(){return new ArrayList<>(clientSkills.keySet());}
    public static List<String> getAllBookmarkedIDs(){return new ArrayList<>(clientBookmarks);}
    public static List<String> getPurchasedSkills(){return clientSkillLevels.keySet().stream().filter(clientSkills::containsKey).toList();}

    public static int getSkillLevel(String id){return clientSkillLevels.getOrDefault(id.toLowerCase(), 0);}
    public static Skill getSkill(String id){return clientSkills.getOrDefault(id.toLowerCase(), null);}
    public static boolean isSkillBookmarked(String id){return clientBookmarks.contains(id.toLowerCase());}

    @Nullable
    public static Category getCategory(@NotNull String id){return clientCategories.getOrDefault(id, null);}

    @NotNull
    public static SkillContext getCurrentContext(String skillID){
        var player = Minecraft.getInstance().player;
        Objects.requireNonNull(player, "Attempt to access SkillContext with null client.");

        return new SkillContext(player, getSkillLevel(skillID), clientLevel, clientSP, getPurchasedSkills());
    }

    public static float getAllTimeXp(){return clientAllTimeXP;}
    public static float getSessionXp(){return clientSessionXP;}
    public static int getSpEarned(){return clientSpEarned;}
    public static int getSpSpent(){return clientSpSpent;}
    public static int getUnlockedSkills(){return clientOwnedSkills;}
    public static int getSkillCount(){
        int count = 0;
        for(var skill : clientSkills.values()){
            count += skill.getMaximumLevel();
        }
        return count;
    }
    public static float getTotalXpPerHour(){
        var level = Minecraft.getInstance().level;
        if(level == null) return 0f;

        var ticks = level.getGameTime();

        var effectiveTicks = Math.max(ticks, 1200f);
        var exactHours = effectiveTicks / 72000f;

        return clientAllTimeXP / exactHours;
    }

    private static long sessionStartTick = -1L;
    public static float getSessionXpPerHour(){
        var level = Minecraft.getInstance().level;
        if(level == null) return 0f;

        if (sessionStartTick == -1) {
            sessionStartTick = level.getGameTime();
        }

        var sessionTicks = level.getGameTime() - sessionStartTick;

        var effectiveTicks = Math.max(sessionTicks, 1200f);
        var exactSessionHours = effectiveTicks / 72000f;

        return ClientCache.getSessionXp() / exactSessionHours;
    }

    //getters config
    public static boolean getConfigBool(String id, boolean fallback){
        var value = clientConfigMap.get(id);
        if(value instanceof Boolean bool) return bool;
        else return fallback;
    }

    public static double getConfigDouble(String id, double fallback){
        var value = clientConfigMap.get(id);
        if(value instanceof Double dbl) return dbl;
        else if(value instanceof Number nbr) return nbr.doubleValue();
        else return fallback;
    }

    public static int getConfigInt(String id, int fallback){
        var value = clientConfigMap.get(id);
        if(value instanceof Integer nbr) return nbr;
        else return fallback;
    }




    //UTIL
    public static int parseColor(String hexString, int fallback) {
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
        clientLevelData = new LevelData(100f, 1);
    }

    private static void resetSkills(){
        clientSkillLevels.clear();
    }

    private static void resetStats(){
        clientSessionXP = 0f;
        clientAllTimeXP = 0f;
        clientSpEarned = 0;
        clientSpSpent = 0;
        sessionStartTick = -1L;
    }

    private static void resetLimits(){
        clientXpLimit = 0f;
    }

    private static void shutDownReset(){
        clientConfigMap.clear();
        clientBookmarks.clear();
        clientSkills.clear();

        clientCategories.clear();
    }
}
