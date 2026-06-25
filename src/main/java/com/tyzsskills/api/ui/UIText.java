package com.tyzsskills.api.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.MutableComponent;

import java.util.function.Supplier;

public class UIText extends UIElement{
    private static final Font FONT = Minecraft.getInstance().font;

    public Supplier<MutableComponent> textExtractor;
    public MutableComponent text;

    private final boolean isStatic;

    public UIText(int x, int y, int width, int height, Supplier<MutableComponent> textExtractor) {
        super(x, y, width, height);
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
        if(isStatic) gui.drawString(FONT, text, 0, 0, 0xFFFFFFFF, false);
        else gui.drawString(FONT, textExtractor.get(), 0, 0, 0xFFFFFFFF, false);
    }
}
