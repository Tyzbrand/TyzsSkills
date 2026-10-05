package com.tyzsskills.impl.client;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.integration.ui.records.UIStyles;
import com.tyzsskills.integration.ui.records.UITexture;
import net.minecraft.resources.ResourceLocation;

public class Styles {

    //COLORS
    public static final int COLOR_BG = 0xD5000000;
    public static final int COLOR_BORDER_MAXED = 0xFFD6AD55;
    public static final int COLOR_BORDER = 0xFFFFFFFF;

    //Styles
    public static final UITexture MAIN_TEXTURE =
            new UITexture(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "textures/gui/background.png"), 400);


    public static final UITexture DEFAULT_SKILL_ICON =
            new UITexture(ResourceLocation.parse("minecraft:textures/item/barrier.png"), 16);
    public static final UITexture LOCKED_SKILL_ICON =
            new UITexture(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "textures/gui/icons/lock_icon.png"), 16);


    public static final UIStyles.BackgroundStyle DEFAULT_BACKGROUND =
            new UIStyles.BackgroundStyle(COLOR_BG, COLOR_BORDER, 4, 4);


}
