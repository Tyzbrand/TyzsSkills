package com.tyzsskills.impl.client.tooltips.spells;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.interfaces.ISpell;
import com.tyzsskills.api.tools.ColorTools;
import com.tyzsskills.api.tools.FormatTools;
import com.tyzsskills.impl.client.Styles;
import com.tyzsskills.impl.client.cache.ClientCache;
import com.tyzsskills.integration.ui.models.UIContainer;
import com.tyzsskills.integration.ui.models.UIImage;
import com.tyzsskills.integration.ui.models.UIText;
import com.tyzsskills.integration.ui.models.UITooltip;
import com.tyzsskills.integration.ui.records.UIStyles;
import com.tyzsskills.integration.ui.records.UITexture;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;


public class SpellTooltip extends UITooltip {
    private final ClientCache cache;
    private final ISpell spell;

    private int latestCacheVersion = -1;
    private boolean lastShiftState;

    private final UITexture spellIcon;
    private final UIContainer tagContainer;

    private MutableComponent title;
    private MutableComponent description;
    private MutableComponent properties;
    private MutableComponent fullBodyText;

    //TODO: translation
    private static final MutableComponent ASSIGNED = Component.literal("assigned");
    //TODO: translation
    private static final MutableComponent UNASSIGNED = Component.literal("unassigned");
    //TODO: translation
    private static final MutableComponent LOCKED = Component.literal("locked");

    //region STYLES
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
    //endregion

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
        this.addChild(new UIText(2, 40, () -> fullBodyText).withWrapWidth(150));
    }

    @Override
    public void updatePosition(int parentX, int parentY) {
        updateData();
        super.updatePosition(parentX, parentY);
    }

    //UPDATES
    private void updateData(){
        var currentCacheVersion = cache.getVersion();
        var currentShiftState = Screen.hasShiftDown();

        var cacheChanged = currentCacheVersion != latestCacheVersion;
        var shiftChanged = currentShiftState != lastShiftState;

        if(!cacheChanged && !shiftChanged) return;

        if(cacheChanged){
            latestCacheVersion = currentCacheVersion;

            title = Component.translatable(spell.getDisplayName()).withStyle(ChatFormatting.DARK_PURPLE);
            updateTags();

            description = Component.translatable(spell.getDescription());
        }

        lastShiftState = currentShiftState;

        updateProperties();
        fullBodyText = Component.empty().append(description).append("\n\n").append(properties);
    }

    //IMPL
    private void updateProperties(){
        //TODO: translation
        if(!lastShiftState){
            properties = Component.literal("Hold [shift] for properties").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC);
            return;
        }

        var tempComponent = Component.empty().append(Component.literal("◆Properties:").withStyle(ChatFormatting.BLUE));

        for(var property : spell.getProperties()){
            var propertyLabel = Component.empty()
                    .append(Component.literal("- "))
                    .append(Component.translatable(property.displayName()))
                    .append(Component.literal(": "))
                    .withStyle(ChatFormatting.GRAY);


            var value = property.getValue(cache.SPELLS.getPropertyLevel(spell.getID(), property.key()));
            var valueLabel = Component.empty()
                    .append(Component.literal(FormatTools.defaultFloat(value)).withStyle(ChatFormatting.GREEN))
                    .append(Component.literal(" "))
                    .append(Component.translatable(property.unit()).withStyle(ChatFormatting.GREEN));

            tempComponent.append("\n").append(propertyLabel).append(valueLabel);
        }
        properties = tempComponent;
    }

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
