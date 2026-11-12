package com.tyzsskills.impl.client.tooltips.spells;

import com.tyzsskills.Config;
import com.tyzsskills.api.interfaces.ISpell;
import com.tyzsskills.api.records.SpellProperty;
import com.tyzsskills.api.tools.FormatTools;
import com.tyzsskills.impl.client.cache.ClientCache;
import com.tyzsskills.integration.ui.models.UIText;
import com.tyzsskills.integration.ui.models.UITooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class PropertyPurchaseTooltip extends UITooltip {
    private final ISpell spell;
    private final SpellProperty property;
    private final ClientCache cache;

    private boolean latestShiftState = false;
    private int latestCacheVersion = -1;

    private MutableComponent currentPriceLine;
    private MutableComponent currentDataLine;

    public PropertyPurchaseTooltip(ISpell spell, SpellProperty property) {
        this.spell = spell;
        this.property = property;
        this.cache = ClientCache.get();

        this.addChild(new UIText(0, 0, () -> currentPriceLine));
        this.addChild(new UIText(0, 11, () -> currentDataLine));

        this.withVisibility(() -> cache.getConfigBool(Config.PURCHASE_SYSTEM_KEY, true));

        updateData();
    }

    @Override
    public void updatePosition(int parentX, int parentY) {
        updateData();
        super.updatePosition(parentX, parentY);
    }

    //IMPL
    private void updateData() {
        var currentCacheVersion = cache.getVersion();
        var currentShiftState = Screen.hasShiftDown();

        if (latestCacheVersion == currentCacheVersion && latestShiftState == currentShiftState) return;
        latestCacheVersion = currentCacheVersion; latestShiftState = currentShiftState;

        var currentLvl = cache.SPELLS.getPropertyLevel(spell.getId(), property.key());
        var currentSp = cache.METADATA.getSp();

        refreshPriceLine(currentLvl, currentSp);
        refreshDataLines(currentLvl);
    }

    //UTILS
    //==================TEXTS==================
    private void refreshPriceLine(int currentLvl, int currentSp){
        if(currentLvl >= property.getMaximumLevel()){
            currentPriceLine = Component.translatable("gui.tyzs_skills.level_max").withStyle(ChatFormatting.GOLD);
            return;
        }

        var nextPrice = property.getPrice(currentLvl + 1);
        var priceColor = currentSp < nextPrice ? ChatFormatting.RED : ChatFormatting.BLUE;

        currentPriceLine = Component.empty()
                .append(Component.translatable("gui.tyzs_skills.cost"))
                .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(nextPrice + " ").withStyle(priceColor))
                .append(Component.translatable("gui.tyzs_skills.SP").withStyle(priceColor));
    }

    private void refreshDataLines(int currentLvl){
        if (currentLvl >= property.getMaximumLevel()) {
            currentDataLine = Component.empty();
            return;
        }

        var currentStat = currentLvl == 0 ? property.baseValue() : property.getValue(currentLvl);
        var targetedStat = property.getValue(currentLvl + 1);

        currentDataLine = Component.empty()
                .append(Component.literal("◆ "))
                .append(Component.literal(FormatTools.defaultFloat(currentStat) + " ").withStyle(ChatFormatting.DARK_GRAY))
                .append(Component.literal(" -> "))
                .append(Component.literal(FormatTools.defaultFloat(targetedStat) + " ").withStyle(ChatFormatting.GREEN))
                .append(Component.translatable(property.unit()));
    }
}
