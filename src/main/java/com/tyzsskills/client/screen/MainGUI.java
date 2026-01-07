package com.tyzsskills.client.screen;

import com.tyzsskills.Tyzsskills;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MainGUI extends Screen {
    private static final ResourceLocation background = ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID,
            "textures/gui/rework-mix.png");

    private final int imageWidth = 256;
    private final int imageHeight = 256;

    private int leftPos;
    private int topPos;

    public MainGUI(){super(Component.translatable("gui.tyzs_skills.title"));}

    @Override
    protected void init(){
        super.init();
        this.leftPos = (this.width - this.imageWidth)/2;
        this.topPos = (this.height - this.imageHeight)/2;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick){
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);


        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 50);

        guiGraphics.blit(background, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        guiGraphics.drawCenteredString(this.font, this.title, this.width/2, this.topPos-10, 0xFFFFFF);

        guiGraphics.pose().popPose();

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen(){
        return false;
    }
}
