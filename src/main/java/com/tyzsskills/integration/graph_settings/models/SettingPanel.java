package com.tyzsskills.integration.graph_settings.models;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.model.settings.Setting;
import com.tyzsskills.integration.graph_settings.SettingMenu;
import com.tyzsskills.integration.ui.models.UIBackground;
import com.tyzsskills.integration.ui.models.UIBackgroundButton;
import com.tyzsskills.integration.ui.models.UIContainer;
import com.tyzsskills.integration.ui.models.UIText;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public abstract class SettingPanel extends UIContainer {
    private static final int WIDTH = 300, HEIGHT = 183;
    private static final int SPACE = 1;

    protected record Measurements(int X, int Y, int width, int height) { }

    private static final Measurements categoriesSide = new Measurements(0, 0, 65, HEIGHT/3 * 2);
    private static final Measurements buttonsSide = new Measurements(0, categoriesSide.height() + SPACE, 65, (HEIGHT/3) - SPACE);
    protected static final Measurements mainsSide = new Measurements(categoriesSide.width() + SPACE, 0, 135, HEIGHT);
    private static final Measurements focusSide = new Measurements(mainsSide.X() + mainsSide.width() + SPACE, 0, 98, HEIGHT);

    private final UIContainer categoriesPanel = new UIContainer(categoriesSide.X(), categoriesSide.Y());
    private final UIContainer buttonsPanel = new UIContainer(buttonsSide.X(), buttonsSide.Y());
    private final UIContainer focusPanel = new UIContainer(focusSide.X(), focusSide.Y());

    private final List<Setting<?, ?>> settings;
    private Enums.SettingCategory currentCategory;

    public SettingPanel(int offsetX, int offsetY, Collection<Setting<?, ?>> settingsSource) {
        super(offsetX, offsetY);

        settings = List.copyOf(settingsSource);

        init();
        renderLimits();

        buildCategories();
        addChild(buildSettings());
    }

    private void init(){
        addChild(categoriesPanel);
        addChild(buttonsPanel);
        addChild(focusPanel);
    }

    private void renderLimits(){
        var catArea = new UIBackground(0, 0, categoriesSide.width(), categoriesSide.height(), () -> SettingMenu.MAIN_STYLE);
        //categoriesPanel.addChild(catArea);

        var btnArea = new UIBackground(0, 0, buttonsSide.width(), buttonsSide.height(), () -> SettingMenu.MAIN_STYLE);
        buttonsPanel.addChild(btnArea);

        addChild(new UIBackground(mainsSide.X(), mainsSide.Y(), mainsSide.width(), mainsSide.height(), () -> SettingMenu.MAIN_STYLE));

        var desArea = new UIBackground(0, 0, focusSide.width(), focusSide.height(), () -> SettingMenu.MAIN_STYLE);
        focusPanel.addChild(desArea);
    }


    //CATEGORIES
    private void buildCategories(){
        var affectedCategories = new ArrayList<Enums.SettingCategory>();
        for(var setting : settings) {
            var cat = setting.meta().category();
            if(!affectedCategories.contains(cat)) affectedCategories.add(cat);
        }

        affectedCategories.sort(Comparator.comparingInt(Enums.SettingCategory::priority));
        currentCategory = affectedCategories.getFirst();

        var x = 0; var startY = 0;
        for(var category : affectedCategories){
            categoriesPanel.addChild(new UIBackgroundButton(x, startY, categoriesSide.width(), 15,
                    () -> handleCategoryChange(category),
                    () -> currentCategory != category ? SettingMenu.SECONDARY_STYLE : SettingMenu.MAIN_STYLE,
                    () -> currentCategory != category ? SettingMenu.SECONDARY_STYLE_HOVER : SettingMenu.MAIN_STYLE_HOVER));

            var raw = Component.translatable(category.label()).getString().toLowerCase();
            var trad = Component.literal(raw);
            categoriesPanel.addChild(new UIText(x + 3, startY + 4, () -> trad)
                    .withScale(.75f, Enums.ScalePivot.TOP_LEFT));

            startY += 15;
        }
    }
    private boolean handleCategoryChange(Enums.SettingCategory category){
        currentCategory = category;
        return true;
    }

    //SETTINGS
    protected abstract UIContainer buildSettings();
}
