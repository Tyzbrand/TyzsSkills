package com.tyzsskills.impl.client.cache;

import com.tyzsskills.Config;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.Context;
import com.tyzsskills.api.records.Category;
import com.tyzsskills.impl.client.active.SortingManager;
import com.tyzsskills.impl.server.payloads.CActionSkillPayload;
import com.tyzsskills.impl.server.payloads.UpdatePayloads;
import com.tyzsskills.impl.server.skills.SkillGraph;
import com.tyzsskills.impl.server.skills.SkillRules;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

public class SkillCache {

    private final ClientCache CACHE;
    SkillCache(ClientCache cache){CACHE = cache;}

    public final Update UPDATE = new Update();
    public final SkillGraph GRAPH = new SkillGraph();

    public class Update{
        void init(UpdatePayloads.PlayerSyncSkillData playerSkillData, UpdatePayloads.ServerSyncData serverData){
            skills.clear(); bookmarks.clear();
            deactivations.clear(); skillLevels.clear();

            for(var skill : serverData.skills()) skills.put(skill.getID(), skill);
            GRAPH.build(getAllSkills().values());

            bookmarks.addAll(playerSkillData.bookmarks());
            deactivations.addAll(playerSkillData.deactivations());
            skillLevels.putAll(playerSkillData.playerSkillLevels());

            sortCategories(playerSkillData.categories());
        }

        public void updateSkillLevel(@NotNull UpdatePayloads.SkillLevelPayload payload) {
            if (payload.level() <= 0) skillLevels.remove(payload.id().toLowerCase());
            else skillLevels.put(payload.id().toLowerCase(), payload.level());
            CACHE.UPDATE.registerUpdate("Skill Level Synced: New Cached Skill Level [" + payload.id() + ", " + payload.level() + "]");
        }

        public void updateBookmarks(@NotNull UpdatePayloads.BookmarksPayload payload) {
            var id = payload.id().toLowerCase();
            if (payload.state()) bookmarks.add(id);
            else bookmarks.remove(id);
            CACHE.UPDATE.registerUpdate("Bookmark Synced: New Cached Bookmark [" + id + ", " + payload.state() + "]");
        }

        public void updateDeactivations(@NotNull UpdatePayloads.DeactivationsPayload payload) {
            if (payload.state()) deactivations.add(payload.id());
            else deactivations.remove(payload.id());
            CACHE.UPDATE.registerUpdate("Deactivation Synced: New Cached Deactivation [" + payload.id() + ", " + payload.state() + "]");
        }
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

    public boolean triggerAction(@NotNull ISkill skill, Enums.SkillAction actionType){
        if(CACHE.isSpamming()) return false;
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
            CACHE.registerNewClick();
            PacketDistributor.sendToServer(new CActionSkillPayload(skill.getID(), actionType));
        }
        return success;
    }

    //PREDICTIONS
    private void predictBookmark(ISkill skill) {
        String id = skill.getID().toLowerCase();
        if (isSkillBookMarked(id)) bookmarks.remove(id);
        else bookmarks.add(id);
        CACHE.incrVersion();
    }
    private boolean predictDeactivation(ISkill skill) {
        String id = skill.getID();
        if(skill.isPermanent() || getSkillLevel(id) <= 0) return false;

        if (isSkillDeactivated(id)) deactivations.remove(id);
        else deactivations.add(id);

        CACHE.incrVersion();
        return true;
    }
    private boolean predictPurchase(ISkill skill) {
        String id = skill.getID().toLowerCase();
        int currentLvl = getSkillLevel(id);

        if (!SkillRules.canBuy(getSkillContext(id), CACHE.getPlayerContext(), CACHE.getConfigBool(Config.PURCHASE_SYSTEM_KEY, true))) return false;
        int price = skill.getPrices().get(currentLvl);


        CACHE.METADATA.sp -= price;
        skillLevels.put(id, currentLvl + 1);
        CACHE.incrVersion();
        return true;
    }
    private boolean predictBulkPurchase(ISkill skill) {
        String id = skill.getID().toLowerCase();
        int currentLvl = getSkillLevel(id);

        var bulkResult = SkillRules.checkBulkBuy(getSkillContext(id), CACHE.getPlayerContext(), CACHE.getConfigBool(Config.PURCHASE_SYSTEM_KEY, true));

        if (bulkResult.levelToAdd() > 0) {
            CACHE.METADATA.sp -= bulkResult.spToWithdraw();
            skillLevels.put(id, currentLvl + bulkResult.levelToAdd());
            CACHE.incrVersion();
            return true;
        }
        return false;
    }
    private boolean predictRefund(ISkill skill) {
        String id = skill.getID().toLowerCase();
        int currentLvl = getSkillLevel(id);

        if (!SkillRules.canRefund(getSkillContext(id), CACHE.getPlayerContext(), CACHE.getConfigBool(Config.REFUND_SYSTEM_KEY, false))) return false;

        skillLevels.put(id, currentLvl - 1);
        float percentage = (float) CACHE.getConfigDouble(Config.REFUND_PERCENTAGE_KEY, 0);

        List<Integer> prices = skill.getPrices();
        if (currentLvl - 1 < prices.size()) {
            int initialPrice = prices.get(currentLvl - 1);
            float refundPercentage = percentage / 100f;
            int refundAmount = (initialPrice <= 0) ? 0 : Math.round(initialPrice * refundPercentage);

            if (refundAmount > 0) CACHE.METADATA.sp += refundAmount;
        }
        CACHE.incrVersion();
        return true;
    }
    private boolean predictBulkRefund(ISkill skill) {
        String id = skill.getID().toLowerCase();

        if (getSkillLevel(id) <= 0) return false;

        var spToRefund = SkillRules.checkBulkRefund(getSkillContext(id), CACHE.getPlayerContext(),
                (float) CACHE.getConfigDouble(Config.REFUND_PERCENTAGE_KEY, 30D), CACHE.getConfigBool(Config.REFUND_SYSTEM_KEY, false));

        if (spToRefund > 0) {
            CACHE.METADATA.sp += spToRefund;
        }

        skillLevels.put(id, 0);
        CACHE.incrVersion();
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
