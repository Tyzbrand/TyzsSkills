package com.tyzsskills;

import com.tyzsskills.integration.ui.records.UITexture;
import net.minecraft.resources.ResourceLocation;

public class Constants {
    public static final int SKILL_MAX_LEVEL = 99;

    public static final long PAYLOAD_COOLDOWN_MS = 100L;

    public static final int MAX_CATEGORIES_PER_LINE = 5;

    public static final UITexture DEFAULT_ICON =
            new UITexture(ResourceLocation.parse("minecraft:textures/item/barrier.png"), 16);
}
