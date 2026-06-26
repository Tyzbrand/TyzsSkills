package com.tyzsskills.api.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.MutableComponent;

import java.util.function.Supplier;

public class UIText extends UIElement{
    public final Font FONT = Minecraft.getInstance().font;

    public Supplier<MutableComponent> textExtractor;
    public MutableComponent text;
    public int wrapWidth = -1;

    private final boolean isStatic;

    public UIText(int offsetX, int offsetY, int width, int height, Supplier<MutableComponent> textExtractor) {
        super(offsetX, offsetY, width, height);
        this.textExtractor = textExtractor;
        isStatic = false;
    }

    public UIText(int x, int y, int width, int height, MutableComponent text) {
        super(x, y, width, height);
        this.text = text;
        isStatic = true;
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        var finalText = isStatic ? text : textExtractor.get();

        if(wrapWidth > 0) gui.drawWordWrap(FONT, finalText, x, y, wrapWidth, 0xFFFFFFFF);
        else gui.drawString(FONT, finalText, x, y, 0xFFFFFFFF, false);
    }
}
