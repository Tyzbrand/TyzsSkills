package com.tyzsskills.impl.server.skills;

import com.tyzsskills.Config;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.events.SkillActionEvent;
import com.tyzsskills.api.events.SkillLoadEvent;
import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.Context;
import com.tyzsskills.impl.server.Level.LevelManager;
import com.tyzsskills.impl.server.active.BehaviorRegistry;
import com.tyzsskills.impl.server.attachments.PlayerData;
import com.tyzsskills.impl.server.effects.GenericEffects;
import com.tyzsskills.impl.server.payloads.UpdatePayloads;
import com.tyzsskills.impl.server.sp.SpManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

@ApiStatus.Internal
public class SkillManager {

    private static final Map<String, Skill> SKILL_COLLECTION = new HashMap<>();
    private static final Map<String, Skill> SKILL_COLLECTION_VIEW = Collections.unmodifiableMap(SKILL_COLLECTION);

    private static final Set<String> SUBSCRIBED_SKILL_BEHAVIORS = new HashSet<>();

    public static final SkillGraph GRAPH = new SkillGraph();

    private static final Context.Player PLAYER_CONTEXT = new Context.Player();
    private static final Context.Skill SKILL_CONTEXT = new Context.Skill();

    //CORE
    private static boolean setSkillLevelInternal(@NotNull ServerPlayer player, @NotNull ISkill skill, int newLevel, boolean syncClient){
        if(newLevel < 0 || newLevel > skill.getMaximumLevel()) return false;
        int oldLvl = getPlayerSkillLevel(player, skill);

        if(oldLvl == newLevel) return false;

        player.getData(PlayerData.DATA).setSkillLevel(skill.getId(), newLevel);

        NeoForge.EVENT_BUS.post(new SkillActionEvent.LevelChange(skill, player, oldLvl, newLevel));

        if(syncClient) PacketDistributor.sendToPlayer(player, new UpdatePayloads.SkillLevelPayload(skill.getId(), newLevel));

        if(skill.getType() == Enums.SkillType.GENERIC || skill.getType() == Enums.SkillType.CUSTOM){
            GenericEffects.applyEffects(skill, player);
        }

        return true;
    }

    //PUBLIC
    public static boolean tryAddSkillLevel(@NotNull ServerPlayer player, @NotNull ISkill skill, int amount){
        return setSkillLevelInternal(player, skill, getPlayerSkillLevel(player, skill) + amount, true);
    }
    public static boolean tryRemoveSkillLevel(@NotNull ServerPlayer player, @NotNull ISkill skill, int amount){
        return setSkillLevelInternal(player, skill, getPlayerSkillLevel(player, skill) - amount, true);
    }
    public static void setSkillLevel(@NotNull ServerPlayer player, @NotNull ISkill skill, int newLevel){
        setSkillLevelInternal(player, skill, newLevel, true);
    }
    public static void resetSkillLevels(@NotNull ServerPlayer player){
        for(var skill : SKILL_COLLECTION.values()) setSkillLevelInternal(player, skill, 0, false);
    }
    public static boolean tryBuySkill(@NotNull ServerPlayer player, @NotNull ISkill skill) {
        var event = new SkillActionEvent.PurchasePre(skill, player);
        NeoForge.EVENT_BUS.post(event);
        if(event.isCanceled()) return false;

        int currentLvl = getPlayerSkillLevel(player, skill);

        if(SkillRules.canBuy(getSkillContext(player, skill), getPlayerContext(player), Config.PURCHASE_SYSTEM.get())){
            var price = skill.getPrices().get(currentLvl);

            if(setSkillLevelInternal(player, skill, currentLvl + 1, true)) {
                SpManager.tryRemoveSp(player, price);
                NeoForge.EVENT_BUS.post(new SkillActionEvent.PurchasePost(skill, player));
                return true;
            }
        }
        return false;
    }
    public static boolean tryBulkBuy(@NotNull ServerPlayer player, @NotNull ISkill skill){
        var event = new SkillActionEvent.PurchasePre(skill, player);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) return false;

        var currentLvl = player.getData(PlayerData.DATA).getSkillLevel(skill.getId());
        var maxLvl = skill.getMaximumLevel();

        if (currentLvl >= maxLvl) return false;

        var bulkResult = SkillRules.checkBulkBuy(getSkillContext(player, skill), getPlayerContext(player), Config.PURCHASE_SYSTEM.get());

        if (bulkResult.levelToAdd() > 0) {
            if(setSkillLevelInternal(player, skill, currentLvl + bulkResult.levelToAdd(), true)){
                SpManager.tryRemoveSp(player, bulkResult.spToWithdraw());
                NeoForge.EVENT_BUS.post(new SkillActionEvent.PurchasePost(skill, player));

                return true;
            }
        }
        return false;
    }
    public static boolean tryRefundSkill(@NotNull ServerPlayer player, @NotNull ISkill skill) {
        if(!SkillRules.canRefund(getSkillContext(player, skill), getPlayerContext(player), Config.REFUND_SYSTEM.getAsBoolean())) return false;

        var event = new SkillActionEvent.RefundPre(skill, player);
        NeoForge.EVENT_BUS.post(event);
        if(event.isCanceled()) return false;

        int currentLvl = player.getData(PlayerData.DATA).getSkillLevel(skill.getId());

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
    public static boolean tryBulkRefund(@NotNull ServerPlayer player, @NotNull ISkill skill){
        var event = new SkillActionEvent.RefundPre(skill, player);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) return false;

        int currentLvl = player.getData(PlayerData.DATA).getSkillLevel(skill.getId());
        if (currentLvl <= 0 || currentLvl > skill.getMaximumLevel()) return false;

        var spToRefund = SkillRules.checkBulkRefund(getSkillContext(player, skill), getPlayerContext(player),
                (float)Config.REFUND_PERCENTAGE.getAsDouble(), Config.REFUND_SYSTEM.getAsBoolean());

        if(setSkillLevelInternal(player, skill, 0, true)) {
            if (spToRefund > 0) SpManager.tryAddSp(player, spToRefund);
            NeoForge.EVENT_BUS.post(new SkillActionEvent.RefundPost(skill, player));
            return true;
        }

        return false;
    }
    public static void bookmarkSkill(@NotNull ServerPlayer player, @NotNull ISkill skill){
        var data = player.getData(PlayerData.DATA);

        var isCurrentlyBookmarked = data.isBookmarked(skill.getId());
        var newValue = !isCurrentlyBookmarked;

        data.triggerBookmark(skill.getId());
        NeoForge.EVENT_BUS.post(new SkillActionEvent.Bookmark(skill, player));
        PacketDistributor.sendToPlayer(player, new UpdatePayloads.BookmarksPayload(skill.getId(), newValue));
    }
    public static boolean tryToggleSkillDeactivation(@NotNull ServerPlayer player, @NotNull ISkill skill){
        if(getPlayerSkillLevel(player, skill) <= 0) return false;

        if(skill.isPermanent() && !isSkillDeactivated(player, skill)) return false;

        var data = player.getData(PlayerData.DATA);
        var isCurrentlyDeactivated = data.isDeactivated(skill.getId());
        var newValue = !isCurrentlyDeactivated;

        data.triggerDeactivation(skill.getId());
        PacketDistributor.sendToPlayer(player, new UpdatePayloads.DeactivationsPayload(skill.getId(), newValue));

        if(skill.getType() == Enums.SkillType.GENERIC || skill.getType() == Enums.SkillType.CUSTOM){
            GenericEffects.applyEffects(skill, player);
        }
        return true;
    }

    //API
    public static void registerSkill(@NotNull Skill skill) {
        var preEvent = new SkillLoadEvent.Pre(skill);
        NeoForge.EVENT_BUS.post(preEvent);
        if(preEvent.isCanceled()) return;

        var id = skill.getId();

        var behaviour = BehaviorRegistry.getSkillBehavior(skill);
        if(behaviour != null && !SUBSCRIBED_SKILL_BEHAVIORS.contains(id)){
            behaviour.registerEvent(NeoForge.EVENT_BUS, skill);
            SUBSCRIBED_SKILL_BEHAVIORS.add(id);
        }

        SKILL_COLLECTION.put(skill.getId().toLowerCase(), skill);

        NeoForge.EVENT_BUS.post(new SkillLoadEvent.Post(skill));
    }
    public static void clearSkills() {SKILL_COLLECTION.clear();}
    public static void buildGraph() {GRAPH.build(getAllSkills());}

    //getters
    public static @Nullable ISkill getSkill(@NotNull String skillId){return SKILL_COLLECTION.get(skillId.toLowerCase());}
    public static boolean isSkillLoaded(@NotNull String skillId){return getSkill(skillId) != null;}
    public static boolean isSkillBookmarked(@NotNull ServerPlayer player, @NotNull ISkill skill) {return player.getData(PlayerData.DATA).isBookmarked(skill.getId());}
    public static boolean isSkillDeactivated(@NotNull ServerPlayer player, @NotNull ISkill skill){return player.getData(PlayerData.DATA).isDeactivated(skill.getId());}

    public static @NotNull Context.Player getPlayerContext(@NotNull ServerPlayer player){
        return PLAYER_CONTEXT.updateContext(player, LevelManager.getLevel(player), SpManager.getSP(player), getPlayerSkillLevels(player));
    }
    public static @NotNull Context.Skill getSkillContext(@NotNull ServerPlayer player, @NotNull ISkill skill){
        return SKILL_CONTEXT.updateContext(skill, getPlayerSkillLevel(player, skill), GRAPH);
    }

    public static int getPlayerSkillLevel(@NotNull ServerPlayer player, @NotNull ISkill skill) {
        return player.getData(PlayerData.DATA).getSkillLevel(skill.getId());}

    public static @NotNull @UnmodifiableView Map<String, Integer> getPlayerSkillLevels(@NotNull ServerPlayer player){
        return player.getData(PlayerData.DATA).getOwnedSkill();
    }

    public static @NotNull @UnmodifiableView List<String> getAllBookmarkIDs(@NotNull ServerPlayer player){return player.getData(PlayerData.DATA).getBookmarks();}
    public static @NotNull @UnmodifiableView List<String> getAllDeactivatedIds(@NotNull ServerPlayer player){return player.getData(PlayerData.DATA).getDeactivatedSkills();}


    //CORE
    @ApiStatus.Internal public static @NotNull Collection<Skill> getAllSkills() {return SKILL_COLLECTION_VIEW.values();}

}