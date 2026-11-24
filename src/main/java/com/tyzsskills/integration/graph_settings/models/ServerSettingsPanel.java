package com.tyzsskills.integration.graph_settings.models;

import com.tyzsskills.Settings;
import com.tyzsskills.integration.ui.models.UIContainer;

public class ServerSettingsPanel extends SettingPanel {

    public ServerSettingsPanel(int offsetX, int offsetY) {
        super(offsetX, offsetY, Settings.Server.getSettings());
    }

    @Override
    protected UIContainer buildSettings() {
        var x = SettingPanel.mainsSide.X();
        var startY = SettingPanel.mainsSide.Y();

        var container = new UIContainer(x, startY);
        return container;
    }
}
