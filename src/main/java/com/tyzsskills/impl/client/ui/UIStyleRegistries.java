package com.tyzsskills.impl.client.ui;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.impl.client.ui.UIStyles.*;
import net.minecraft.resources.ResourceLocation;

public class UIStyleRegistries {

    //TEXTURES
    public static final ResourceLocation MAIN_TEXTURE = ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "textures/gui/background.png");
    public static final int MAIN_TEXTURE_SIZE = 325;

    public static final ResourceLocation DEFAULT_SKILL_ICON = ResourceLocation.parse("minecraft:textures/item/barrier.png");

    //COLORS
    public static final int COLOR_BG = 0xD5000000;
    public static final int COLOR_BORDER_MAXED = 0xFFD6AD55;
    public static final int COLOR_BORDER = 0xFFFFFFFF;

    //BUTTONS
    public static final ButtonStyle PURCHASE_BTN = new ButtonStyle(9, 9, 42 , 192, 51, 192, 60, 192, MAIN_TEXTURE, MAIN_TEXTURE_SIZE);
    public static final ButtonStyle BULK_PURCHASE_BTN = new ButtonStyle(9, 9, 100, 192, 109, 192, 60, 192, MAIN_TEXTURE, MAIN_TEXTURE_SIZE);

    public static final ButtonStyle REFUND_BTN = new ButtonStyle(9, 9, 71 , 192, 80, 192, 89, 192, MAIN_TEXTURE, MAIN_TEXTURE_SIZE);
    public static final ButtonStyle BULK_REFUND_BTN = new ButtonStyle(9, 9, 120, 192, 129, 192, 89, 192, MAIN_TEXTURE, MAIN_TEXTURE_SIZE);

    public static final ButtonStyle BOOKMARK_BTN_OFF = new ButtonStyle(9, 9, 62 , 203, 71, 203, MAIN_TEXTURE, MAIN_TEXTURE_SIZE);
    public static final ButtonStyle BOOKMARK_BTN_ON = new ButtonStyle(9, 9, 42, 203, 51, 203, MAIN_TEXTURE, MAIN_TEXTURE_SIZE);


    //IMAGES
    public static final ImageStyle SKILL_CARD = new ImageStyle(64, 30, 166, 142, MAIN_TEXTURE, MAIN_TEXTURE_SIZE);
    public static final ImageStyle SKILL_CARD_COMPLETE = new ImageStyle(64, 30, 230, 142, MAIN_TEXTURE, MAIN_TEXTURE_SIZE);
}
