package com.tyzsskills.impl.client.tooltips;

import com.tyzsskills.integration.ui.models.UIText;
import com.tyzsskills.integration.ui.models.UITooltip;
import net.minecraft.network.chat.Component;

public class StatsTooltip extends UITooltip {
    public StatsTooltip() {

        //TODO: translation
        var header = Component.literal("Statistics");
        addChild(new UIText(0, 0, () -> header));
    }
}
