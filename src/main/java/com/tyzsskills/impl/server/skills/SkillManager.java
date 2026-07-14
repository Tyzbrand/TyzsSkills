package com.tyzsskills.impl.server.skills;

import java.util.*;

import com.tyzsskills.Config;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.events.SkillActionEvent;
import com.tyzsskills.api.events.SkillLoadEvent;
import com.tyzsskills.api.model.Context;
import com.tyzsskills.impl.server.Level.LevelManager;
import com.tyzsskills.impl.server.sp.SpManager;
import com.tyzsskills.impl.server.attachments.PlayerData;
import com.tyzsskills.impl.server.effects.GenericEffects;
import com.tyzsskills.impl.server.payloads.*;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.*;

@ApiStatus.Internal
public class SkillManager {

    private static final Map<String, Skill> SKILL_COLLECTION = new HashMap<>();
    private static final Map<String, Skill> SKILL_COLLECTION_VIEW = Collections.unmodifiableMap(SKILL_COLLECTION);

    private static final Set<String> SUBSCRIBED_SKILL_BEHAVIORS = new HashSet<>();

    public static final SkillGraph GRAPH = new SkillGraph();

    private static final Context.Player PLAYER_CONTEXT = new Context.Player();
    private static final Context.Skill SKILL_CONTEXT = new Context.Skill();

    //CORE
    private static boolean setSkillLevelInternal(@NotNull ServerPlayer player, @NotNull Skill skill, int newLevel, boolean syncClient){
        if(newLevel < 0 || newLevel > skill.getMaximumLevel()) return false;
        int oldLvl = getPlayerSkillLevel(player, skill.getID());

        if(oldLvl == newLevel) return false;

        player.getData(PlayerData.DATA).setSkillLevel(skill.getID(), newLevel);

        NeoForge.EVENT_BUS.post(new SkillActionEvent.LevelChange(skill, player, oldLvl, newLevel));

        if(syncClient) PacketDistributor.sendToPlayer(player, new UpdatePayloads.SkillLevelPayload(skill.getID(), newLevel));

        if(skill.getType() == Enums.SkillType.GENERIC || skill.getType() == Enums.SkillType.CUSTOM){
            GenericEffects.applyEffects(skill, player);
        }

        return true;
    }

    //PUBLIC
    public static boolean tryAddSkillLevel(@NotNull ServerPlayer player, @NotNull String skillId, int amount){
        var skill = getSkill(skillId.toLowerCase()); if(skill == null) return false;
        return setSkillLevelInternal(player, skill, getPlayerSkillLevel(player, skillId) + amount, true);
    }
    public static boolean tryRemoveSkillLevel(@NotNull ServerPlayer player, @NotNull String skillId, int amount){
        var skill = getSkill(skillId.toLowerCase()); if(skill == null) return false;
        return setSkillLevelInternal(player, skill, getPlayerSkillLevel(player, skillId) - amount, true);
    }
    public static void setSkillLevel(@NotNull ServerPlayer player, @NotNull String skillId, int newLevel){
        var skill = getSkill(skillId.toLowerCase());
        if(skill != null) setSkillLevelInternal(player, skill, newLevel, true);
    }
    public static void resetSkillLevels(@NotNull ServerPlayer player){
        for(var skill : SKILL_COLLECTION.values()) setSkillLevelInternal(player, skill, 0, false);
    }
    public static boolean tryBuySkill(@NotNull ServerPlayer player, @NotNull String skillId)
    {
        skillId = skillId.toLowerCase();
        var skill = getSkill(skillId);
        if(skill == null) return false;

        var event = new SkillActionEvent.PurchasePre(skill, player);
        NeoForge.EVENT_BUS.post(event);
        if(event.isCanceled()) return false;

        int currentLvl = getPlayerSkillLevel(player, skillId);

        if(SkillRules.canBuy(getSkillContext(player, skillId), getPlayerContext(player), Config.PURCHASE_SYSTEM.get())){
            var price = skill.getPrices().get(currentLvl);

            if(setSkillLevelInternal(player, skill, currentLvl + 1, true)) {
                SpManager.tryRemoveSp(player, price);
                NeoForge.EVENT_BUS.post(new SkillActionEvent.PurchasePost(skill, player));
                return true;
            }
        }
        return false;
    }
    public static boolean tryBulkBuy(@NotNull ServerPlayer player, @NotNull String skillId){

        skillId = skillId.toLowerCase();
        var skill = getSkill(skillId);
        if (skill == null) return false;

        var event = new SkillActionEvent.PurchasePre(skill, player);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) return false;

        var currentLvl = player.getData(PlayerData.DATA).getSkillLevel(skillId);
        var maxLvl = skill.getMaximumLevel();

        if (currentLvl >= maxLvl) return false;

        var bulkResult = SkillRules.checkBulkBuy(getSkillContext(player, skillId), getPlayerContext(player), Config.PURCHASE_SYSTEM.get());

        if (bulkResult.levelToAdd() > 0) {
            if(setSkillLevelInternal(player, skill, currentLvl + bulkResult.levelToAdd(), true)){
                SpManager.tryRemoveSp(player, bulkResult.spToWithdraw());
                NeoForge.EVENT_BUS.post(new SkillActionEvent.PurchasePost(skill, player));

                return true;
            }
        }
        return false;
    }
    public static boolean tryRefundSkill(@NotNull ServerPlayer player, @NotNull String skillId)
    {
        skillId = skillId.toLowerCase();
        var skill = getSkill(skillId);

        if(skill == null) return false;
        if(!SkillRules.canRefund(getSkillContext(player, skillId), getPlayerContext(player), Config.REFUND_SYSTEM.getAsBoolean())) return false;

        var event = new SkillActionEvent.RefundPre(skill, player);
        NeoForge.EVENT_BUS.post(event);
        if(event.isCanceled()) return false;

        int currentLvl = player.getData(PlayerData.DATA).getSkillLevel(skillId);

        int initialPrice = skill.getPrices().get(currentLvl - 1);
        float refundRate = (float)(Config.REFUND_PERCENTAGE.get() / 100f);
        int finalPrice;

        if(initialPrice == 0) finalPrice = 0;
        else finalPrice = Math.round(initialPrice * refundRate);

        if(setSkillLevelInternal(player, skill, currentLvl - 1, true)) {
            if(finalPrice > 0) SpManager.tryAddSp(player, finalPrice);
            NeoForge.EVENT_BUS.post(new SkillActionEvent.RefundPost(skill, player));
            return true;
        }

        return false;
    }
    public static boolean tryBulkRefund(@NotNull ServerPlayer player, @NotNull String skillId){
        skillId = skillId.toLowerCase();
        var skill = getSkill(skillId);
        if (skill == null || !Config.REFUND_SYSTEM.get()) return false;

        var event = new SkillActionEvent.RefundPre(skill, player);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) return false;

        int currentLvl = player.getData(PlayerData.DATA).getSkillLevel(skillId);
        if (currentLvl <= 0 || currentLvl > skill.getMaximumLevel()) return false;

        var spToRefund = SkillRules.checkBulkRefund(getSkillContext(player, skillId), getPlayerContext(player),
                (float)Config.REFUND_PERCENTAGE.getAsDouble(), Config.REFUND_SYSTEM.getAsBoolean());

        if(setSkillLevelInternal(player, skill, 0, true)) {
            if (spToRefund > 0) SpManager.tryAddSp(player, spToRefund);
            NeoForge.EVENT_BUS.post(new SkillActionEvent.RefundPost(skill, player));
            return true;
        }

        return false;
    }
    public static void bookmarkSkill(@NotNull ServerPlayer player, @NotNull String skillId){
        skillId = skillId.toLowerCase();
        var skill = getSkill(skillId);
        if(skill == null) return;

        var data = player.getData(PlayerData.DATA);

        var isCurrentlyBookmarked = data.isBookmarked(skillId);
        var newValue = !isCurrentlyBookmarked;

        data.triggerBookmark(skillId);
        NeoForge.EVENT_BUS.post(new SkillActionEvent.Bookmark(skill, player));
        PacketDistributor.sendToPlayer(player, new UpdatePayloads.BookmarksPayload(skillId, newValue));
    }



    //API
    public static void registerSkill(@NotNull Skill skill) {
        var preEvent = new SkillLoadEvent.Pre(skill);
        NeoForge.EVENT_BUS.post(preEvent);
        if(preEvent.isCanceled()) return;

        var id = skill.getID();

        var behaviour = SkillBehaviorRegistry.getSkillBehavior(id);
        if(behaviour != null && !SUBSCRIBED_SKILL_BEHAVIORS.contains(id)){
            skill.setBehaviour(behaviour);
            behaviour.registerEvent(NeoForge.EVENT_BUS, id);
            SUBSCRIBED_SKILL_BEHAVIORS.add(id);
        }

        SKILL_COLLECTION.put(skill.getID().toLowerCase(), skill);

        NeoForge.EVENT_BUS.post(new SkillLoadEvent.Post(skill));
    }
    public static void clearSkills() {SKILL_COLLECTION.clear();}
    public static void buildGraph() {GRAPH.build(getAllSkills());}

    //getters
    public static @Nullable Skill getSkill(@NotNull String skillId){return SKILL_COLLECTION.getOrDefault(skillId.toLowerCase(), null);}
    public static boolean isSkillLoaded(String id){return SKILL_COLLECTION.containsKey(id);}
    public static boolean isSkillBookmarked(@NotNull ServerPlayer player,@NotNull String skillId) {return player.getData(PlayerData.DATA).isBookmarked(skillId.toLowerCase());}

    public static @NotNull Context.Player getPlayerContext(@NotNull ServerPlayer player){
        return PLAYER_CONTEXT.updateContext(player, LevelManager.getLevel(player), SpManager.getSP(player), getPlayerSkillLevels(player));
    }
    public static @NotNull Context.Skill getSkillContext(@NotNull ServerPlayer player, @NotNull String skillId){
        var skill = getSkill(skillId);
        Objects.requireNonNull(skill, "Attempt to access SkillContext with null skill (server side).");
        return SKILL_CONTEXT.updateContext(skill, getPlayerSkillLevel(player, skillId), GRAPH);
    }

    public static int getPlayerSkillLevel(@NotNull ServerPlayer player, @NotNull String skillId) {
        return player.getData(PlayerData.DATA).getSkillLevel(skillId.toLowerCase());}

    public static @NotNull @UnmodifiableView Map<String, Integer> getPlayerSkillLevels(@NotNull ServerPlayer player){
        return player.getData(PlayerData.DATA).getOwnedSkill();
    }

    public static @NotNull @Unmodifiable List<String> getAllBookmarkIDs(@NotNull ServerPlayer player){return player.getData(PlayerData.DATA).getBookmarks();}


    //CORE
    @ApiStatus.Internal public static @NotNull Collection<Skill> getAllSkills() {return SKILL_COLLECTION_VIEW.values();}

}