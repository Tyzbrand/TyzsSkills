package com.tyzsskills.impl.client.models;

import com.tyzsskills.Config;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.interfaces.ISpell;
import com.tyzsskills.api.records.SpellProperty;
import com.tyzsskills.impl.client.Styles;
import com.tyzsskills.impl.client.cache.ClientCache;
import com.tyzsskills.impl.client.tooltips.spells.PropertyPurchaseTooltip;
import com.tyzsskills.impl.client.tooltips.spells.PropertyTooltip;
import com.tyzsskills.impl.server.spells.SpellRules;
import com.tyzsskills.integration.ui.models.*;
import com.tyzsskills.integration.ui.records.UIStyles;
import net.minecraft.network.chat.Component;


public class PropertyCard extends UIContainer {
    private final ClientCache cache;
    private final SpellProperty property;
    private final ISpell spell;

    private boolean canBuyProperty;

    public static final int WIDTH = 106;
    public static final int HEIGHT = 30;

    private int latestCacheVersion = -1;

    //region STYLES
    private static final UIStyles.ImageStyle CARD =
            new UIStyles.ImageStyle(WIDTH, HEIGHT, 136, 299, Styles.MAIN_TEXTURE);
    private static final UIStyles.ImageStyle INFO =
            new UIStyles.ImageStyle(9, 9, 206, 227, Styles.MAIN_TEXTURE);
    private static final UIStyles.ImageStyle PROGRESS =
            new UIStyles.ImageStyle(92, 5, 136, 335, Styles.MAIN_TEXTURE);
    private static final UIStyles.ImageStyle PROGRESS_MAXED =
            new UIStyles.ImageStyle(92, 5, 136, 330, Styles.MAIN_TEXTURE);

    private static final UIStyles.BackgroundStyle PROP_LABEL =
            new UIStyles.BackgroundStyle(Styles.COLOR_BG, Styles.COLOR_BORDER, 3, 3);

    private static final UIStyles.ButtonStyle PURCHASE_BTN =
            new UIStyles.ButtonStyle(9, 9, 69, 227, 78, 227, 87, 227, Styles.MAIN_TEXTURE);
    //endregion

    public PropertyCard(int offsetX, int offsetY, SpellProperty property, ISpell spell){
        super(offsetX, offsetY);

        cache = ClientCache.get();
        this.property = property;
        this.spell = spell;

        addChild(new UIImage(0, 0, () -> CARD));

        var title = Component.translatable(property.displayName());
        addChild(new UIText(9, 9, () -> title)
                .withBackground(() -> PROP_LABEL, () -> false)
                .withScale(.75f, Enums.ScalePivot.TOP_LEFT));

        var purchaseTooltip = new PropertyPurchaseTooltip(spell, property);
        addChild(new UIButton(90, 7,() -> PURCHASE_BTN, this::handlePurchase)
                .withDisabled(() -> !canBuyProperty)
                .withTooltip(() -> purchaseTooltip));

        var infoTooltip = new PropertyTooltip(spell, property);
        addChild(new UIImage(79, 7, () -> INFO)
                .withTooltip(() -> infoTooltip));

        addChild(new UIProgressBar(7, 18, () -> getPropertyProgress() >= 1f ? PROGRESS_MAXED : PROGRESS, this::getPropertyProgress));

        updateData();
    }

    @Override
    public void updatePosition(int parentX, int parentY) {
        updateData();
        super.updatePosition(parentX, parentY);
    }

    private void updateData(){
        if(latestCacheVersion == cache.getVersion()) return;
        latestCacheVersion = cache.getVersion();

        var playerCtx = cache.getPlayerContext();
        var propertyCtx = cache.SPELLS.getPropertyContext(property, spell.getId());

        canBuyProperty = SpellRules.canBuyProperty(propertyCtx, playerCtx, cache.getConfigBool(Config.PURCHASE_SYSTEM_KEY, true));
    }

    //region EXTRACTED LOGIC
    private boolean handlePurchase(){
        return cache.SPELLS.triggerPropertyAction(spell, property.key(), Enums.SpellAction.PROPERTY_PURCHASE);
    }

    private float getPropertyProgress(){
        return (float)cache.SPELLS.getPropertyLevel(spell.getId(), property.key()) / property.getMaximumLevel();
    }
    //endregion
}
