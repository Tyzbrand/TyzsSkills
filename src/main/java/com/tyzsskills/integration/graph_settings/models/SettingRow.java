package com.tyzsskills.integration.graph_settings.models;

import com.tyzsskills.api.model.settings.Setting;
import com.tyzsskills.integration.graph_settings.SettingMenu;
import com.tyzsskills.integration.ui.models.UIBackgroundButton;
import com.tyzsskills.integration.ui.models.UIContainer;
import com.tyzsskills.integration.ui.models.UIText;
import net.minecraft.network.chat.Component;

public abstract class SettingRow extends UIContainer {
    public static final int WIDTH = 135, HEIGHT = 17;

    public SettingRow(int offsetX, int offsetY, Setting<?, ?> setting) {
        super(offsetX, offsetY);

        addChild(new UIBackgroundButton(0, 0, WIDTH, HEIGHT, () -> true, () -> SettingMenu.MAIN_STYLE));

        var label = Component.translatable(setting.meta().displayName());
        addChild(new UIText(3, 3, () -> Component.literal("Render Shadows")));

        addChild(specificBuild());
    }


    protected abstract UIContainer specificBuild();
}
