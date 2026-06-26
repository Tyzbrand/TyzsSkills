package com.tyzsskills.api.ui;

import com.tyzsskills.api.ui.UIStyles.ButtonStyle;
import com.tyzsskills.impl.client.SoundPlayer;
import net.minecraft.client.gui.GuiGraphics;

public class UIButton extends UIElement {

    public ButtonStyle style;
    private final Runnable onClick;

    public UIButton(int offsetX, int y, ButtonStyle style, Runnable onClick) {
        super(offsetX, y, style.width(), style.height());

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
