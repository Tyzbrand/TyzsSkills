package com.tyzsskills.impl.client;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.integration.ui.records.UIStyles;
import com.tyzsskills.integration.ui.records.UITexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceLocation;

public class Styles {

    //COLORS
    public static final int COLOR_BG = 0xD5000000;
    public static final int COLOR_BORDER_MAXED = 0xFFA38441;
    public static final int COLOR_BORDER = 0xFFFFFFFF;

    public static final int TOOLTIP_BACKGROUND_START = 0xFF000000;
    public static final int TOOLTIP_BACKGROUND_END = 0xFF202020;
    public static final int TOOLTIP_BORDER_START = 0xFFFFFFFF;
    public static final int TOOLTIP_BORDER_END = 0xFFD0D0D0;

    //MISC
    public static final Font FONT = Minecraft.getInstance().font;
    public static final int TEXT_HEIGHT = 9;

    //Styles
    public static final UITexture MAIN_TEXTURE =
            new UITexture(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "textures/gui/main_texture.png"), 400);

    public static final UITexture MAIN_TEXTURE_BACKGROUNDS =
            new UITexture(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "textures/gui/main_texture_backgrounds.png"), 400);


    public static final UITexture DEFAULT_ICON =
            new UITexture(ResourceLocation.parse("minecraft:textures/item/barrier.png"), 16);
    public static final UITexture LOCKED_ICON =
            new UITexture(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "textures/gui/icons/lock_icon.png"), 16);


    public static final UIStyles.BackgroundStyle DEFAULT_BACKGROUND =
            new UIStyles.BackgroundStyle(COLOR_BG, COLOR_BORDER, 4, 4);

    public static final UIStyles.BackgroundStyle TOOLTIP =
            new UIStyles.BackgroundStyle(TOOLTIP_BACKGROUND_START, TOOLTIP_BACKGROUND_END, TOOLTIP_BORDER_START, TOOLTIP_BORDER_END, 4, 4);


}
