package com.tyzsskills.integration.ui.renderers;

import com.tyzsskills.Settings;
import com.tyzsskills.integration.ui.models.UITooltip;
import net.minecraft.client.gui.GuiGraphics;

public class UITooltipRenderer {
    public static void render(GuiGraphics gui, UITooltip tooltip, int mouseX, int mouseY, int screenWidth, int screenHeight) {
        if (tooltip == null || !tooltip.isVisible()) return;

        var scale = Settings.Client.TOOLTIP_SCALES.get();
        tooltip.updatePosition(0, 0);

        var style = tooltip.style.get();

        var padX = style.paddingX();
        var padY = style.paddingY();

        var rawWidth = tooltip.getRawWidth();
        var rawHeight = tooltip.getRawHeight();

        if (rawWidth <= 0 || rawHeight <= 0) return;

        var shiftX = tooltip.getVisualX();
        var shiftY = tooltip.getVisualY();

        var unscaledWidth = rawWidth + (padX * 2);
        var unscaledHeight = rawHeight + (padY * 2);

        var totalWidth = Math.round(unscaledWidth * scale);
        var totalHeight = Math.round(unscaledHeight * scale);

        var x = mouseX + 4;
        var y = mouseY - 10;

        if(x + totalWidth > screenWidth - 4) x = mouseX - 10 - totalWidth;
        if(y + totalHeight > screenHeight - 4) y = screenHeight - 4 - totalHeight;
        if(y < 4) y = 4;

        gui.pose().pushPose();
        gui.pose().translate(x, y, 400.0f);
        gui.pose().scale(scale, scale, 1.0f);

        UIBackgroundRenderer.render(gui, 0, 0, unscaledWidth, unscaledHeight, style);

        gui.pose().translate(padX - shiftX, padY - shiftY, 0f);
        tooltip.draw(gui, Math.round((mouseX - x) / scale), Math.round((mouseY - y) / scale), 0f);
        gui.pose().popPose();
    }
}
