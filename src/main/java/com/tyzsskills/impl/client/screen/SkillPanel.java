package com.tyzsskills.impl.client.screen;

import com.tyzsskills.Constants;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.client.active.SortingManager;
import com.tyzsskills.impl.client.models.SubScreen;
import com.tyzsskills.impl.client.ui.*;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SkillPanel extends SubScreen {
    protected final ClientCache cache;

    protected final UIContainer tabsPanel;
    protected final UIContainer mainPanel;

    protected final UIScrollView scrollView;

    protected int categoryOffset = 0;

    protected MainMenu menu;

    protected final int SCROLL_ITEM_SPACING = 3;
    protected final int SCROLL_ITEM_PER_LINE = 5;
    protected final int SCROLL_ITEM_HEIGHT = 28;
    protected final int SCROLL_ITEM_WIDTH = 28;

    public SkillPanel(MainMenu menu){
        this.menu = menu;

        cache = ClientCache.get();

        mainPanel = new UIContainer(0, 0, MainMenu.WIDTH, MainMenu.HEIGHT);
        tabsPanel = new UIContainer(0, 0, MainMenu.WIDTH, MainMenu.HEIGHT);

        scrollView = new UIScrollView(98, 25, 158, 108);
        //TO SEE SCROLL VIEW SHAPE => scrollView.addChild(new UIBackground(0, 0, 158, 150, 0xFFD6AD55));

        mainPanel.addChild(tabsPanel);
        mainPanel.addChild(scrollView);

        mainPanel.addChild(new UIBackground(267, 5, 80, 129, UIStyleRegistries.COLOR_BG));

        initOffset();
        buildTabs();
        refreshScrollView();
    }

    //PARENT
    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick){
        mainPanel.draw(gui, mouseX, mouseY, partialTick);
    }
    @Override
    public void drawTooltips(GuiGraphics gui, Font font, int mouseX, int mouseY){
        mainPanel.drawTooltips(gui, font, mouseX, mouseY);
    }
    @Override
    public void updatePositions(int x, int y){
        mainPanel.updatePosition(x, y);
    }
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return scrollView.mouseScrolled(mouseX, mouseY, scrollY);
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return mainPanel.handleClick((int)mouseX, (int)mouseY);
    }

    //IMPLEMENTATION TABS
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
                        refreshScrollView();
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

    //IMPLEMENTATION SCROLL
    public void refreshScrollView(){
        scrollView.clear();

        SortingManager.refreshList();

        var x = 0;
        var y = SCROLL_ITEM_SPACING;
        var rowCount = 0;

        for(var skill : SortingManager.getCurrentSkillOrder()){
            if(rowCount >= SCROLL_ITEM_PER_LINE){
                y += SCROLL_ITEM_SPACING + SCROLL_ITEM_HEIGHT;
                x = 0;
                rowCount = 0;
            }

            x += SCROLL_ITEM_SPACING;
            if(rowCount > 0) x += SCROLL_ITEM_WIDTH;

            scrollView.addChild(SkillCard.getCard(skill, x, y));

            rowCount++;
        }
    }
}
