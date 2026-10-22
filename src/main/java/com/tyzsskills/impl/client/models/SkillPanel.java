package com.tyzsskills.impl.client.models;

import com.tyzsskills.Config;
import com.tyzsskills.Constants;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.records.Category;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.client.active.SortingManager;
import com.tyzsskills.impl.client.Styles;
import com.tyzsskills.impl.client.tooltips.SearchBarTooltip;
import com.tyzsskills.impl.client.tooltips.SortingDirectionTooltip;
import com.tyzsskills.impl.client.tooltips.SortingTypeTooltip;
import com.tyzsskills.impl.client.tooltips.ToggleTooltip;
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

    private static final UIStyles.ImageStyle SEARCH_BAR =
            new UIStyles.ImageStyle(217, 17, 8, 198, Styles.MAIN_TEXTURE);

    private static final UIStyles.ToggleStyle TOGGLE =
            new UIStyles.ToggleStyle(17, 9, 183, 254, 166, 254,
                    183, 245, 166, 245, Styles.MAIN_TEXTURE);

    private static final UIStyles.ButtonStyle SORTING_NAME_BTN =
            new UIStyles.ButtonStyle(15, 9, 2, 224, 19, 224, Styles.MAIN_TEXTURE);
    private static final UIStyles.ButtonStyle SORTING_PRICE_BTN =
            new UIStyles.ButtonStyle(15, 9, 2, 234, 19, 234, Styles.MAIN_TEXTURE);
    private static final UIStyles.ButtonStyle SORTING_LEVEL_BTN =
            new UIStyles.ButtonStyle(15, 9, 2, 244, 19, 244, Styles.MAIN_TEXTURE);
    private static final UIStyles.ButtonStyle SORTING_DEFAULT_BTN =
            new UIStyles.ButtonStyle(15, 9, 2, 254, 19, 254, Styles.MAIN_TEXTURE);
    private static final UIStyles.ButtonStyle SORTING_ASC_BTN =
            new UIStyles.ButtonStyle(15, 9, 2, 264, 19, 264, Styles.MAIN_TEXTURE);
    private static final UIStyles.ButtonStyle SORTING_DSC_BTN =
            new UIStyles.ButtonStyle(15, 9, 2, 274, 19, 274, Styles.MAIN_TEXTURE);
    //endregion

    //PARENTS
    public SkillPanel(int offsetX, int offsetY) {
        super(offsetX, offsetY);

        cache = ClientCache.get();

        tabsPanel = new UIContainer(0, 0);
        scrollView = new UIScrollView(98, 25, 214, 108);

        //TO SEE SCROLL VIEW SHAPE =>scrollView.addChild(new UIBackground(0, 0, 214, 150, 0xFFD6AD55));

        categoryDisplayName = getCategoryDisplayName(SortingManager.getCurrentCategory());
        this.addChild(new UIText(307, 10, () -> categoryDisplayName)
                .withAlignment(Enums.TextAlignment.RIGHT)
                .withBackground(() -> Styles.DEFAULT_BACKGROUND, () -> true)
                .withScale(.85f, Enums.ScalePivot.CENTER));

        this.addChild(tabsPanel);
        this.addChild(scrollView);

        if (!Config.KEEP_SEARCH_QUERY.get()) SortingManager.setSearchQuery("");

        initOffset();

        buildTabs();
        buildFilterTools();

        refreshScrollView();
    }

    //INITIALIZATION
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


            var catTooltip = new UITooltip(Component.translatable(category.displayName()));
            tabsPanel.addChild(new UITabButton(x, y, 29, 20, () -> handleCategoryChange(category), icon)
                    .withStyle(() -> SortingManager.getCurrentCategory() == category ? TAB_BTN_SELECTED : TAB_BTN_UNSELECTED)
                    .withTooltip(() -> catTooltip));
            x += 28;
        }

        tabsPanel.addChild(new UIButton(x + 1, 12, () -> NEXT_TAB, this::handleTabNext)
                .withDisabled(this::isTabNextDisabled).withShade(this::isTabNextDisabled));
    }
    private void buildFilterTools(){
        this.addChild(new UIImage(97, -17, () -> SEARCH_BAR));

        int startX = 214;

        //TODO: translation
        var maxedTooltip = new ToggleTooltip("Show Maxed", SortingManager::showMaxed);
        this.addChild(new UIToggle(startX, -9, 17, 9, this::handleMaxedSwitch, SortingManager::showMaxed, () -> TOGGLE)
                .withTooltip(() -> maxedTooltip)
                .withScale(.85f, Enums.ScalePivot.TOP_LEFT));

        //TODO: translation
        var unaffordableTooltip = new ToggleTooltip("Show Unaffordable", SortingManager::showUnaffordable);
        this.addChild(new UIToggle(startX += 17, -9, 17, 9, this::handleUnaffordableSwitch, SortingManager::showUnaffordable, () -> TOGGLE)
                .withTooltip(() -> unaffordableTooltip)
                .withScale(.85f, Enums.ScalePivot.TOP_LEFT));

        //TODO: translation
        var lockedTooltip = new ToggleTooltip("Show Locked", SortingManager::showLocked);
        this.addChild(new UIToggle(startX + 17, -9, 17, 9, this::handleLockedSwitch, SortingManager::showLocked, () -> TOGGLE)
                .withTooltip(() -> lockedTooltip)
                .withScale(.85f, Enums.ScalePivot.TOP_LEFT));

        var sortingDirTooltip = new SortingDirectionTooltip(SortingManager::getSortingDirection);
        this.addChild(new UIButton(294, -10, this::getSortingDirectionStyle, this::handleSortingDirectionChange)
                .withTooltip(() -> sortingDirTooltip));

        var sortingTypeTooltip = new SortingTypeTooltip(SortingManager::getSortingType);
        this.addChild(new UIButton(277, -10, this::getSortingTypeStyle, this::handleSortingTypeChange)
                .withTooltip(() -> sortingTypeTooltip));

        var searchBarTooltip = new SearchBarTooltip();
        this.addChild(new UIEditBox(106, -10, 84, 9, this::handleSearchChange, SortingManager::getSearchQuery)
                .withTooltip(() -> searchBarTooltip));

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
    public void refreshScrollView(){
        scrollView.clear();

        SortingManager.refreshList();
        var skillOrder = SortingManager.getCurrentSkillOrder();

        if(skillOrder.isEmpty()){
            //TODO: translation
            var message = Component.literal("-No Skills There-");
            scrollView.addChild(new UIText(scrollView.getRawWidth() / 2, scrollView.getRawHeight() / 2, () -> message)
                    .withBackground(() -> Styles.DEFAULT_BACKGROUND, () -> false)
                    .withAlignment(Enums.TextAlignment.CENTER));
            return;
        }

        var x = 0;
        var y = SCROLL_ITEM_SPACING;
        var rowCount = 0;

        for(var skill : skillOrder){
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
    private void handleSearchChange(String query) {
        SortingManager.setSearchQuery(query);
        refreshScrollView();
    }
    private boolean handleMaxedSwitch(){
        SortingManager.toggleShowMaxed();
        refreshScrollView();
        return true;
    }
    private boolean handleUnaffordableSwitch(){
        SortingManager.toggleShowUnaffordable();
        refreshScrollView();
        return true;
    }
    private boolean handleLockedSwitch(){
        SortingManager.toggleShowLocked();
        refreshScrollView();
        return true;
    }
    private boolean handleSortingTypeChange(){
        SortingManager.toggleSortingType();
        refreshScrollView();
        return true;
    }

    private boolean handleSortingDirectionChange(){
        SortingManager.toggleSortingDirection();
        refreshScrollView();
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

    // ==================== STYLES ====================
    public UIStyles.ButtonStyle getSortingTypeStyle(){
        return switch (SortingManager.getSortingType()) {
            case NAME -> SORTING_NAME_BTN;
            case PRICE -> SORTING_PRICE_BTN;
            case LEVEL -> SORTING_LEVEL_BTN;
            default -> SORTING_DEFAULT_BTN;
        };
    }

    public UIStyles.ButtonStyle getSortingDirectionStyle(){
        return switch (SortingManager.getSortingDirection()) {
            case ASCENDING -> SORTING_ASC_BTN;
            case DESCENDING -> SORTING_DSC_BTN;
        };
    }
    //endregion
}
