package com.tyzsskills.api.ui;

import net.minecraft.client.gui.GuiGraphics;

public abstract class UIElement {

    public int offsetX, offsetY;
    public int x, y;
    public int width, height;

    public UIElement(int offsetX, int offsetY, int width, int height){
        this.offsetX = offsetX;
        this.offsetY = offsetY;

        this.width = width;
        this.height = height;
    }

    public abstract void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick);
    public boolean mouseClicked(int mouseX, int mouseY){return false;}

    //Utils
    public final boolean isHovering(int mouseX, int mouseY){
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
    public final void updatePosition(int parentX, int parentY){
        this.x = parentX + this.offsetX;
        this.y = parentY + this.offsetY;
    }

}
