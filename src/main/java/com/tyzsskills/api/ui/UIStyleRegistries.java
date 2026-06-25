package com.tyzsskills.api.ui;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.api.records.UIStyle;
import net.minecraft.resources.ResourceLocation;

public class UIStyleRegistries {
    private static final ResourceLocation MAIN_TEXTURE = ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "textures/gui/background.png");
    private static final int MAIN_TEXTURE_SIZE = 325;

    public static final UIStyle PURCHASE_BTN = new UIStyle(9, 9, 169 , 173, 178, 173, MAIN_TEXTURE, MAIN_TEXTURE_SIZE);
    public static final UIStyle BULK_PURCHASE_BTN = new UIStyle(9, 9, 260, 173, 269, 173, MAIN_TEXTURE, MAIN_TEXTURE_SIZE);

    public static final UIStyle REFUND_BTN = new UIStyle(9, 9, 200 , 173, 209, 173, MAIN_TEXTURE, MAIN_TEXTURE_SIZE);
    public static final UIStyle BULK_REFUND_BTN = new UIStyle(9, 9, 283, 173, 292, 173, MAIN_TEXTURE, MAIN_TEXTURE_SIZE);

    public static final UIStyle BOOKMARK_BTN_OFF = new UIStyle(9, 9, 248 , 173, 230, 173, MAIN_TEXTURE, MAIN_TEXTURE_SIZE);
    public static final UIStyle BOOKMARK_BTN_ON = new UIStyle(9, 9, 239, 173, 221, 173, MAIN_TEXTURE, MAIN_TEXTURE_SIZE);
}
