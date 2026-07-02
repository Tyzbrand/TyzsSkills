package com.tyzsskills.api.ui;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.api.ui.UIStyles.*;
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
    public static final ButtonStyle PURCHASE_BTN = new ButtonStyle(9, 9, 169 , 173, 178, 173, MAIN_TEXTURE, MAIN_TEXTURE_SIZE);
    public static final ButtonStyle BULK_PURCHASE_BTN = new ButtonStyle(9, 9, 260, 173, 269, 173, MAIN_TEXTURE, MAIN_TEXTURE_SIZE);

    public static final ButtonStyle REFUND_BTN = new ButtonStyle(9, 9, 200 , 173, 209, 173, MAIN_TEXTURE, MAIN_TEXTURE_SIZE);
    public static final ButtonStyle BULK_REFUND_BTN = new ButtonStyle(9, 9, 283, 173, 292, 173, MAIN_TEXTURE, MAIN_TEXTURE_SIZE);

    public static final ButtonStyle BOOKMARK_BTN_OFF = new ButtonStyle(9, 9, 248 , 173, 230, 173, MAIN_TEXTURE, MAIN_TEXTURE_SIZE);
    public static final ButtonStyle BOOKMARK_BTN_ON = new ButtonStyle(9, 9, 239, 173, 221, 173, MAIN_TEXTURE, MAIN_TEXTURE_SIZE);


    //IMAGES
    public static final ImageStyle SKILL_CARD = new ImageStyle(64, 30, 166, 142, MAIN_TEXTURE, MAIN_TEXTURE_SIZE);
    public static final ImageStyle SKILL_CARD_COMPLETE = new ImageStyle(64, 30, 230, 142, MAIN_TEXTURE, MAIN_TEXTURE_SIZE);
}
