package com.tyzsskills.impl.client.ui;

import com.tyzsskills.impl.client.SoundPlayer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class UITabButton extends UIElement{

    private Supplier<UIStyles.ButtonStyle> style;
    private final Supplier<Boolean> onClick;
    private final ResourceLocation icon;

    //Fluent
    public UITabButton withStyle(Supplier<UIStyles.ButtonStyle> style){this.style = style; return this;}

    public UITabButton(int offsetX, int offsetY, int width, int height, Supplier<Boolean> onClick, ResourceLocation icon) {
        super(offsetX, offsetY, width, height);

        this.onClick = onClick;
        this.icon = icon;
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        int finalU;
        int finalV;
        var currentStyle = style.get();

        if(isHovering(mouseX, mouseY)) {finalU = currentStyle.uHover(); finalV = currentStyle.vHover();}
        else {finalU = currentStyle.u(); finalV = currentStyle.v();}

        gui.blit(currentStyle.texture(), x, y, finalU, finalV, width, height, currentStyle.textureSize(), currentStyle.textureSize());
        gui.blit(icon, x + 4, y + 3, 0, 0, 16, 16, 16, 16);
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
