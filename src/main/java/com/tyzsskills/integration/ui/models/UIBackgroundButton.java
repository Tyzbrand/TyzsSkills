package com.tyzsskills.integration.ui.models;

import com.tyzsskills.integration.ui.UIBackgroundRenderer;
import com.tyzsskills.integration.ui.abstracts.UIButton;
import com.tyzsskills.integration.ui.records.UIStyles;
import net.minecraft.client.gui.GuiGraphics;

import java.util.function.Supplier;

public class UIBackgroundButton extends UIButton {
    protected Supplier<UIStyles.BackgroundStyle> style;
    protected Supplier<UIStyles.BackgroundStyle> hoveredStyle;
    protected Supplier<UIStyles.BackgroundStyle> disabledStyle;

    public UIBackgroundButton(int offsetX, int offsetY, int width, int height, Supplier<Boolean> onClick, Supplier<UIStyles.BackgroundStyle> style,
                              Supplier<UIStyles.BackgroundStyle> hoveredStyle, Supplier<UIStyles.BackgroundStyle> disabledStyle) {

        super(offsetX, offsetY, width, height, onClick);

        this.style = style;
        this.hoveredStyle = hoveredStyle;
        this.disabledStyle = disabledStyle;
    }

    public UIBackgroundButton(int offsetX, int offsetY, int width, int height, Supplier<Boolean> onClick, Supplier<UIStyles.BackgroundStyle> style) {
        this(offsetX, offsetY, width, height, onClick, style, style, style);
    }

    public UIBackgroundButton(int offsetX, int offsetY, int width, int height, Supplier<Boolean> onClick,
                              Supplier<UIStyles.BackgroundStyle> style, Supplier<UIStyles.BackgroundStyle> hoveredStyle) {

        this(offsetX, offsetY, width, height, onClick, style, hoveredStyle, style);
    }

    @Override
    protected void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        UIStyles.BackgroundStyle currentStyle;
        if(disabledWhen.get()) currentStyle = disabledStyle.get();
        else currentStyle = isHovered() ? hoveredStyle.get() : style.get();

        UIBackgroundRenderer.render(gui, getRawX(), getRawY(), getRawWidth(), getRawHeight(), currentStyle);
    }
}
