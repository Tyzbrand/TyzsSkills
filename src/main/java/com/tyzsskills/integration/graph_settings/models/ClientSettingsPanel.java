package com.tyzsskills.integration.graph_settings.models;

import com.tyzsskills.Settings;
import com.tyzsskills.integration.ui.models.UIContainer;

public class ClientSettingsPanel extends SettingPanel {
    public ClientSettingsPanel(int offsetX, int offsetY) {
        super(offsetX, offsetY, Settings.Client.getSettings());
    }

    @Override
    protected UIContainer buildSettings() {
        var x = SettingPanel.mainsSide.X();
        var startY = SettingPanel.mainsSide.Y();

        var container = new UIContainer(x, startY);

        container.addChild(new BooleanRow(0, 0, Settings.Client.RENDER_SHADOWS));

        return container;
    }
}
