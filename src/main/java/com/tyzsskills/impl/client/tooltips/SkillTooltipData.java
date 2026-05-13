package com.tyzsskills.impl.client.tooltips;

import com.tyzsskills.api.interfaces.ISkill;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.jetbrains.annotations.NotNull;

public record SkillTooltipData(@NotNull ISkill skill) implements TooltipComponent {
}
