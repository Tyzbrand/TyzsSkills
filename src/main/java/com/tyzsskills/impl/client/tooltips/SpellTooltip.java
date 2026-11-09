package com.tyzsskills.impl.client.tooltips;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.interfaces.ISpell;
import com.tyzsskills.api.tools.ColorTools;
import com.tyzsskills.impl.client.Styles;
import com.tyzsskills.impl.client.cache.ClientCache;
import com.tyzsskills.integration.ui.models.UIContainer;
import com.tyzsskills.integration.ui.models.UIImage;
import com.tyzsskills.integration.ui.models.UIText;
import com.tyzsskills.integration.ui.models.UITooltip;
import com.tyzsskills.integration.ui.records.UIStyles;
import com.tyzsskills.integration.ui.records.UITexture;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;


public class SpellTooltip extends UITooltip {
    private final ClientCache cache;
    private final ISpell spell;

    private int latestCacheVersion = -1;

    private final UITexture spellIcon;
    private final UIContainer tagContainer;

    private MutableComponent title;
    private MutableComponent description;

    //TODO: translation
    private static final MutableComponent ASSIGNED = Component.literal("assigned");
    //TODO: translation
    private static final MutableComponent UNASSIGNED = Component.literal("unassigned");
    //TODO: translation
    private static final MutableComponent LOCKED = Component.literal("locked");

    private static final UIStyles.BackgroundStyle ASSIGNED_STYLE =
            new UIStyles.BackgroundStyle(0xFF367536, ColorTools.darken(0xFF367536), 4, 4);
    private static final UIStyles.BackgroundStyle UNASSIGNED_STYLE =
            new UIStyles.BackgroundStyle(0xFF753636, ColorTools.darken(0xFF753636), 4, 4);
    private static final UIStyles.BackgroundStyle LOCKED_STYLE =
            new UIStyles.BackgroundStyle(0xFF757575, ColorTools.darken(0xFF757575), 4, 4);

    private static final UIStyles.BackgroundStyle TOOLTIP_STYLE =
            new UIStyles.BackgroundStyle(Styles.TOOLTIP_BACKGROUND_START, Styles.TOOLTIP_BACKGROUND_END, 0xFFA27C4F, 0xFF724C20, 4, 4);

    private static final UIStyles.ImageStyle SLOT =
            new UIStyles.ImageStyle(32, 32, 96, 346, Styles.MAIN_TEXTURE);

    public SpellTooltip(ISpell spell){
        super(() -> TOOLTIP_STYLE);
        this.spell = spell;

        var icon = ResourceLocation.tryParse(spell.getIcon());
        if(icon == null) icon = Styles.DEFAULT_ICON.texture();
        spellIcon = new UITexture(icon, 16);

        tagContainer = new UIContainer(40 ,22);
        this.addChild(tagContainer);

        cache = ClientCache.get();

        updateData();

        this.addChild(new UIImage(2, 2, () -> SLOT));
        this.addChild(new UIImage(10, 10, 16, 16, () -> spellIcon)
                .withScale(1.27f, Enums.ScalePivot.CENTER));

        this.addChild(new UIText(38, 5, () -> title)
                .withScale(1.2f, Enums.ScalePivot.TOP_LEFT));
        this.addChild(new UIText(2, 40, () -> description).withWrapWidth(150));
    }

    @Override
    public void updatePosition(int parentX, int parentY) {
        updateData();
        super.updatePosition(parentX, parentY);
    }

    //UPDATES
    private void updateData(){
        var currentCacheVersion = cache.getVersion();
        var cacheChanged = currentCacheVersion != latestCacheVersion;
        if(!cacheChanged) return;

        latestCacheVersion = currentCacheVersion;

        title = Component.translatable(spell.getDisplayName()).withStyle(ChatFormatting.DARK_PURPLE);
        description = Component.translatable(spell.getDescription());

        updateTags();
    }

    //IMPL
    private void updateTags(){
        tagContainer.clear();
        var scale = .57f;

        var isPurchased = cache.SPELLS.isSpellPurchased(spell.getID());
        var isAssigned = cache.SPELLS.isSpellAssigned(spell.getID());

        var text = isPurchased ? isAssigned ? ASSIGNED : UNASSIGNED : LOCKED;
        var style = isPurchased ? isAssigned ? ASSIGNED_STYLE : UNASSIGNED_STYLE : LOCKED_STYLE;

        var stateText = new UIText(0, 0, () -> text)
                .withBackground(() -> style, () -> true)
                .withScale(scale, Enums.ScalePivot.TOP_LEFT);

        tagContainer.addChild(stateText);
    }

}
