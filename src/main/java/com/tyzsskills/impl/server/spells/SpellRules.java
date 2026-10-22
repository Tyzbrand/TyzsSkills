package com.tyzsskills.impl.server.spells;

import com.tyzsskills.api.model.Context;
import org.jetbrains.annotations.NotNull;

public class SpellRules {

    public static boolean canBuyProperty(@NotNull Context.Property prCtx, @NotNull Context.Player plCtx, boolean purchaseEnabled){
        if(!purchaseEnabled) return false;

        var property = prCtx.property();
        if(prCtx.propertyLevel() >= property.getMaximumLevel()) return false;

        var price = property.getPrice(prCtx.propertyLevel() + 1);
        return price <= plCtx.playerSp();
    }

    public static boolean canBuySpell(@NotNull Context.Spell sCtx, @NotNull Context.Player pCtx, boolean purchaseEnabled) {
        if(!purchaseEnabled || sCtx.isSpellPurchased()) return false;
        return sCtx.spell().getPrice() <= pCtx.playerSp();
    }
}
