package com.tyzsskills.impl.server.skills;

import com.tyzsskills.api.records.BulkPurchaseResult;
import com.tyzsskills.api.model.Context;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public class SkillRules {
    //LOCK CONDITIONS
    public static boolean isAvailable(@NotNull Context.Skill sCtx, @NotNull Context.Player pCtx){
        return sCtx.skill().getRequiredLevel() <= sCtx.skillLevel()
                && getActivePrerequisites(sCtx, pCtx).isEmpty()
                && getActiveIncompatibilities(sCtx, pCtx).isEmpty();
    }
    public static @NotNull List<String> getActiveIncompatibilities(@NotNull Context.Skill sCtx, @NotNull Context.Player pCtx) {
        var rawIncompatibilities = sCtx.graph().getIncompatibilitiesFor(sCtx.skill().getID());
        if(rawIncompatibilities.isEmpty() || pCtx.ownedSkillLevels().isEmpty()) return Collections.emptyList();

        var intersections = new ArrayList<String>();
        for(var id : pCtx.ownedSkillLevels().keySet()){
            if(rawIncompatibilities.contains(id))
                intersections.add(id);
        }
        return intersections.isEmpty() ? List.of() : intersections;
    }
    public static @NotNull @Unmodifiable Map<String, Integer> getActivePrerequisites(@NotNull Context.Skill sCtx, @NotNull Context.Player pCtx) {
        var prerequisites = sCtx.graph().getPrerequisitesFor(sCtx.skill().getID());
        if(prerequisites.isEmpty()) return Map.of();

        var stillPrerequisites = new HashMap<String, Integer>();
        for(var prerequisite : prerequisites.entrySet()){
            var id = prerequisite.getKey();
            var requiredLevel = prerequisite.getValue();
            var currentLevel = pCtx.ownedSkillLevels().getOrDefault(id, 0);

            if(currentLevel < requiredLevel)
                stillPrerequisites.put(id, requiredLevel);
        }
        return stillPrerequisites.isEmpty() ? Map.of() : stillPrerequisites;
    }


    //PURCHASES & REFUNDS
    public static boolean canRefund(@NotNull Context.Skill sCtx, boolean refundEnabled){
        if(!refundEnabled || !sCtx.skill().isRefundable() || sCtx.skillLevel() <= 0) return false;
        return sCtx.skillLevel() <= sCtx.skill().getPrices().size();
    }
    public static boolean canBuy(@NotNull Context.Skill sCtx, @NotNull Context.Player pCtx, boolean purchaseEnabled){
        if(!sCtx.skill().isPurchasable() || !purchaseEnabled || sCtx.skillLevel() >= sCtx.skill().getMaximumLevel()) return false;
        if(!isAvailable(sCtx, pCtx)) return false;

        var price = sCtx.skill().getPrices().get(sCtx.skillLevel());
        return price <= pCtx.playerSp();
    }
    public static @NotNull BulkPurchaseResult checkBulkBuy(@NotNull Context.Skill sCtx, @NotNull Context.Player pCtx, boolean purchaseEnabled){
        var bulkResultFallback = new BulkPurchaseResult(0, 0);

        if(!purchaseEnabled || !sCtx.skill().isPurchasable() || sCtx.skillLevel() >= sCtx.skill().getMaximumLevel()) return bulkResultFallback;
        if(!isAvailable(sCtx, pCtx)) return bulkResultFallback;

        var spToSpend = 0;
        var levelsToAdd = 0;
        var availableSp = pCtx.playerSp();

        for (int i = sCtx.skillLevel(); i < sCtx.skill().getMaximumLevel(); i++) {
            if (i >= sCtx.skill().getPrices().size()) break;
            var price = sCtx.skill().getPrices().get(i);

            if (availableSp >= price) {
                availableSp -= price;
                spToSpend += price;
                levelsToAdd++;
            } else break;
        }

        return new BulkPurchaseResult(levelsToAdd, spToSpend);
    }
    public static int checkBulkRefund(@NotNull Context.Skill sCtx, float refundPercentage, boolean refundEnabled){
        if(!refundEnabled || !sCtx.skill().isRefundable() || sCtx.skillLevel() <= 0) return 0;

        var finalRefund = 0;
        var refundRate = refundPercentage / 100f;

        for (int i = sCtx.skillLevel() - 1; i >= 0; i--) {
            if (i < sCtx.skill().getPrices().size()) {
                int levelPrice = sCtx.skill().getPrices().get(i);

                if (levelPrice > 0) finalRefund += Math.round(levelPrice * refundRate);
            }
        }
        return finalRefund;
    }
}
