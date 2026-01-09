package com.tyzsskills.client.screen;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.client.ClientCache;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;

public class MainGUI extends Screen {
    private static final ResourceLocation background = ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID,
            "textures/gui/rework-mix.png");

    private final int imageWidth = 270;
    private final int imageHeight = 139;

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

        guiGraphics.blit(background, leftPos, topPos, 0, 0, imageWidth, imageHeight, 300, 300);

        guiGraphics.drawCenteredString(this.font, this.title, this.width/2, this.topPos-10, 0xFFFFFF);

        this.renderPlayerStats(guiGraphics);
        this.renderXpBar(guiGraphics);

        super.render(guiGraphics, mouseX, mouseY, partialTick);

        this.renderTooltips(guiGraphics, mouseX, mouseY);
    }


    //helpers
    private void renderPlayerStats(GuiGraphics gui){

        int lineGap = 10;
        int xStart = leftPos + 15;
        int yStart = topPos + 16;

        MutableComponent lvlStat = Component.translatable("gui.tyzs_skills.Lvl").append(" " + ClientCache.GetLvl());
        gui.drawString(this.font, lvlStat, xStart, yStart, 0xFF000000, false);

        MutableComponent spStat = Component.translatable("gui.tyzs_skills.SP").append(" " + ClientCache.GetSP());
        yStart += lineGap;
        gui.drawString(this.font, spStat, xStart, yStart, 0xFF000000, false);
    }

    private void renderXpBar(GuiGraphics gui){

        float xpStat = ClientCache.GetXP();
        float xpGoal = ClientCache.GetXPGOAL();

        if(xpGoal <= 0) return;

        float ratio = Math.min(1f, xpStat/xpGoal);
        int widthToDraw = (int)(ratio*70);

        if(widthToDraw > 0) {
            gui.blit(background, leftPos + 3, topPos + 95, 82, 142, widthToDraw, 5, 300, 300);
        }
    }

    private void renderTooltips(GuiGraphics gui, int mouseX, int mouseY){

        int barTooltipX = leftPos+3; int barTooltipY = topPos+95; int barTooltipW = 70; int barTooltipH = 5;

        if(mouseX >= barTooltipX && mouseX <= barTooltipX + barTooltipW &&
            mouseY >= barTooltipY && mouseY <= barTooltipY + barTooltipH){

            String xpTooltip = SmartFormat(ClientCache.GetXP()) + "/" + SmartFormat(ClientCache.GetXPGOAL());
            gui.renderTooltip(this.font, Component.literal(xpTooltip), mouseX, mouseY);
        }

    }


    //Uilitaires
    private String SmartFormat(float value){
        if(value == (long)value){
            return String.format("%d", (long)value);
        }
        else return String.format(Locale.US, "%.1f", value);
    }
    //states
    @Override
    protected void renderBlurredBackground(float partialTick){}
    @Override
    public boolean isPauseScreen(){
        return false;
    }

}
