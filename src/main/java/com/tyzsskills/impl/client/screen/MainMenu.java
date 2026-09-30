package com.tyzsskills.impl.client.screen;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.tools.FormatTools;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.client.Styles;
import com.tyzsskills.integration.ui.models.UIContainer;
import com.tyzsskills.integration.ui.models.UIImage;
import com.tyzsskills.integration.ui.models.UIProgressBar;
import com.tyzsskills.integration.ui.models.UIText;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class MainMenu extends Screen {
    public static final int WIDTH = 352, HEIGHT = 139;

    protected final UIContainer mainPanel;
    protected final ClientCache cache;

    private SkillPanel skillPanel;
//    private EditBox searchBar;

    private int leftPos;
    private int topPos;


    public MainMenu(){
        super(Component.translatable("gui.tyzs_skills.title"));
        cache = ClientCache.get();

        mainPanel = new UIContainer(0, 0, WIDTH, HEIGHT);
        skillPanel = new SkillPanel(this);
    }

    @Override
    protected void init(){
        super.init();
        leftPos = (width - WIDTH)/2;
        topPos = (height - HEIGHT)/2;

        mainPanel.clear();
        mainPanel.updatePosition(leftPos, topPos);

        skillPanel.updatePositions(leftPos, topPos);


        //Background Icon
        mainPanel.addChild(new UIImage(0, 0, () -> Styles.MENU_BACKGROUND));

        //Progress Bar
        mainPanel.addChild(new UIProgressBar(2, 105, () -> Styles.XP_BAR, () -> cache.getXp()/cache.getXpGoal())
                .withTooltip(() -> Component.literal(cache.getXp() + "/" + FormatTools.defaultFloat(cache.getXpGoal()))));

        //Texts
        var textScale = .63f;
        mainPanel.addChild(new UIText(4, 96, 26, 5, () -> Component.translatable("gui.tyzs_skills.Lvl"))
                .withScale(textScale, Enums.ScalePivot.TOP_LEFT));
        mainPanel.addChild(new UIText(4, 96, 26, 5, () -> Component.literal(FormatTools.bigFloat(cache.getLevel())))
                .withAlignment(Enums.TextAlignment.RIGHT)
                .withScale(textScale, Enums.ScalePivot.TOP_LEFT));

        mainPanel.addChild(new UIText(37, 96, 26, 5, () -> Component.translatable("gui.tyzs_skills.SP"))
                .withScale(textScale, Enums.ScalePivot.TOP_LEFT));
        mainPanel.addChild(new UIText(37, 96, 26, 5, () -> Component.literal(FormatTools.bigFloat(cache.getSp())))
                .withAlignment(Enums.TextAlignment.RIGHT)
                .withScale(textScale, Enums.ScalePivot.TOP_LEFT));
    }


    //OVERRIDES
    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick){
        super.renderBackground(gui, mouseX, mouseY, partialTick);

        renderEntity(gui, 39, mouseX, mouseY);
        mainPanel.draw(gui, mouseX, mouseY, partialTick);

        skillPanel.render(gui, mouseX, mouseY, partialTick);
        skillPanel.drawTooltips(gui, font, mouseX, mouseY);

        mainPanel.drawTooltips(gui, font, mouseX, mouseY);
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button){
        int mX = (int) mouseX;
        int mY = (int) mouseY;

        if(button != 0) return false;
        if(mainPanel.handleClick(mX, mY)) return true;
        if(skillPanel.mouseClicked(mouseX, mouseY, button)) return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (skillPanel.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) return true;
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    @Override
    protected void renderBlurredBackground(float partialTick){}
    @Override
    public boolean isPauseScreen(){
        return false;
    }
    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}
    //  @Override
//    public void removed() {
//        if(!Config.KEEP_SEARCH_QUERY.getAsBoolean()) SortingTools.setSearchQuery("");
//        super.removed();
//    }

    //RENDERER
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


    //UTILS
    private boolean isHovering(int mouseX, int mouseY, int x, int y, int width, int height){
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
}
