package com.tyzsskills.impl.client;

import com.tyzsskills.Config;
import com.tyzsskills.Constants;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.Category;
import com.tyzsskills.api.records.LevelData;
import com.tyzsskills.api.records.PlayerContext;
import com.tyzsskills.api.records.SkillContext;
import com.tyzsskills.impl.client.screen.MainGUI;
import com.tyzsskills.impl.client.screen.XpTriggerOverlay;
import com.tyzsskills.impl.client.tools.SortingTools;
import com.tyzsskills.impl.server.payloads.CActionSkillPayload;
import com.tyzsskills.impl.server.payloads.UpdatePayloads;
import com.tyzsskills.impl.server.skills.Skill;
import com.tyzsskills.impl.server.skills.SkillGraph;
import com.tyzsskills.impl.server.skills.SkillRules;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;


public class ClientCache {

    private ClientCache() {}

    private static ClientCache INSTANCE;
    public static @NotNull ClientCache get() throws IllegalStateException {
        if (INSTANCE == null)
            throw new IllegalStateException("Attempt to Access ClientCache.java While it's not yet Initialized.");
        return INSTANCE;
    }
    public static void deleteCache() {INSTANCE = null;}
    public static void init() {INSTANCE = new ClientCache();}
    public static boolean isReady() {return INSTANCE != null;}

    public final Update UPDATE = new Update();
    public final SkillGraph GRAPH = new SkillGraph();


    //METADATA
    private int level;
    public int getLevel() {return level;}

    private int sp;
    public int getSp() {return sp;}

    private float xp;
    public float getXp() {
        return xp;
    }

    private LevelData levelData;
    public float getXpGoal() {
        return levelData != null ? levelData.goal() : 999999;
    }
    public int getSpReward() {
        return levelData != null ? levelData.reward() : 0;
    }

    private final Map<String, Integer> skillLevels = new HashMap<>();
    public int getSkillLevel(@NotNull String skillId) {
        return skillLevels.getOrDefault(skillId.toLowerCase(), 0);
    }
    public @NotNull @Unmodifiable Map<String, Integer> getSkillLevels(){return Map.copyOf(skillLevels);}
    public @NotNull List<String> getPurchasedSkills() {
        var list = new ArrayList<String>();
        for (var kvp : skillLevels.entrySet()) if (kvp.getValue() > 0) list.add(kvp.getKey());
        return list;
    }

    private final HashSet<String> bookmarks = new HashSet<>();
    public boolean isSkillBookMarked(@NotNull String skillId) {
        return bookmarks.contains(skillId.toLowerCase());
    }
    public @NotNull @Unmodifiable List<String> getBookmarkedSkills() {
        return List.copyOf(bookmarks);
    }

    //STATISTICS
    private float allTimeXp;
    public float getAllTimeXP() {
        return allTimeXp;
    }

    private float sessionXp;
    public float getSessionXp() {
        return sessionXp;
    }

    private int spEarned;
    public int getSpEarned() {
        return spEarned;
    }

    private int spSpent;
    public int getSpSpent() {
        return spSpent;
    }

    private int ownedSkills;
    public int getOwnedSkillsAmount() {
        return ownedSkills;
    }

    private int totalSkills;
    public int getTotalSkills(){return totalSkills;}

    //SECURITY
    private long lastClickTime = 0L;
    public void registerNewClick(){lastClickTime = System.currentTimeMillis();}
    public boolean isSpamming(){return System.currentTimeMillis() - lastClickTime < Constants.PAYLOAD_COOLDOWN_MS;}

    //SERVER
    private final Map<String, Category> categories = new HashMap<>();
    public @Nullable Category getCategory(@NotNull String categoryId) {
        return categories.getOrDefault(categoryId, null);
    }

    private final Map<String, Skill> skills = new HashMap<>();
    public boolean isSkillLoaded(@NotNull String skillId) {
        return skills.containsKey(skillId.toLowerCase());
    }
    public @Nullable ISkill getSkill(@NotNull String skillId) {
        return skills.getOrDefault(skillId.toLowerCase(), null);
    }
    public @NotNull @Unmodifiable List<ISkill> getAllSkills() {
        return List.copyOf(skills.values());
    }
    public @NotNull @Unmodifiable List<String> getAllSkillIDs() {
        return List.copyOf(skills.keySet());
    }

    private final Map<String, Object> configMap = new HashMap<>();


    public class Update {

        public void updateInit(@NotNull UpdatePayloads.InitPayload payload){
            var playerData = payload.playerData(); var configData = payload.configData();
            var serverData = payload.serverData();

            categories.clear(); skills.clear(); configMap.clear(); bookmarks.clear();
            skillLevels.clear();

            for(var skill : serverData.skills()) skills.put(skill.getID(), skill);
            GRAPH.build(getAllSkills());

            bookmarks.addAll(serverData.bookmarks());
            skillLevels.putAll(serverData.playerSkillLevels());

            categories.putAll(serverData.categories());
            sortCategories();

            level = playerData.level();
            sp = playerData.sp();
            xp = playerData.xp();
            allTimeXp = playerData.totalXp();
            spEarned = playerData.spEarned();
            spSpent = playerData.spSpent();
            levelData = payload.levelData();

            configMap.putAll(configData.booleanMap());
            configMap.putAll(configData.doubleMap());

            ownedSkills = skillLevels.values().stream().mapToInt(i -> i).sum();

            totalSkills = 0;
            for (var skill : skills.values()) totalSkills += skill.getMaximumLevel();



            logUpdate("Initial Synchronization");
        }

        public void updateLevel(@NotNull UpdatePayloads.LevelPayload payload) {
            level = payload.level();
            logUpdate("Level Synced: New Cached Level [" + level + "]");
        }

        public void updateSp(@NotNull UpdatePayloads.SpPayload payload) {
            var old = sp;
            sp = payload.sp();

            var diff = sp - old;
            if(diff < 0) spSpent += Math.abs(diff);
            else if(diff > 0) spEarned += diff;

            logUpdate("Sp Synced: New Cached Sp Value [" + sp + "]");
        }

        public void updateXp(@NotNull UpdatePayloads.XpPayload payload) {
            if (payload.gained() > 0) XpTriggerOverlay.AddXp(payload.gained(), payload.triggersOverlay());
            xp = payload.xp();

            allTimeXp += payload.gained();
            sessionXp += payload.gained();

            logUpdate("Xp Synced: New Cached Xp Value [" + xp + "]");
        }

        public void updateSkillLevel(@NotNull UpdatePayloads.SkillLevelPayload payload) {
            var diff = payload.level() - getSkillLevel(payload.id());
            ownedSkills += diff;

            if (payload.level() <= 0) skillLevels.remove(payload.id().toLowerCase());
            else skillLevels.put(payload.id().toLowerCase(), payload.level());
            logUpdate("Skill Level Synced: New Cached Skill Level [" + payload.id() + ", " + payload.level() + "]");
        }

        public void updateLevelData(@NotNull UpdatePayloads.LevelDataPayload payload) {
            levelData = payload.data();
            logUpdate("Level Data Synced: New Cached Level Data [" + payload.data().goal() + " xp, " + payload.data().reward() + "sp]");
        }

        public void updateBookmarks(@NotNull UpdatePayloads.BookmarksPayload payload) {
            var id = payload.id().toLowerCase();
            if (payload.state()) bookmarks.add(id);
            else bookmarks.remove(id);
            logUpdate("Bookmark Synced: New Cached Bookmark [" + id + ", " + payload.state() + "]");
        }

        private void logUpdate(String message) {
            var player = Minecraft.getInstance().player;
            if (player == null || !Config.SHOW_DEBUG_MESSAGES.get()) return;
            player.displayClientMessage(Component.literal(message), false);
        }
    }

    //ACTIONS
    public boolean triggerAction(@NotNull ISkill skill, Enums.ClientAction actionType){
        if(isSpamming()) return false;
        var success = false;

        switch(actionType){
            case PURCHASE -> {success = predictPurchase(skill);}
            case REFUND -> {success =  predictRefund(skill);}
            case BULK_PURCHASE -> {success = predictBulkPurchase(skill);}
            case BULK_REFUND -> {success = predictBulkRefund(skill);}
            case BOOKMARK -> {
                predictBookmark(skill);
                success = true;
            }
        }

        if(success){
            registerNewClick();
            PacketDistributor.sendToServer(new CActionSkillPayload(skill.getID(), actionType));
        }
        return success;
    }

    //PREDICTIONS
    private void predictBookmark(ISkill skill) {
        String id = skill.getID();
        if (isSkillBookMarked(id)) bookmarks.remove(id);
        else bookmarks.add(id);

        if(Minecraft.getInstance().screen instanceof MainGUI gui && SortingTools.getMainCategory() == Enums.SortingCategory.BOOKMARKS)
            gui.refreshList();
    }

    private boolean predictPurchase(ISkill skill) {
        String id = skill.getID().toLowerCase();
        int currentLvl = getSkillLevel(id);

        if (!SkillRules.canBuy(getSkillContext(id), getPlayerContext(), getConfigBool(Config.PURCHASE_SYSTEM_KEY, true))) return false;
        int price = skill.getPrices().get(currentLvl);

        sp -= price;
        skillLevels.put(id, currentLvl + 1);
        return true;
    }

    private boolean predictBulkPurchase(ISkill skill) {
        String id = skill.getID().toLowerCase();
        int currentLvl = getSkillLevel(id);

        var bulkResult = SkillRules.checkBulkBuy(getSkillContext(id), getPlayerContext(), getConfigBool(Config.PURCHASE_SYSTEM_KEY, true));

        if (bulkResult.levelToAdd() > 0) {
            sp -= bulkResult.spToWithdraw();
            skillLevels.put(id, currentLvl + bulkResult.levelToAdd());
            return true;
        }
        return false;
    }

    private boolean predictRefund(ISkill skill) {
        String id = skill.getID().toLowerCase();
        int currentLvl = getSkillLevel(id);

        if (!SkillRules.canRefund(getSkillContext(id), getConfigBool(Config.REFUND_SYSTEM_KEY, false))) return false;

        skillLevels.put(id, currentLvl - 1);
        float percentage = (float) getConfigDouble(Config.REFUND_PERCENTAGE_KEY, 0);

        List<Integer> prices = skill.getPrices();
        if (currentLvl - 1 < prices.size()) {
            int initialPrice = prices.get(currentLvl - 1);
            float refundPercentage = percentage / 100f;
            int refundAmount = (initialPrice <= 0) ? 0 : Math.round(initialPrice * refundPercentage);

            if (refundAmount > 0) sp += refundAmount;
        }
        return true;
    }

    private boolean predictBulkRefund(ISkill skill) {
        String id = skill.getID().toLowerCase();

        if (getSkillLevel(id) <= 0) return false;

        var spToRefund = SkillRules.checkBulkRefund(getSkillContext(id),
                (float) getConfigDouble(Config.REFUND_PERCENTAGE_KEY, 30D), getConfigBool(Config.REFUND_SYSTEM_KEY, false));

        if (spToRefund > 0) {
            sp += spToRefund;
        }

        skillLevels.put(id, 0);
        return true;
    }


    //UTILS
    public @NotNull PlayerContext getPlayerContext() {
        var player = Minecraft.getInstance().player;
        Objects.requireNonNull(player, "Attempt to access PlayerContext with null client.");
        return new PlayerContext(player, level, sp, getSkillLevels());
    }

    public @NotNull SkillContext getSkillContext(String skillID){
        var skill = getSkill(skillID);
        Objects.requireNonNull(skill, "Attempt to access SkillContext with null skill (client side).");
        return new SkillContext(skill, getSkillLevel(skillID), GRAPH);
    }

    public int getTotalXpPerHour() {
        var level = Minecraft.getInstance().level;
        if (level == null) return 0;

        var ticks = level.getGameTime();
        var effectiveTicks = Math.max(ticks, 1200f);
        var exactHours = effectiveTicks / 72000f;

        return (int) (allTimeXp / exactHours);
    }

    private long sessionStartTick = -1L;
    public int getSessionXpPerHour() {
        var level = Minecraft.getInstance().level;
        if (level == null) return 0;

        if (sessionStartTick == -1) {
            sessionStartTick = level.getGameTime();
        }

        var sessionTicks = level.getGameTime() - sessionStartTick;

        var effectiveTicks = Math.max(sessionTicks, 1200f);
        var exactSessionHours = effectiveTicks / 72000f;

        return (int) (sessionXp / exactSessionHours);
    }

    public static int parseColor(String hexString, int fallback) {
        if (hexString == null || hexString.isEmpty()) return fallback;
        try {
            String clean = hexString.replace("#", "");
            if (clean.length() == 6) clean = "FF" + clean;
            return (int) Long.parseLong(clean, 16);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private void sortCategories(){
        var sortedCategories = new ArrayList<>(categories.values());
        sortedCategories.sort(Comparator.comparingInt(Category::order));

        var sortedIds = sortedCategories.stream()
                .map(Category::id)
                .toList();

        SortingTools.registerCategories(sortedIds);
    }

    //CONFIG
    public boolean getConfigBool(@NotNull String id, boolean fallback) {
        var value = configMap.get(id);
        if (value instanceof Boolean bool) return bool;
        else return fallback;
    }

    public double getConfigDouble(@NotNull String id, double fallback) {
        var value = configMap.get(id);
        if (value instanceof Double dbl) return dbl;
        else if (value instanceof Number nbr) return nbr.doubleValue();
        else return fallback;
    }

}