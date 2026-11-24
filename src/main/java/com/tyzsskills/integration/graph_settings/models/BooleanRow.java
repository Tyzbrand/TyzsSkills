package com.tyzsskills.integration.graph_settings.models;

import com.tyzsskills.api.model.settings.BoolSetting;
import com.tyzsskills.integration.graph_settings.SettingMenu;
import com.tyzsskills.integration.ui.models.UIBackgroundButton;
import com.tyzsskills.integration.ui.models.UIContainer;

public class BooleanRow extends SettingRow{
    private final BoolSetting setting;

    public BooleanRow(int offsetX, int offsetY, BoolSetting setting) {
        super(offsetX, offsetY, setting);

        this.setting = setting;
    }

    @Override
    protected UIContainer specificBuild() {
        var container = new UIContainer(0, 0);

        container.addChild(new UIBackgroundButton(0, 0, 17, 10,
                () -> true, () -> SettingMenu.MAIN_STYLE, () -> SettingMenu.MAIN_STYLE_HOVER));

        return container;
    }

}
