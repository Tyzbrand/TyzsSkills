package com.tyzsskills.api.ui;

import com.tyzsskills.api.records.UIStyle;
import com.tyzsskills.impl.client.SoundPlayer;
import net.minecraft.client.gui.GuiGraphics;

public class UIButton extends UIElement {

    public UIStyle style;
    private final Runnable onClick;

    public UIButton(int x, int y, UIStyle style, Runnable onClick) {
        super(x, y, style.width(), style.height());

        this.style = style;
        this.onClick = onClick;
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        int finalU;
        int finalV;

        if(isHovering(mouseX, mouseY)) {finalU =  style.uHover(); finalV = style.vHover();}
        else {finalU =  style.u(); finalV = style.v();}

        gui.blit(style.texture(), x, y, finalU, finalV, width, height, style.textureSize(), style.textureSize());
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY) {
        if(isHovering(mouseX, mouseY) && onClick != null){
            onClick.run();
            SoundPlayer.PlayUIClick();
            return true;
        }
        return false;
    }
}
