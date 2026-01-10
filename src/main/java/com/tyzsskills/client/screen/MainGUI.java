package com.tyzsskills.client.screen;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.client.ClientCache;
import com.tyzsskills.client.models.CustomTabButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Locale;

public class MainGUI extends Screen {
    private static final ResourceLocation background = ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID,
            "textures/gui/background.png");

    private static final ResourceLocation mainFont = ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID,
            "main_font");

    private final int imageWidth = 270;
    private final int imageHeight = 139;

    private int leftPos;
    private int topPos;

    public MainGUI(){super(Component.translatable("gui.tyzs_skills.title"));}

    public enum ContainerType {SKILLS, QUESTS}

    @Override
    protected void init(){
        super.init();
        this.leftPos = (this.width - this.imageWidth)/2;
        this.topPos = (this.height - this.imageHeight)/2;

        this.addButtons();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick){
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.blit(background, leftPos, topPos, 0, 0, imageWidth, imageHeight, 300, 300);

        this.renderPlayerStats(guiGraphics, mouseX, mouseY);

        this.renderXpBar(guiGraphics);

        this.renderEntity(guiGraphics, 30, mouseX, mouseY );

        super.render(guiGraphics, mouseX, mouseY, partialTick);

        this.renderTooltips(guiGraphics, mouseX, mouseY);
    }


    //helpers
    private void renderPlayerStats(GuiGraphics gui, int mouseX, int mouseY){

        int color1 = isHovering(mouseX, mouseY, leftPos, topPos+81, 36, 12 )? 0xD6AD55 : 0x737373;
        MutableComponent lvlStat = Component.translatable("gui.tyzs_skills.Lvl").withStyle(Style.EMPTY.withFont(mainFont));
        gui.drawString(this.font, lvlStat, leftPos+7, topPos+84, color1, false);

        MutableComponent lvlValue = Component.literal(String.valueOf(ClientCache.GetLvl())).withStyle(Style.EMPTY.withFont(mainFont));
        int text1W = this.font.width(lvlValue);
        int rightLimit1 = leftPos+31;
        gui.drawString(this.font, lvlValue, rightLimit1 - text1W, topPos+84, color1, false);


        int color2 = isHovering(mouseX, mouseY, leftPos+37, topPos+81, 36, 12 )? 0xD6AD55 : 0x737373;
        MutableComponent spStat = Component.translatable("gui.tyzs_skills.SP").withStyle(Style.EMPTY.withFont(mainFont));
        gui.drawString(this.font, spStat, leftPos+46, topPos+84, color2, false);

        MutableComponent spValue = Component.literal(String.valueOf(ClientCache.GetSP())).withStyle(Style.EMPTY.withFont(mainFont));
        int text2W = this.font.width(spValue);
        int rightLimit2 = leftPos+69;
        gui.drawString(this.font, spValue, rightLimit2 -text2W, topPos+84, color2, false);
    }

    private void renderXpBar(GuiGraphics gui){

        float xpStat = ClientCache.GetXP();
        float xpGoal = ClientCache.GetXPGOAL();

        if(xpGoal <= 0) return;

        float ratio = Math.min(1f, xpStat/xpGoal);
        int widthToDraw = (int)(ratio*70);

        if(widthToDraw > 0) {
            gui.blit(background, leftPos + 2, topPos + 98, 82, 142, widthToDraw, 5, 300, 300);
        }
    }

    private void addButtons(){
        CustomTabButton skillBtn = new CustomTabButton(
                leftPos + 55, topPos + 6,
                16, 16,
                82, 150,
                114, 150,
                98, 150,
                 300, 300,
                () -> ClientCache.GetContainerType() == ContainerType.SKILLS,
                background,
                (b) -> ClientCache.SetContainerType(ContainerType.SKILLS));

        this.addRenderableWidget(skillBtn);

        CustomTabButton questBtn = new CustomTabButton(
                leftPos + 55, topPos + 24,
                16, 16,
                82, 167,
                114, 167,
                98, 167,
                300, 300,
                () -> ClientCache.GetContainerType() == ContainerType.QUESTS,
                background,
                (b) -> ClientCache.SetContainerType(ContainerType.QUESTS));

        this.addRenderableWidget(questBtn);
    }

    private void renderEntity(GuiGraphics gui, int scale,  int mouseX, int mouseY){
        LivingEntity player = this.minecraft.player;
        if(player == null) return;

        int renderX = leftPos + 29;
        int renderY = topPos + 72;


        float mouseXOffset = (float)(renderX) - mouseX;
        float mouseYOffset = (float)(renderY - 50) - mouseY;

        float f = (float)Math.atan((double)mouseXOffset / 40f);
        float f1 = (float)Math.atan((double)mouseYOffset / 40f);

        Quaternionf quatF = (new Quaternionf()).rotateZ((float)Math.PI);
        Quaternionf quatF1 = (new Quaternionf()).rotateX(f1 * 20f * (float)Math.PI/180f);
        quatF.mul(quatF1);

        float f2 = player.yBodyRot;
        float f3 = player.getYRot();
        float f4 = player.getXRot();
        float f5 = player.yHeadRotO;
        float f6 = player.yHeadRot;

        player.yBodyRot = 180f + f * 20f;
        player.setYRot(180f + f * 40f);
        player.setXRot(-f1 * 20f);
        player.yHeadRot = player.getYRot();
        player.yHeadRotO = player.getYRot();

        InventoryScreen.renderEntityInInventory(
                gui,
                (float) renderX,
                (float) renderY,
                scale,
                new Vector3f(),
                quatF,
                null,
                player
        );

        player.yBodyRot = f2;
        player.setYRot(f3);
        player.setXRot(f4);
        player.yHeadRot = f5;
        player.yHeadRotO = f6;

    }

    private void renderTooltips(GuiGraphics gui, int mouseX, int mouseY){

        int barTooltipX = leftPos+2; int barTooltipY = topPos+98; int barTooltipW = 70; int barTooltipH = 5;

        if(isHovering(mouseX, mouseY, barTooltipX, barTooltipY, barTooltipW, barTooltipH)){
            String xpTooltip = SmartFormat(ClientCache.GetXP()) + "/" + SmartFormat(ClientCache.GetXPGOAL()) ;
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
    private boolean isHovering(int mouseX, int mouseY, int x, int y, int width, int height){
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
    //states
    @Override
    protected void renderBlurredBackground(float partialTick){}
    @Override
    public boolean isPauseScreen(){
        return false;
    }
    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

}
