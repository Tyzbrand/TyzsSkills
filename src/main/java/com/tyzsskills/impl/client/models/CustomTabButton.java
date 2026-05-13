package com.tyzsskills.impl.client.models;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.impl.client.SoundPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class CustomTabButton extends Button {
    private static final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "textures/gui/background.png");
    private static final ResourceLocation defaultIcon = ResourceLocation.parse("minecraft:textures/item/barrier.png");

    private final ResourceLocation icon;

    private final int u = 132, v = 233;
    private final int uHover = 190;
    private final int uActive = 161;
    private final float scale;


    private final Supplier<Boolean> isActiveCdt;

    /*
    * Constructor :
    * x,y => Screen coordinates (= where to draw)
    * width,height => Blit size
    * u,v => Image coordinates on ref texture
    * */

    public CustomTabButton(int x, int y, Supplier<Boolean> isActiveCdt, OnPress onPress, String icon, float scale){

        super(x, y, 29, 20, Component.empty(), onPress, DEFAULT_NARRATION);
        this.isActiveCdt = isActiveCdt;

        this.scale = scale;

        var iconSource = ResourceLocation.tryParse(icon);
        this.icon = iconSource == null ? defaultIcon : iconSource;
    }

    public CustomTabButton(int x, int y, Supplier<Boolean> isActiveCdt, OnPress onPress, String icon){
        this(x, y, isActiveCdt, onPress, icon, 1f);
    }

    @Override
    public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTick){
        int currentU = u;

        if(isActiveCdt.get()) currentU = uActive;
        else if (isHovered()) currentU = uHover;

        gui.blit(texture, this.getX(), this.getY(), currentU, v,
                this.width, this.height, 325, 325);

        this.renderIcon(gui, this.scale, 0, 0, 16, 16, this.getX() + 6, this.getY() + 3);
    }

    private void renderIcon(GuiGraphics gui, float scale, int u, int v, int w, int h, int btnX, int btnY){

        gui.pose().pushPose();

        float centerX = btnX + (w / 2f);
        float centerY = btnY + (h / 2f);
        gui.pose().translate(centerX, centerY, 0);

        gui.pose().scale(scale, scale, 1.0f);

        gui.setColor(1.0f, 1.0f, 1.0f, 1f);
        gui.blit(this.icon, -(w / 2), -(h / 2), u, v, w, h, 16, 16);

        gui.pose().popPose();
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(false);
    }

    @Override
    public void playDownSound(SoundManager handler) {
        LocalPlayer player = Minecraft.getInstance().player;
        if(player != null) SoundPlayer.PlayUIClick();
    }
}
