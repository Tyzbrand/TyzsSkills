package com.tyzsskills.impl.client.screen;

import com.tyzsskills.Config;
import com.tyzsskills.Tyzsskills;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.client.models.*;
import com.tyzsskills.impl.client.tools.SortingTools;
import com.tyzsskills.impl.client.ui.UIContainer;
import com.tyzsskills.impl.client.ui.UIImage;
import com.tyzsskills.impl.client.ui.UIProgressBar;
import com.tyzsskills.impl.client.ui.UIStyleRegistries;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class MainMenu extends Screen {
    private static final ResourceLocation background = ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID,
            "textures/gui/background.png");

    private static final ResourceLocation mainFont = ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID,
            "main_font");

    public static final int WIDTH = 301, HEIGHT = 142;

    protected final UIContainer mainPanel;
    protected final ClientCache cache;

//    private CustomScrollView scrollView;
//    private EditBox searchBar;


    private int leftPos;
    private int topPos;


    public MainMenu(){
        super(Component.translatable("gui.tyzs_skills.title"));
        this.cache = ClientCache.get();

        this.mainPanel = new UIContainer(0, 0, WIDTH, HEIGHT);
    }

    @Override
    protected void init(){
        super.init();
        this.leftPos = (this.width - WIDTH)/2;
        this.topPos = (this.height - HEIGHT)/2;


        mainPanel.updatePosition(this.leftPos, this.topPos);

        //Background Icon
        mainPanel.addChild(new UIImage(0, 0, () -> UIStyleRegistries.MENU_BACKGROUND));

        //Progress Bar
        mainPanel.addChild(new UIProgressBar(94, 144, () -> UIStyleRegistries.XP_BAR, () -> cache.getXp()/cache.getXpGoal()));
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick){
        super.renderBackground(gui, mouseX, mouseY, partialTick);

        this.renderEntity(gui, 39, mouseX, mouseY);
        mainPanel.draw(gui, mouseX, mouseY, partialTick);
    }


    //Locals
    private void renderEntity(GuiGraphics gui, int scale,  int mouseX, int mouseY){
        LivingEntity player = this.minecraft.player;
        if(player == null) return;

        int renderX = leftPos + 28;
        int renderY = topPos + 84;

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


    //Uilitaires
    private boolean isHovering(int mouseX, int mouseY, int x, int y, int width, int height){
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }


    private void renderIcon(GuiGraphics gui, float scale, int u, int v, int w, int h, int btnX, int btnY, int btnS){

        float scaledSize = w * scale;
        float offset = (btnS - scaledSize) / 2f;

        float targetVisualX = btnX + offset;
        float targetVisualY = btnY + offset;

        int drawX = (int)(targetVisualX / scale);
        int drawY = (int)(targetVisualY / scale);


        gui.pose().pushPose();
        gui.pose().scale(scale, scale, 1.0f);
        gui.setColor(1.0f, 1.0f, 1.0f, 1f);


        gui.blit(background, drawX, drawY, u, v, w, h, 325, 325);

        gui.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        gui.pose().popPose();
    }

    private void renderBackdrop(GuiGraphics gui, int x, int y, int width, int height, int color) {
        gui.fill(x, y + 1, x + width, y + height - 1, color);
        gui.fill(x + 1, y, x + width - 1, y + 1, color);
        gui.fill(x + 1, y + height - 1, x + width - 1, y + height, color);

        gui.fill(x + 1, y, x + width - 1, y + 1, 0xFFFFFFFF); // Haut
        gui.fill(x + 1, y + height - 1, x + width - 1, y + height, 0xFFFFFFFF); // Bas
        gui.fill(x, y + 1, x + 1, y + height - 1, 0xFFFFFFFF); // Gauche
        gui.fill(x + width - 1, y + 1, x + width, y + height - 1, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button){
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void removed() {
        if(!Config.KEEP_SEARCH_QUERY.getAsBoolean()) SortingTools.setSearchQuery("");
        super.removed();
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
