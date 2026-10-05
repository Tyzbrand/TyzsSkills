package com.tyzsskills.impl.client.screen;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.tools.FormatTools;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.client.Styles;
import com.tyzsskills.impl.client.active.SortingManager;
import com.tyzsskills.impl.client.models.SkillPanel;
import com.tyzsskills.integration.ui.models.*;
import com.tyzsskills.integration.ui.records.UIStyles;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class MainMenu extends Screen {
    public static final int WIDTH = 352, HEIGHT = 139;

    protected final UIContainer mainPanel;
    protected final ClientCache cache;

    private final SkillPanel skillPanel;
//    private EditBox searchBar;

    private int leftPos;
    private int topPos;

    //region STYLES
    private static final UIStyles.ImageStyle BACKGROUND =
            new UIStyles.ImageStyle(MainMenu.WIDTH, MainMenu.HEIGHT, 0, 0, Styles.MAIN_TEXTURE);

    private static final UIStyles.ImageStyle XP_BAR =
            new UIStyles.ImageStyle(62, 5, 8, 217, Styles.MAIN_TEXTURE);

    private static final UIStyles.ButtonStyle TYPE_SELECTED =
            new UIStyles.ButtonStyle(22, 20, 40, 240, Styles.MAIN_TEXTURE);
    private static final UIStyles.ButtonStyle TYPE_UNSELECTED =
            new UIStyles.ButtonStyle(22, 20, 40, 260, Styles.MAIN_TEXTURE);
    //endregion

    //PARENT
    public MainMenu(){
        super(Component.translatable("gui.tyzs_skills.title"));
        cache = ClientCache.get();

        mainPanel = new UIContainer(0, 0, WIDTH, HEIGHT);

        skillPanel = new SkillPanel(0, 0, WIDTH, HEIGHT);
        skillPanel.withVisibility(() -> SortingManager.getCurrentMenuType() == Enums.MenuFocus.SKILL);
    }

    @Override
    protected void init(){
        super.init();
        leftPos = (width - WIDTH)/2;
        topPos = (height - HEIGHT)/2;

        mainPanel.clear();
        mainPanel.updatePosition(leftPos, topPos);

        buildDefaultUis();
        mainPanel.addChild(skillPanel);
    }

    //IMPLEMENTATION
    protected void buildDefaultUis(){
        //Background Icon
        mainPanel.addChild(new UIImage(0, 0, () -> BACKGROUND));

        //Type Selector
        mainPanel.addChild(new UIButton(63, 6,
                () -> SortingManager.getCurrentMenuType() == Enums.MenuFocus.SKILL ? TYPE_SELECTED : TYPE_UNSELECTED,
                () -> handleTypeChange(Enums.MenuFocus.SKILL)));

        mainPanel.addChild(new UIButton(63, 27,
                () -> SortingManager.getCurrentMenuType() == Enums.MenuFocus.SPELL ? TYPE_SELECTED : TYPE_UNSELECTED,
                () -> handleTypeChange(Enums.MenuFocus.SPELL)));

        //Entity
        mainPanel.addChild(new UIEntity(8, 14, 40, 70, () -> this.minecraft.player, 39));

        //Progress Bar
        mainPanel.addChild(new UIProgressBar(2, 105, () -> XP_BAR, () -> cache.getXp()/cache.getXpGoal())
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

        mainPanel.draw(gui, mouseX, mouseY, partialTick);
        mainPanel.drawTooltips(gui, font, mouseX, mouseY);
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button){
        int mX = (int) mouseX;
        int mY = (int) mouseY;

        if(button != 0) return false;
        if(mainPanel.handleClick(mX, mY)) return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (mainPanel.handleScroll(mouseX, mouseY, scrollY)) return true;
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (mainPanel.handleDrag(mouseX, mouseY, button, dragX, dragY)) return true;
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if(mainPanel.handleRelease( mouseX,  mouseY, button)) return true;
        return super.mouseReleased(mouseX, mouseY, button);
    }
    @Override
    protected void renderBlurredBackground(float partialTick){}
    @Override
    public boolean isPauseScreen(){
        return false;
    }
    //  @Override
//    public void removed() {
//        if(!Config.KEEP_SEARCH_QUERY.getAsBoolean()) SortingTools.setSearchQuery("");
//        super.removed();
//    }

    //region EXTRACTED LOGIC
    // ==================== BUTTON CLICKS ====================
    private boolean handleTypeChange(Enums.MenuFocus type){
        SortingManager.setMenuType(type);
        return true;
    }
    //endregion

}
