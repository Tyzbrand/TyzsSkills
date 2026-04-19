package com.tyzsskills.impl.server.skills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tyzsskills.Config;
import com.tyzsskills.Constants;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.events.SkillActionEvent;
import com.tyzsskills.api.events.SkillLoadEvent;
import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.impl.server.sp.SpManager;
import com.tyzsskills.impl.server.attachments.PlayerData;
import com.tyzsskills.impl.server.attachments.StatsTracker;
import com.tyzsskills.impl.server.effects.GenericEffects;
import com.tyzsskills.impl.server.model.Skill;
import com.tyzsskills.impl.server.payloads.*;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class SkillManager {

    private static final SkillManager INSTANCE = new SkillManager();
    public static SkillManager get() {return INSTANCE;}

    private final Map<String, Skill> skillCollection = new HashMap<>();
    private final List<Skill> sortedBehaviorSkills = new ArrayList<>();


    public void registerSkill(Skill skill)
    {
        var preEvent = new SkillLoadEvent.Pre(skill);
        NeoForge.EVENT_BUS.post(preEvent);

        if(preEvent.isCanceled()) return;

        var behaviour = SkillBehaviorRegistry.getBehavior(skill.getID());
        if(behaviour != null){skill.setBehaviour(behaviour);}

        skillCollection.put(skill.getID(), skill);
        NeoForge.EVENT_BUS.post(new SkillLoadEvent.Post(skill));
    }

    public void buildSortedBehaviors() {
        sortedBehaviorSkills.clear();
        for (Skill skill : skillCollection.values()) {
            if (skill.hasBehaviour()) {
                sortedBehaviorSkills.add(skill);
            }
        }
        sortedBehaviorSkills.sort((s1, s2) -> Integer.compare(
                s2.getBehavior().getPriority(),
                s1.getBehavior().getPriority()
        ));
    }

    public void clearSkills(){
        skillCollection.clear();
        sortedBehaviorSkills.clear();
    }


    public boolean tryBuySkill(ServerPlayer player, String id)
    {
        var skill = getSkill(id);
        if(player == null || skill == null) return false;

        var event = new SkillActionEvent.PurchasePre(skill, player);
        NeoForge.EVENT_BUS.post(event);
        if(event.isCanceled()) return false;

        var data = player.getData(PlayerData.DATA);

        int currentLvl = data.getSkillLevel(id);
        if(currentLvl >= skill.getMaximumLevel()) return false;

        var prices = skill.getPrices();
        if(currentLvl >= prices.size()) return false;
        int price = prices.get(currentLvl);

        if(SpManager.getSP(player) >= price){
            SpManager.removeSP(player, price);
            PacketDistributor.sendToPlayer(player, new StatsSpSpentPayload(price));
            player.getData(StatsTracker.DATA).addSpSpent(price);

            setSkillLevel(player, id, currentLvl + 1);

            NeoForge.EVENT_BUS.post(new SkillActionEvent.PurchasePost(skill, player));
            return true;
        }
        return false;
    }

    public boolean tryBuyMaxSkill(ServerPlayer player, String id){
        var skill = getSkill(id);
        if (player == null || skill == null) return false;

        var event = new SkillActionEvent.PurchasePre(skill, player);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) return false;

        var data = player.getData(PlayerData.DATA);
        var currentLvl = data.getSkillLevel(id);
        var maxLvl = skill.getMaximumLevel();

        if (currentLvl >= maxLvl) return false;

        var prices = skill.getPrices();
        var availableSp = SpManager.getSP(player);
        var spToSpend = 0;
        var levelsToAdd = 0;

        for (int i = currentLvl; i < maxLvl; i++) {
            if (i >= prices.size()) break;
            var price = prices.get(i);

            if (availableSp >= price) {
                availableSp -= price;
                spToSpend += price;
                levelsToAdd++;
            } else break;

        }

        if (levelsToAdd > 0) {
            SpManager.removeSP(player, spToSpend);

            PacketDistributor.sendToPlayer(player, new StatsSpSpentPayload(spToSpend));
            player.getData(StatsTracker.DATA).addSpSpent(spToSpend);

            setSkillLevel(player, id, currentLvl + levelsToAdd);

            NeoForge.EVENT_BUS.post(new SkillActionEvent.PurchasePost(skill, player));
            return true;
        }
        return false;
    }

    public boolean tryRefundSkill(ServerPlayer player, String id)
    {
        var skill = getSkill(id);
        if(player == null || skill == null || !Config.REFUND_SYSTEM.get()) return false;

        var event = new SkillActionEvent.RefundPre(skill, player);
        NeoForge.EVENT_BUS.post(event);
        if(event.isCanceled()) return false;

        var data = player.getData(PlayerData.DATA);

        int currentLvl = data.getSkillLevel(id);
        if(currentLvl <= 0 || currentLvl > skill.getMaximumLevel()) return false;

        var prices = skill.getPrices();
        if (currentLvl > prices.size()) return false;

        int initialPrice = prices.get(currentLvl - 1);
        int finalPrice = Math.max(1, (int)(initialPrice * (Config.REFUND_PERCENTAGE.get() / 100f)));

        SpManager.addSP(player, finalPrice);
        PacketDistributor.sendToPlayer(player, new StatsSpEarnedPayload(finalPrice));
        player.getData(StatsTracker.DATA).addSpEarned(finalPrice);

        setSkillLevel(player, id, currentLvl - 1);

        NeoForge.EVENT_BUS.post(new SkillActionEvent.RefundPost(skill, player));

        return true;
    }

    public boolean tryRefundMaxSkill(ServerPlayer player, String id){
        var skill = getSkill(id);
        if (player == null || skill == null || !Config.REFUND_SYSTEM.get()) return false;

        var event = new SkillActionEvent.RefundPre(skill, player);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) return false;

        var data = player.getData(PlayerData.DATA);
        int currentLvl = data.getSkillLevel(id);
        if (currentLvl <= 0 || currentLvl > skill.getMaximumLevel()) return false;

        int targetLvl = 0;
        var prices = skill.getPrices();
        int finalRefund = 0;

        for (int i = currentLvl - 1; i >= targetLvl; i--) {
            if (i < prices.size()) {
                int levelPrice = prices.get(i);
                int levelRefund = (int) (levelPrice * (Config.REFUND_PERCENTAGE.get() / 100f));

                if (levelPrice > 0) {
                    levelRefund = Math.max(1, levelRefund);
                }

                finalRefund += levelRefund;
            }
        }

        if (finalRefund > 0) {
            SpManager.addSP(player, finalRefund);
            PacketDistributor.sendToPlayer(player, new StatsSpEarnedPayload(finalRefund));
            player.getData(StatsTracker.DATA).addSpEarned(finalRefund);
        }

        setSkillLevel(player, id, targetLvl);

        NeoForge.EVENT_BUS.post(new SkillActionEvent.RefundPost(skill, player));

        return true;
    }


    public void bookmarkSkill(ServerPlayer player, String id){
        if(player == null || getSkill(id.toLowerCase()) == null) return;

        var data = player.getData(PlayerData.DATA);

        var isCurrentlyBookmarked = data.isBookmarked(id);
        var newValue = !isCurrentlyBookmarked;

        data.triggerBookmark(id);
        NeoForge.EVENT_BUS.post(new SkillActionEvent.Bookmark(getSkill(id), player));
        PacketDistributor.sendToPlayer(player, new SkillBookmarksPayload(id.toLowerCase(), newValue));
    }

    public void setSkillLevel(ServerPlayer player, String id, int lvl){
        if(player == null || lvl < 0 || lvl > Constants.SKILL_MAX_LEVEL) return;

        var skill = getSkill(id.toLowerCase());
        if(skill == null) return;

        var data = player.getData(PlayerData.DATA);

        lvl = Math.max(0, Math.min(lvl, skill.getMaximumLevel()));
        int oldLvl = getPlayerSkillLevel(player, id);

        data.setSkillLevel(id, lvl);
        NeoForge.EVENT_BUS.post(new SkillActionEvent.LevelChange(skill, player, oldLvl, lvl));

        PacketDistributor.sendToPlayer(player, new SkillLevelSyncPayload(id, lvl));


        if(skill.getType() == Enums.SkillType.GENERIC || skill.getType() == Enums.SkillType.CUSTOM){
            if(lvl > 0) GenericEffects.applyEffects(skill, player);
            else GenericEffects.applyEffects(skill, player);
        }
    }

    public void addSKillLevel(ServerPlayer player, String id, int amount){
        int current = getPlayerSkillLevel(player, id);
        setSkillLevel(player, id, current + amount);
    }

    public void removeSkillLevel(ServerPlayer player, String id, int amount){
        int current = getPlayerSkillLevel(player, id);
        setSkillLevel(player, id, current - amount);
    }


    //getters
    public Skill getSkill(String id){return skillCollection.getOrDefault(id.toLowerCase(), null);}
    public List<Skill> getAllSkills() {return new ArrayList<>(skillCollection.values());}
    public int getPlayerSkillLevel(ServerPlayer player, String id) {return player.getData(PlayerData.DATA).getSkillLevel(id);}
    public boolean isSkillLoaded(String id){return skillCollection.containsKey(id);}
    public List<Skill> getSortedBehaviorSkills() {return sortedBehaviorSkills;}

    //API LINKS
    public ISkill getSkillInfos(String id){return skillCollection.getOrDefault(id.toLowerCase(), null);}
    public List<ISkill> getAllSkillsInfos(){return new ArrayList<>(skillCollection.values());}

    public boolean isSkillBookmarked(ServerPlayer player,String id) {return player.getData(PlayerData.DATA).isBookmarked(id);}
    public List<String> getAllBookmarkIDs(ServerPlayer player){return player.getData(PlayerData.DATA).getBookmarks();}
}