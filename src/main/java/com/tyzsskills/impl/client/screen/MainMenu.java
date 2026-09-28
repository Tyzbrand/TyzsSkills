package com.tyzsskills.impl.client.screen;

import com.tyzsskills.Constants;
import com.tyzsskills.Tyzsskills;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.tools.FormatTools;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.client.active.ComponentManager;
import com.tyzsskills.impl.client.active.SortingManager;
import com.tyzsskills.impl.client.models.CustomScrollView;
import com.tyzsskills.impl.client.models.SkillEntry;
import com.tyzsskills.impl.client.tooltips.CategoryTooltipData;
import com.tyzsskills.impl.client.ui.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class MainMenu extends Screen {
    private static final ResourceLocation background = ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID,
            "textures/gui/background.png");

    private static final ResourceLocation mainFont = ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID,
            "main_font");

    public static final int WIDTH = 301, HEIGHT = 142;

    protected final UIContainer mainPanel;
    protected final UIContainer tabsPanel;
    protected final ClientCache cache;

    private CustomScrollView scrollView;
//    private EditBox searchBar;

    private int categoryOffset = 0;
    private int leftPos;
    private int topPos;


    public MainMenu(){
        super(Component.translatable("gui.tyzs_skills.title"));
        this.cache = ClientCache.get();

        this.mainPanel = new UIContainer(0, 0, WIDTH, HEIGHT);
        this.tabsPanel = new UIContainer(0, 0, WIDTH, HEIGHT);
    }

    @Override
    protected void init(){
        super.init();
        this.leftPos = (this.width - WIDTH)/2;
        this.topPos = (this.height - HEIGHT)/2;

        this.addScrollView();

        mainPanel.clear();
        mainPanel.updatePosition(this.leftPos, this.topPos);

        initOffset();

        //Background Icon
        mainPanel.addChild(new UIImage(0, 0, () -> UIStyleRegistries.MENU_BACKGROUND));

        //Progress Bar
        mainPanel.addChild(new UIProgressBar(2, 105, () -> UIStyleRegistries.XP_BAR, () -> cache.getXp()/cache.getXpGoal())
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

        mainPanel.addChild(tabsPanel);
        buildTabs();
        refreshList();
    }


    //OVERRIDES
    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick){
        super.renderBackground(gui, mouseX, mouseY, partialTick);

        this.renderEntity(gui, 39, mouseX, mouseY);
        mainPanel.draw(gui, mouseX, mouseY, partialTick);

        if (this.scrollView != null) {
            this.scrollView.render(gui, mouseX, mouseY, partialTick);
        }

        mainPanel.drawTooltips(gui, this.font, mouseX, mouseY);
        renderScrollViewTooltips(gui, mouseX, mouseY);
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button){
        int mX = (int) mouseX;
        int mY = (int) mouseY;

        if(mainPanel.handleClick(mX, mY)) return true;

        return super.mouseClicked(mouseX, mouseY, button);
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
    private void buildTabs(){
        tabsPanel.clear();
        var categories = cache.getAllCategories();
        var total = categories.size();

        tabsPanel.addChild(new UIButton(97, 12, () -> UIStyleRegistries.PREV_TAB,
                () -> {
                    if (categoryOffset > 0){
                        categoryOffset = Math.max(0, categoryOffset - Constants.MAX_CATEGORIES_PER_LINE);
                        buildTabs();
                        return true;
                    }
                    return false;
                })
                .withDisabled(() -> categoryOffset == 0)
                .withShade(() -> categoryOffset == 0)
        );

        var x = 106;
        var y = 5;
        var end = Math.min(categoryOffset + Constants.MAX_CATEGORIES_PER_LINE, total);

        for(int i = categoryOffset; i < end; i++){
            var category = categories.get(i);
            var icon = ResourceLocation.tryParse(category.icon());
            if(icon == null) continue;

            tabsPanel.addChild(new UITabButton(x, y, 29, 20,
                    () -> {
                SortingManager.setCategory(category.id());
                refreshList();
                return true;
                    }, icon)
                    .withStyle(() -> SortingManager.getCurrentCategory().equalsIgnoreCase(category.id())
                            ? UIStyleRegistries.TAB_BTN_SELECTED
                            : UIStyleRegistries.TAB_BTN_UNSELECTED)
                    .withTooltip(() -> Component.translatable(category.displayName()))
            );
            x += 28;
        }

        tabsPanel.addChild(new UIButton(x + 1, 12, () -> UIStyleRegistries.NEXT_TAB,
                () -> {
                    if(categoryOffset + Constants.MAX_CATEGORIES_PER_LINE < total){
                        categoryOffset += Constants.MAX_CATEGORIES_PER_LINE;
                        buildTabs();
                        return true;
                    }
                    return false;
                })
                .withDisabled(() -> categoryOffset + Constants.MAX_CATEGORIES_PER_LINE >= total)
                .withShade(() -> categoryOffset + Constants.MAX_CATEGORIES_PER_LINE >= total)
        );
    }
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
    private void addScrollView(){
        scrollView = new CustomScrollView(
                this.minecraft,
                leftPos + 95, topPos + 26,
                162, 106, 30,
                background, UIStyleRegistries.MAIN_TEXTURE_SIZE, UIStyleRegistries.MAIN_TEXTURE_SIZE,
                7, 242, 10, 242,
                3, 11);

        this.addRenderableWidget(this.scrollView);
    }
    private void renderScrollViewTooltips(GuiGraphics gui, int mouseX, int mouseY){
        if (scrollView != null && scrollView.isMouseOver(mouseX, mouseY)) {
            var hoveredCard = scrollView.getHoveredWidget(mouseX, mouseY);
            if (hoveredCard != null) hoveredCard.drawTooltips(gui, this.font, mouseX, mouseY);

        }
    }
    public void refreshList(){
        if(this.scrollView == null) return;
        scrollView.clearEntries();

        SortingManager.refreshList();

        int maxPerLine = 5;

        SkillEntry currentRow = null;
        int countInRow = 0;

        for(var skill : SortingManager.getCurrentSkillOrder()){
            if(currentRow == null || countInRow >= maxPerLine){
                currentRow = new SkillEntry();
                this.scrollView.AddEntry(currentRow);
                countInRow = 0;
            }
            currentRow.addWidget(new SkillCard(skill));
            countInRow++;
        }
        this.scrollView.setScrollAmount(0);
    }


    //ACTIVES
    private void initOffset(){
        var activeCat = SortingManager.getCurrentCategory();
        var categories = cache.getAllCategories();

        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).id().equalsIgnoreCase(activeCat)) {
                this.categoryOffset = (i / Constants.MAX_CATEGORIES_PER_LINE) * Constants.MAX_CATEGORIES_PER_LINE;
                return;
            }
        }
        this.categoryOffset = 0;
    }

    //UTILS
    private boolean isHovering(int mouseX, int mouseY, int x, int y, int width, int height){
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
}
