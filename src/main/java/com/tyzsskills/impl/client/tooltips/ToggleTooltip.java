package com.tyzsskills.impl.client.tooltips;

import com.tyzsskills.api.Enums;
import com.tyzsskills.integration.ui.models.UIText;
import com.tyzsskills.integration.ui.models.UITooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.util.function.Supplier;


public class ToggleTooltip extends UITooltip {
    public ToggleTooltip(String translationKey, Supplier<Boolean> state) {
        //TODO: translation
        var trueLine = Component.empty()
                .append(Component.translatable(translationKey))
                .append(Component.literal(" [true]").withStyle(ChatFormatting.GREEN));

        //TODO: translation
        var falseLine = Component.empty()
                .append(Component.translatable(translationKey))
                .append(Component.literal(" [false]").withStyle(ChatFormatting.RED));

        addChild(new UIText(0, 0, () -> state.get() ? trueLine : falseLine)
                .withScale(.75f, Enums.ScalePivot.CENTER));
    }
}
