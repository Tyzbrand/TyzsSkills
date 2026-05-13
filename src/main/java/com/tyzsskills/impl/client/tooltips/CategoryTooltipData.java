package com.tyzsskills.impl.client.tooltips;

import com.tyzsskills.api.model.Category;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.jetbrains.annotations.NotNull;

public record CategoryTooltipData(@NotNull Category category) implements TooltipComponent {
}
