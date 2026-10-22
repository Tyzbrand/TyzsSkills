package com.tyzsskills.impl.client.tooltips;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.tools.FormatTools;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.client.Styles;
import com.tyzsskills.integration.ui.models.UIContainer;
import com.tyzsskills.integration.ui.models.UIText;
import com.tyzsskills.integration.ui.models.UITooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class XpTooltip extends UITooltip {

    private final ClientCache cache;

    private int latestCacheVersion = -1;
    private MutableComponent currentXpLine;

    public XpTooltip() {
        cache = ClientCache.get();

        addChild(new UIText(0, 0, () -> currentXpLine).withScale(.75f, Enums.ScalePivot.CENTER));
    }

    @Override
    public void updatePosition(int parentX, int parentY) {
        updateData();
        super.updatePosition(parentX, parentY);
    }

    private void updateData(){
        var currentCacheVersion = cache.getVersion();

        if(latestCacheVersion == currentCacheVersion) return;
        latestCacheVersion = currentCacheVersion;

        currentXpLine = getXpLine();
    }

    private MutableComponent getXpLine(){
        return Component.empty()
                .append(Component.literal(FormatTools.defaultFloat(cache.METADATA.getXp()) + "/"))
                .append(Component.literal(FormatTools.defaultFloat(cache.METADATA.getXpGoal()) + " "))
                .append(Component.literal("[+" + cache.METADATA.getSpReward() + " ").withStyle(ChatFormatting.GREEN))
                .append(Component.translatable("gui.tyzs_skills.SP").withStyle(ChatFormatting.GREEN))
                .append(Component.literal("]").withStyle(ChatFormatting.GREEN));
    }
}
