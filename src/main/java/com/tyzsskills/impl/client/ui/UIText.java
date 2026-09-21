package com.tyzsskills.impl.client.ui;

import com.tyzsskills.api.Enums;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.MutableComponent;

import java.util.function.Supplier;

public class UIText extends UIElement{

    private final Font FONT = Minecraft.getInstance().font;
    private final Supplier<MutableComponent> textExtractor;

    private Enums.TextAlignment alignment = Enums.TextAlignment.LEFT;
    private int wrapWidth = -1;

    //fluent
    public UIText withWrapWidth(int wrapWidth){this.wrapWidth = wrapWidth; return this;}
    public UIText withAlignment(Enums.TextAlignment alignment){this.alignment = alignment; return this;}

    public UIText(int offsetX, int offsetY, int width, int height, Supplier<MutableComponent> textExtractor) {
        super(offsetX, offsetY, width, height);
        this.textExtractor = textExtractor;
    }


    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        var finalText = textExtractor.get();
        if (finalText == null) return;

        if(wrapWidth > 0) gui.drawWordWrap(FONT, finalText, x, y, wrapWidth, 0xFFFFFFFF);
        else{
            var drawX = x;
            var textWidth = FONT.width(finalText);
            var localWidth = scale != 0f ? (int)(width / scale) : width;

            switch (alignment) {
                case RIGHT -> drawX = x + localWidth - textWidth;
                case CENTER -> drawX = x + (localWidth - textWidth) / 2;
                case LEFT -> drawX = x;
            }

            gui.drawString(FONT, finalText, drawX, y, 0xFFFFFFFF, false);
        }

    }
}
