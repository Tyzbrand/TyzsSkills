package com.tyzsskills.api.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.MutableComponent;

import java.util.function.Supplier;

public class UIText extends UIElement{

    private final Font FONT = Minecraft.getInstance().font;
    private final Supplier<MutableComponent> textExtractor;

    private int wrapWidth = -1;

    //fluent
    public UIText withWrapWidth(int wrapWidth){this.wrapWidth = wrapWidth; return this;}


    public UIText(int offsetX, int offsetY, int width, int height, Supplier<MutableComponent> textExtractor) {
        super(offsetX, offsetY, width, height);
        this.textExtractor = textExtractor;
    }


    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        var finalText = textExtractor.get();

        if(wrapWidth > 0) gui.drawWordWrap(FONT, finalText, x, y, wrapWidth, 0xFFFFFFFF);
        else gui.drawString(FONT, finalText, x, y, 0xFFFFFFFF, false);
    }
}
