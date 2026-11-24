package com.tyzsskills.integration.ui.models;

import com.tyzsskills.integration.ui.UIBackgroundRenderer;
import com.tyzsskills.integration.ui.abstracts.UIElement;
import com.tyzsskills.integration.ui.records.UIStyles;
import net.minecraft.client.gui.GuiGraphics;

import java.util.function.Supplier;

public class UIBackground extends UIElement {
    private final Supplier<UIStyles.BackgroundStyle> style;

    public UIBackground(int offsetX, int offsetY, int width, int height, Supplier<UIStyles.BackgroundStyle> style) {
        super(offsetX, offsetY, width, height);
        this.style = style;
    }


    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        var currentStyle = style != null ? style.get() : UIStyles.BackgroundStyle.NONE;
        UIBackgroundRenderer.render(gui, getRawX(), getRawY(), getRawWidth(), getRawHeight(), currentStyle);
    }
}
