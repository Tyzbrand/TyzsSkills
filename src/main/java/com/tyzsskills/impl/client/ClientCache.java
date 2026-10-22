package com.tyzsskills.impl.client;

import com.tyzsskills.Config;
import com.tyzsskills.Constants;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.interfaces.ISpell;
import com.tyzsskills.api.records.Category;
import com.tyzsskills.api.records.LevelData;
import com.tyzsskills.api.model.Context;
import com.tyzsskills.api.records.SpellProperty;
import com.tyzsskills.impl.client.active.SortingManager;
import com.tyzsskills.impl.client.overlay.XpTriggerOverlay;
import com.tyzsskills.impl.server.payloads.CActionSkillPayload;
import com.tyzsskills.impl.server.payloads.UpdatePayloads;
import com.tyzsskills.impl.server.skills.SkillGraph;
import com.tyzsskills.impl.server.skills.SkillRules;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

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
    public final Skills SKILLS = new Skills();
    public final Spells SPELLS = new Spells();
    public final Metadata METADATA = new Metadata();
    public final SkillGraph GRAPH = new SkillGraph();

    private int cacheVersion = 0;
    public int getVersion(){return cacheVersion;}

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
    private final Map<String, Object> configMap = new HashMap<>();


    public class Update {

        public void updateInit(@NotNull UpdatePayloads.InitPayload payload){
            var serverData = payload.serverData(); var configData = payload.configData();
            var playerSkillData = payload.playerSkillData(); var playerSpellData = payload.playerSpellData();
            var playerData = payload.playerData();

            SKILLS.update(playerSkillData, serverData);
            SPELLS.update(playerSpellData, serverData);
            METADATA.update(playerData, payload.levelData());

           configMap.clear();

            allTimeXp = playerData.totalXp();
            spEarned = playerData.spEarned();
            spSpent = playerData.spSpent();

            configMap.putAll(configData.booleanMap());
            configMap.putAll(configData.doubleMap());

            registerUpdate("Initial Synchronization");
        }

        public void updateLevel(@NotNull UpdatePayloads.LevelPayload payload) {
            METADATA.level = payload.level();

            registerUpdate("Level Synced: New Cached Level [" + METADATA.level + "]");
        }

        public void updateSp(@NotNull UpdatePayloads.SpPayload payload) {
            var old = METADATA.sp;
            METADATA.sp = payload.sp();

            var diff = METADATA.sp - old;
            if(diff < 0) spSpent += Math.abs(diff);
            else if(diff > 0) spEarned += diff;

            registerUpdate("Sp Synced: New Cached Sp Value [" + METADATA.sp + "]");
        }

        public void updateXp(@NotNull UpdatePayloads.XpPayload payload) {
            if (payload.gained() > 0) XpTriggerOverlay.AddXp(payload.gained(), payload.triggersOverlay());
            METADATA.xp = payload.xp();

            allTimeXp += payload.gained();
            sessionXp += payload.gained();

            registerUpdate("Xp Synced: New Cached Xp Value [" + METADATA.xp + "]");
        }

        public void updateSkillLevel(@NotNull UpdatePayloads.SkillLevelPayload payload) {
            var diff = payload.level() - SKILLS.getSkillLevel(payload.id());
            ownedSkills += diff;

            if (payload.level() <= 0) SKILLS.skillLevels.remove(payload.id().toLowerCase());
            else SKILLS.skillLevels.put(payload.id().toLowerCase(), payload.level());
            registerUpdate("Skill Level Synced: New Cached Skill Level [" + payload.id() + ", " + payload.level() + "]");
        }

        public void updateLevelData(@NotNull UpdatePayloads.LevelDataPayload payload) {
            METADATA.levelData = payload.data();
            registerUpdate("Level Data Synced: New Cached Level Data [" + payload.data().goal() + " xp, " + payload.data().reward() + "sp]");
        }

        public void updateBookmarks(@NotNull UpdatePayloads.BookmarksPayload payload) {
            var id = payload.id().toLowerCase();
            if (payload.state()) SKILLS.bookmarks.add(id);
            else SKILLS.bookmarks.remove(id);
            registerUpdate("Bookmark Synced: New Cached Bookmark [" + id + ", " + payload.state() + "]");
        }

        public void updateDeactivations(@NotNull UpdatePayloads.DeactivationsPayload payload) {
            if (payload.state()) SKILLS.deactivations.add(payload.id());
            else SKILLS.deactivations.remove(payload.id());
            registerUpdate("Deactivation Synced: New Cached Deactivation [" + payload.id() + ", " + payload.state() + "]");
        }

        public void updateSpellPurchase(@NotNull UpdatePayloads.SpellPurchasePayload payload) {
            if(!SPELLS.isSpellPurchased(payload.id()))SPELLS.purchasedSpells.add(payload.id());
            registerUpdate("Spell Purchase: New Purchased Spell [" + payload.id() + "]");
        }

        public void updatePropertyLevel(@NotNull UpdatePayloads.PropertyLevelPayload payload) {
            if (payload.level() <= 0) SPELLS.propertyLevels.remove(payload.id().toLowerCase());
            else SPELLS.propertyLevels.put(payload.id().toLowerCase(), payload.level());
            registerUpdate("Property Level Synced: New Cached Spell Property Level [" + payload.id() + ", " + payload.level() + "]");
        }

        public void updateSpellSlot(@NotNull UpdatePayloads.SpellSlotChange payload) {
            if (payload.id().isEmpty()) SPELLS.spellSlots.remove(payload.slot());
            else {
                SPELLS.spellSlots.values().remove(payload.id());
                SPELLS.spellSlots.put(payload.slot(), payload.id());
            }

            registerUpdate("Slot Synced: New Cached Spell in Slot [" + payload.slot() + "]");
        }

        private void registerUpdate(String message) {
            cacheVersion++;
            var player = Minecraft.getInstance().player;
            if (player == null || !Config.SHOW_DEBUG_MESSAGES.get()) return;
            player.displayClientMessage(Component.literal(message), false);
        }
    }

    public class Skills{
        public void update(UpdatePayloads.PlayerSyncSkillData playerSkillData, UpdatePayloads.ServerSyncData serverData){
            skills.clear(); bookmarks.clear();
            deactivations.clear(); skillLevels.clear();

            for(var skill : serverData.skills()) skills.put(skill.getID(), skill);
            GRAPH.build(getAllSkills().values());

            bookmarks.addAll(playerSkillData.bookmarks());
            deactivations.addAll(playerSkillData.deactivations());
            skillLevels.putAll(playerSkillData.playerSkillLevels());

            sortCategories(playerSkillData.categories());

            ownedSkills = skillLevels.values().stream().mapToInt(i -> i).sum();

            totalSkills = 0;
            for (var skill : skills.values()) totalSkills += skill.getMaximumLevel();
        }


        private final Map<String, Integer> skillLevels = new HashMap<>();
        private final Map<String, Integer> skillLevelsView = Collections.unmodifiableMap(skillLevels);
        public int getSkillLevel(@NotNull String skillId) {
            return skillLevels.getOrDefault(skillId, 0);
        }
        public @NotNull @UnmodifiableView Map<String, Integer> getOwnedSkills(){return skillLevelsView;}

        private final Set<String> bookmarks = new HashSet<>();
        private final Set<String> bookmarksView = Collections.unmodifiableSet(bookmarks);
        public boolean isSkillBookMarked(@NotNull String skillId) {
            return bookmarks.contains(skillId);
        }

        private final Set<String> deactivations = new HashSet<>();
        private final Set<String> deactivationsView = Collections.unmodifiableSet(deactivations);
        public boolean isSkillDeactivated(@NotNull String skillId) {
            return deactivations.contains(skillId);
        }

        private final List<Category> categories = new ArrayList<>();
        private final Map<String, Category> categoriesMap = new HashMap<>();
        private final List<Category> categoriesView = Collections.unmodifiableList(categories);
        public @NotNull @UnmodifiableView List<Category> getAllCategories(){return categoriesView;}
        public @Nullable Category getCategory(@NotNull String categoryId){return categoriesMap.getOrDefault(categoryId, null);}


        private final Map<String, ISkill> skills = new HashMap<>();
        private final Map<String, ISkill> skillsView = Collections.unmodifiableMap(skills);
        public @Nullable ISkill getSkill(@NotNull String skillId) {return skills.getOrDefault(skillId.toLowerCase(), null);}
        public @NotNull @UnmodifiableView Map<String, ISkill> getAllSkills() {return skillsView;}

        public boolean triggerAction(@NotNull ISkill skill, Enums.ClientAction actionType){
            if(isSpamming()) return false;
            var success = false;

            switch(actionType){
                case PURCHASE -> success = predictPurchase(skill);
                case REFUND -> success =  predictRefund(skill);
                case BULK_PURCHASE -> success = predictBulkPurchase(skill);
                case BULK_REFUND -> success = predictBulkRefund(skill);
                case BOOKMARK -> {
                    predictBookmark(skill);
                    success = true;
                }
                case DEACTIVATION -> success = predictDeactivation(skill);


            }

            if(success){
                registerNewClick();
                PacketDistributor.sendToServer(new CActionSkillPayload(skill.getID(), actionType));
            }
            return success;
        }

        //PREDICTIONS
        private void predictBookmark(ISkill skill) {
            String id = skill.getID().toLowerCase();
            if (isSkillBookMarked(id)) bookmarks.remove(id);
            else bookmarks.add(id);
            cacheVersion++;
        }
        private boolean predictDeactivation(ISkill skill) {
            String id = skill.getID();
            if(skill.isPermanent() || getSkillLevel(id) <= 0) return false;

            if (isSkillDeactivated(id)) deactivations.remove(id);
            else deactivations.add(id);

            cacheVersion++;
            return true;
        }
        private boolean predictPurchase(ISkill skill) {
            String id = skill.getID().toLowerCase();
            int currentLvl = getSkillLevel(id);

            if (!SkillRules.canBuy(getSkillContext(id), getPlayerContext(), getConfigBool(Config.PURCHASE_SYSTEM_KEY, true))) return false;
            int price = skill.getPrices().get(currentLvl);


            METADATA.sp -= price;
            skillLevels.put(id, currentLvl + 1);
            cacheVersion++;
            return true;
        }
        private boolean predictBulkPurchase(ISkill skill) {
            String id = skill.getID().toLowerCase();
            int currentLvl = getSkillLevel(id);

            var bulkResult = SkillRules.checkBulkBuy(getSkillContext(id), getPlayerContext(), getConfigBool(Config.PURCHASE_SYSTEM_KEY, true));

            if (bulkResult.levelToAdd() > 0) {
                METADATA.sp -= bulkResult.spToWithdraw();
                skillLevels.put(id, currentLvl + bulkResult.levelToAdd());
                cacheVersion++;
                return true;
            }
            return false;
        }
        private boolean predictRefund(ISkill skill) {
            String id = skill.getID().toLowerCase();
            int currentLvl = getSkillLevel(id);

            if (!SkillRules.canRefund(getSkillContext(id), getPlayerContext(), getConfigBool(Config.REFUND_SYSTEM_KEY, false))) return false;

            skillLevels.put(id, currentLvl - 1);
            float percentage = (float) getConfigDouble(Config.REFUND_PERCENTAGE_KEY, 0);

            List<Integer> prices = skill.getPrices();
            if (currentLvl - 1 < prices.size()) {
                int initialPrice = prices.get(currentLvl - 1);
                float refundPercentage = percentage / 100f;
                int refundAmount = (initialPrice <= 0) ? 0 : Math.round(initialPrice * refundPercentage);

                if (refundAmount > 0) METADATA.sp += refundAmount;
            }
            cacheVersion++;
            return true;
        }
        private boolean predictBulkRefund(ISkill skill) {
            String id = skill.getID().toLowerCase();

            if (getSkillLevel(id) <= 0) return false;

            var spToRefund = SkillRules.checkBulkRefund(getSkillContext(id), getPlayerContext(),
                    (float) getConfigDouble(Config.REFUND_PERCENTAGE_KEY, 30D), getConfigBool(Config.REFUND_SYSTEM_KEY, false));

            if (spToRefund > 0) {
                METADATA.sp += spToRefund;
            }

            skillLevels.put(id, 0);
            cacheVersion++;
            return true;
        }

        //CONTEXT
        private final Context.Skill skillContext = new Context.Skill();
        public @NotNull Context.Skill getSkillContext(String skillID){
            var skill = getSkill(skillID);
            Objects.requireNonNull(skill, "Attempt to access SkillContext with null skill (client side).");
            return skillContext.updateContext(skill, getSkillLevel(skillID), GRAPH);
        }

        //UTILS
        private void sortCategories(List<Category> categories){
            this.categories.clear();

            var sortedCategories = new ArrayList<>(categories);
            sortedCategories.sort(Comparator.comparingInt(Category::order));

            this.categories.addAll(sortedCategories);
            for(var cat : categories) categoriesMap.put(cat.id(), cat);
            if(!this.categories.isEmpty()) SortingManager.setCategory(this.categories.getFirst());
        }
    }
    public class Spells{
        public void update(UpdatePayloads.PlayerSyncSpellData playerSpellData, UpdatePayloads.ServerSyncData serverData){
            purchasedSpells.clear(); propertyLevels.clear(); spells.clear(); spellSlots.clear();

            for(var spell : serverData.spells()) spells.put(spell.getID(), spell);
            purchasedSpells.addAll(playerSpellData.purchasedSpells());
            propertyLevels.putAll(playerSpellData.playerPropertyLevels());
            spellSlots.putAll(playerSpellData.playerSlots());
        }

        private final List<String> purchasedSpells = new ArrayList<>();
        private final List<String> purchasedSpellsView = Collections.unmodifiableList(purchasedSpells);
        public boolean isSpellPurchased(@NotNull String spellId) {
            return purchasedSpells.contains(spellId);
        }

        private final Map<String, Integer> propertyLevels = new HashMap<>();
        private final Map<String, Integer> propertyLevelsView = Collections.unmodifiableMap(propertyLevels);
        public int getPropertyLevel(@NotNull String spellId, @NotNull String propertyKey) {
            return propertyLevels.getOrDefault((spellId + ":" + propertyKey).toLowerCase(), 0);
        }

        private final Map<Integer, String> spellSlots = new HashMap<>();
        private final Map<Integer, String> spellSlotsView = Collections.unmodifiableMap(spellSlots);
        public boolean isSpellAssigned(@NotNull String spellId){return  spellSlots.containsValue(spellId);}
        public boolean isSlotFree(int slot){
            return !spellSlots.containsKey(slot);
        }
        public @NotNull @UnmodifiableView Map<Integer, String> getSlots(){return spellSlotsView;}

        private final Map<String, ISpell> spells = new HashMap<>();
        private final Map<String, ISpell> spellsView = Collections.unmodifiableMap(spells);
        public @Nullable ISpell getSpell(@NotNull String spellId) {return spells.getOrDefault(spellId.toLowerCase(), null);}
        public @NotNull @UnmodifiableView Map<String, ISpell> getAllSpells() {return spellsView;}

        private final Context.Spell spellContext = new Context.Spell();
        public @NotNull Context.Spell getSpellContext(@NotNull String spellId) {
            var spell = getSpell(spellId);
            Objects.requireNonNull(spell, "Attempt to access SpellContext with null spell.");
            return spellContext.updateContext(spell, isSpellPurchased(spellId));
        }

        private final Context.Property propertyContext = new Context.Property();
        public @NotNull Context.Property getPropertyContext(@NotNull SpellProperty property, @NotNull String spellId) {
            return propertyContext.updateContext(property, getPropertyLevel(spellId, property.key()));
        }
    }
    public class Metadata{
        public void update(UpdatePayloads.PlayerSyncData playerData, LevelData levelData){
            level = playerData.level();
            sp = playerData.sp();
            xp = playerData.xp();
            this.levelData = levelData;
        }

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
    }


    //UTILS
    private final Context.Player playerContext = new Context.Player();
    public @NotNull Context.Player getPlayerContext() {
        var player = Minecraft.getInstance().player;
        Objects.requireNonNull(player, "Attempt to access PlayerContext with null client.");
        return playerContext.updateContext(player, METADATA.level, METADATA.sp, SKILLS.getOwnedSkills());
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