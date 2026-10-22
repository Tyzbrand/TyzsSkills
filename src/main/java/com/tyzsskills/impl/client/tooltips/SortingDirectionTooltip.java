package com.tyzsskills.impl.client.tooltips;

import com.tyzsskills.api.Enums;
import com.tyzsskills.integration.ui.models.UIText;
import com.tyzsskills.integration.ui.models.UITooltip;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class SortingDirectionTooltip extends UITooltip {

    public SortingDirectionTooltip(Supplier<Enums.SortingDirection> direction) {
        //TODO: translation
        var ascending = Component.literal("Ascending");
        //TODO: translation
        var descending = Component.literal("Descending");

        addChild(new UIText(0, 0, () -> direction.get() == Enums.SortingDirection.ASCENDING ? ascending : descending)
                .withScale(.75f, Enums.ScalePivot.CENTER));
    }

}
