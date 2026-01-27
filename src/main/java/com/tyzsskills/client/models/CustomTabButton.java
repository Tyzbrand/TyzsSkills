package com.tyzsskills.client.models;

import com.tyzsskills.server.active.SoundRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class CustomTabButton extends Button {
    private final ResourceLocation texture;
    private final int u, v;
    private final int uHover, vHover;
    private final int uActive, vActive;
    private final int textureW, textureH;

    private final Supplier<Boolean> isActiveCdt;

    /*
    * Constructor :
    * x,y => Screen coordinates (= where to draw)
    * width,height => Blit size
    * u,v => Image coordinates on ref texture
    * */

    public CustomTabButton(int x, int y, int width, int height,
                           int u, int v, int uHover, int vHover,
                           int uActive, int vActive, int textureW, int textureH,
                           Supplier<Boolean> isActiveCdt, ResourceLocation texture, OnPress onPress){

        super(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
        this.texture = texture;
        this.u = u;
        this.v = v;
        this.uHover = uHover;
        this.vHover = vHover;
        this.uActive = uActive;
        this.vActive = vActive;
        this.textureW = textureW;
        this.textureH = textureH;
        this.isActiveCdt = isActiveCdt;
    }

    @Override
    public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTick){

        boolean isActive = isActiveCdt.get();
        boolean isHover = isHovered();

        int currentU = u;
        int currentV = v;

        if(isActive){
            currentU = uActive;
            currentV = vActive;
        }
        else if(isHover){
            currentU = uHover;
            currentV = vHover;
        }

        gui.blit(texture, this.getX(), this.getY(), currentU, currentV,
                this.width, this.height, textureW, textureH);
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(false);
    }

    @Override
    public void playDownSound(SoundManager handler) {
        LocalPlayer player = Minecraft.getInstance().player;
        if(player != null) SoundRegistry.PlayUIClick();
    }
}
