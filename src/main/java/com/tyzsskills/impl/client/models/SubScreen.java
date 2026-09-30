package com.tyzsskills.impl.client.models;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public abstract class SubScreen {

    public abstract void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick);
    public abstract void updatePositions(int x, int y);

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY){return false;}
    public boolean mouseClicked(double mouseX, double mouseY, int button){return false;}
    public void drawTooltips(GuiGraphics gui, Font font, int mouseX, int mouseY){}
}
