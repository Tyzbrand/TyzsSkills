package com.tyzsskills.impl.client.tooltips;

import com.tyzsskills.integration.ui.models.UIText;
import com.tyzsskills.integration.ui.models.UITooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class SearchBarTooltip extends UITooltip {
    public SearchBarTooltip(){
        var lineSpace = 11;
        var currentY = 0;

        //TODO: translation
        var line1 = Component.literal("Search by Name or ID").withStyle(ChatFormatting.BLUE);
        addChild(new UIText(0, currentY, () -> line1));

        //TODO: translation
        var line2 = Component.literal("Advanced Prefixes").withStyle(ChatFormatting.GRAY);
        addChild(new UIText(0, currentY += lineSpace * 2, () -> line2));

        //TODO: translation
        var line3 = Component.literal("@ Search in description").withStyle(ChatFormatting.DARK_GRAY);
        addChild(new UIText(0, currentY += lineSpace, () -> line3));

        //TODO: translation
        var line4 = Component.literal("# Search strictly by ID").withStyle(ChatFormatting.DARK_GRAY);
        addChild(new UIText(0, currentY + lineSpace, () -> line4));
    }
}
