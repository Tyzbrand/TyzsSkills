package com.tyzsskills.api.ui;

import net.minecraft.client.gui.GuiGraphics;

public class UIBackground extends UIElement{
    public final int backgroundColor;
    public int borderColor;

    public boolean drawBorder;

    public UIBackground(int x, int y, int width, int height, int backgroundColor, int borderColor) {
        super(x, y, width, height);

        this.backgroundColor = backgroundColor;
        this.borderColor = borderColor;
        this.drawBorder = true;
    }

    public UIBackground(int x, int y, int width, int height, int backgroundColor) {
        super(x, y, width, height);
        this.backgroundColor = backgroundColor;
        this.drawBorder = false;
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        gui.fill(x, y + 1, x + width, y + height - 1, backgroundColor);
        gui.fill(x + 1, y, x + width - 1, y + 1, backgroundColor);
        gui.fill(x + 1, y + height - 1, x + width - 1, y + height, backgroundColor);


        if(!drawBorder) return;
        gui.fill(x + 1, y, x + width - 1, y + 1, borderColor);
        gui.fill(x + 1, y + height - 1, x + width - 1, y + height, borderColor);
        gui.fill(x, y + 1, x + 1, y + height - 1, borderColor);
        gui.fill(x + width - 1, y + 1, x + width, y + height - 1, borderColor);
    }
}
