package com.tyzsskills.impl.client.models;

import com.tyzsskills.Constants;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.records.Category;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.client.active.SortingManager;
import com.tyzsskills.impl.client.Styles;
import com.tyzsskills.integration.ui.models.*;
import com.tyzsskills.integration.ui.records.UIStyles;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class SkillPanel extends UIContainer {
    protected final ClientCache cache;

    protected final UIContainer tabsPanel;
    protected final UIScrollView scrollView;

    protected int categoryOffset = 0;
    private MutableComponent categoryDisplayName = Component.empty();

    protected final int SCROLL_ITEM_SPACING = 4;
    protected final int SCROLL_ITEM_PER_LINE = 3;

    //region STYLES
    private static final UIStyles.ButtonStyle TAB_BTN_SELECTED =
            new UIStyles.ButtonStyle(29, 20, 87, 240, 87, 240, Styles.MAIN_TEXTURE);
    private static final UIStyles.ButtonStyle TAB_BTN_UNSELECTED =
            new UIStyles.ButtonStyle(29, 20, 116, 240, 116, 240, Styles.MAIN_TEXTURE);

    private static final UIStyles.ButtonStyle PREV_TAB =
            new UIStyles.ButtonStyle(9, 13, 69, 247, 78, 247, Styles.MAIN_TEXTURE);
    private static final UIStyles.ButtonStyle NEXT_TAB =
            new UIStyles.ButtonStyle(9, 13, 154, 247, 145, 247, Styles.MAIN_TEXTURE);
    //endregion

    //PARENTS
    public SkillPanel(int offsetX, int offsetY, int width, int height) {
        super(offsetX, offsetY, width, height);

        cache = ClientCache.get();

        tabsPanel = new UIContainer(0, 0, width, height);
        scrollView = new UIScrollView(98, 25, 214, 108);

        //TO SEE SCROLL VIEW SHAPE =>scrollView.addChild(new UIBackground(0, 0, 214, 150, 0xFFD6AD55));

        categoryDisplayName = getCategoryDisplayName(SortingManager.getCurrentCategory());
        this.addChild(new UIText(253, 9, 53, 18, () -> categoryDisplayName)
                .withAlignment(Enums.TextAlignment.RIGHT).withBackground(() -> Styles.DEFAULT_BACKGROUND, () -> true)
                .withScale(.85f, Enums.ScalePivot.CENTER));

        this.addChild(tabsPanel);
        this.addChild(scrollView);

        initOffset();
        buildTabs();
        refreshScrollView();
    }

    //IMPLEMENTATION
    private void initOffset(){
        var activeCat = SortingManager.getCurrentCategory();
        var categories = cache.getAllCategories();

        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i) == activeCat) {
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

        tabsPanel.addChild(new UIButton(97, 12, () -> PREV_TAB, this::handleTabBack)
                .withDisabled(this::isTabBackDisabled).withShade(this::isTabBackDisabled));

        var x = 106;
        var y = 5;
        var end = Math.min(categoryOffset + Constants.MAX_CATEGORIES_PER_LINE, total);

        for(int i = categoryOffset; i < end; i++){
            var category = categories.get(i);
            var icon = ResourceLocation.tryParse(category.icon());
            if(icon == null) continue;

            tabsPanel.addChild(new UITabButton(x, y, 29, 20, () -> handleCategoryChange(category), icon)
                    .withStyle(() -> SortingManager.getCurrentCategory() == category ? TAB_BTN_SELECTED : TAB_BTN_UNSELECTED)
                    .withTooltip(() -> Component.translatable(category.displayName())));
            x += 28;
        }

        tabsPanel.addChild(new UIButton(x + 1, 12, () -> NEXT_TAB, this::handleTabNext)
                .withDisabled(this::isTabNextDisabled).withShade(this::isTabNextDisabled));
    }
    public void refreshScrollView(){
        scrollView.clear();

        SortingManager.refreshList();

        var x = 0;
        var y = SCROLL_ITEM_SPACING;
        var rowCount = 0;

        for(var skill : SortingManager.getCurrentSkillOrder()){
            if(rowCount >= SCROLL_ITEM_PER_LINE){
                y += SCROLL_ITEM_SPACING + SkillCard.HEIGHT;
                x = 0;
                rowCount = 0;
            }

            x += SCROLL_ITEM_SPACING;
            if(rowCount > 0) x += SkillCard.WIDTH;

            scrollView.addChild(new SkillCard(skill, x, y));

            rowCount++;
        }
    }

    //region EXTRACTED LOGIC
    // ==================== BUTTON CLICKS ====================
    private boolean handleTabBack(){
        if(isTabBackDisabled()) return false;

        categoryOffset = Math.max(0, categoryOffset - Constants.MAX_CATEGORIES_PER_LINE);
        buildTabs();
        return true;
    }
    private boolean handleTabNext(){
        if(isTabNextDisabled()) return false;

        categoryOffset += Constants.MAX_CATEGORIES_PER_LINE;
        buildTabs();
        return true;

    }
    private boolean handleCategoryChange(Category category){
        SortingManager.setCategory(category);
        refreshScrollView();
        categoryDisplayName = getCategoryDisplayName(category);
        return true;
    }

    // ==================== CONDITIONS ====================
    private boolean isTabBackDisabled(){
        return categoryOffset <= 0;
    }
    private boolean isTabNextDisabled(){
        var total = cache.getAllCategories().size();
        return categoryOffset + Constants.MAX_CATEGORIES_PER_LINE >= total;
    }

    // ==================== TEXTS ====================
    public MutableComponent getCategoryDisplayName(Category category){
        if(category == null) return Component.empty();
        return Component.translatable(category.displayName());
    }
    //endregion
}
