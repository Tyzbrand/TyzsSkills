package com.tyzsskills.impl.client.ui;

import com.tyzsskills.impl.client.ui.UIStyles.ButtonStyle;
import com.tyzsskills.impl.client.SoundPlayer;
import net.minecraft.client.gui.GuiGraphics;

import java.util.function.Supplier;

public class UIButton extends UIElement {

    private final Supplier<ButtonStyle> style;
    private final Supplier<Boolean> onClick;

    //Fluents

    public UIButton(int offsetX, int offsetY, Supplier<ButtonStyle> style, Supplier<Boolean> onClick) {
        super(offsetX, offsetY, style.get().width(), style.get().height());

        this.style = style;
        this.onClick = onClick;
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        int finalU;
        int finalV;
        var currentStyle = style.get();

        if(isHovering(mouseX, mouseY)) {finalU =  currentStyle.uHover(); finalV = currentStyle.vHover();}
        else {finalU =  currentStyle.u(); finalV = currentStyle.v();}

        gui.blit(currentStyle.texture(), x, y, finalU, finalV, width, height, currentStyle.textureSize(), currentStyle.textureSize());
    }

    @Override
    protected boolean onClick(int mouseX, int mouseY) {
        if(isHovering(mouseX, mouseY) && onClick != null){
            if(onClick.get()) SoundPlayer.PlayUIClick();
            return true;
        }
        return false;
    }
}
