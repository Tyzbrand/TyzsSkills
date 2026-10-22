package com.tyzsskills.impl.client.tooltips;

import com.tyzsskills.api.Enums;
import com.tyzsskills.integration.ui.models.UIText;
import com.tyzsskills.integration.ui.models.UITooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.function.Supplier;

public class SortingTypeTooltip extends UITooltip {

    //TODO: translation
    private static final MutableComponent byName = Component.literal("By Name");
    //TODO: translation
    private static final MutableComponent byPrice = Component.literal("By Price");
    //TODO: translation
    private static final MutableComponent byLevel = Component.literal("By Level");
    //TODO: translation
    private static final MutableComponent byDefault = Component.literal("By Default");

    public SortingTypeTooltip(Supplier<Enums.SortingType> type) {
        addChild(new UIText(0, 0, () -> getSortingLine(type.get()))
                .withScale(.75f, Enums.ScalePivot.CENTER));
    }

    private MutableComponent getSortingLine(Enums.SortingType type){
        return switch(type){
            case DEFAULT -> byDefault;
            case NAME -> byName;
            case PRICE -> byPrice;
            case LEVEL -> byLevel;
        };
    }
}
