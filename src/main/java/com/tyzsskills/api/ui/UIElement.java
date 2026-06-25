package com.tyzsskills.api.ui;

import net.minecraft.client.gui.GuiGraphics;

public abstract class UIElement {

    public int x, y;
    public final int width, height;

    public UIElement(int x, int y, int width, int height){
        this.x = x;
        this.y = y;

        this.width = width;
        this.height = height;
    }

    public abstract void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick);

    public boolean mouseClicked(int mouseX, int mouseY){return false;}

    //Utils
    public final boolean isHovering(int mouseX, int mouseY){
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

}
