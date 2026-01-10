package com.tyzsskills.client.models;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CustomButton extends Button {
    private final ResourceLocation texture;
    private final int u, v;
    private final int uHover, vHover;
    private final int textureW, textureH;

    /*
    * Constructor :
    * x,y => Screen coordinates (= where to draw)
    * width,height => Blit size
    * u,v => Image coordinates on ref texture
    * */

    public CustomButton (int x, int y, int width, int height,
                         int u, int v, int uHover, int vHover,
                         ResourceLocation texture, int textureW, int textureH,
                         OnPress onPress){
        super(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
        this.texture = texture;
        this.u = u;
        this.v = v;
        this.uHover = uHover;
        this.vHover = vHover;
        this.textureW = textureW;
        this.textureH = textureH;
    }

    @Override
    public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTick){
        int currentU = this.isHovered() ? uHover : u;
        int currentV = this.isHovered() ? vHover : v;

        gui.blit(texture, this.getX(), this.getY(), currentU, currentV,
                this.width, this.height, textureW, textureH);
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(false);
    }
}
