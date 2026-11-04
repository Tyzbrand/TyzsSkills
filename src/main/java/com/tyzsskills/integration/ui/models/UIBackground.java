package com.tyzsskills.integration.ui.models;

import com.tyzsskills.integration.ui.UIElement;
import com.tyzsskills.integration.ui.records.UIStyles;
import net.minecraft.client.gui.GuiGraphics;

import java.util.function.Supplier;

public class UIBackground extends UIElement {
    private Supplier<Boolean> drawBorder = () -> true;
    private Supplier<Boolean> insetBorder = () -> false;
    private final Supplier<UIStyles.BackgroundStyle> style;

    //Fluent
    public UIBackground withBorder(Supplier<Boolean> drawBorder, Supplier<Boolean> insetBorder){
        this.drawBorder = drawBorder; this.insetBorder = insetBorder;
        return this;
    }

    public UIBackground(int offsetX, int offsetY, int width, int height, Supplier<UIStyles.BackgroundStyle> style) {
        super(offsetX, offsetY, width, height);
        this.style = style;
    }


    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        renderBackground(gui, getRawX(), getRawY(), getRawWidth(), getRawHeight(), style.get(), drawBorder.get());
    }

    //STATIC
    public static void renderBackground(GuiGraphics gui, int x, int y, int w, int h, UIStyles.BackgroundStyle style, boolean hasBorder) {
        renderBackground(gui, x, y, w, h, style, hasBorder, false);
    }


    public static void renderBackground(GuiGraphics gui, int x, int y, int w, int h, UIStyles.BackgroundStyle style, boolean hasBorder, boolean insetBorder) {
        if (!hasBorder) {
            drawCutCornerBox(gui, x, y, w, h, style.backgroundStartColor(), style.backgroundEndColor());
            return;
        }

        if (insetBorder) {
            drawCutCornerBox(gui, x, y, w, h, style.backgroundStartColor(), style.backgroundEndColor());

            gui.fill(x + 1, y + 1, x + w - 1, y + 2, style.borderStartColor());
            gui.fill(x + 1, y + h - 2, x + w - 1, y + h - 1, style.borderEndColor());
            gui.fillGradient(x + 1, y + 2, x + 2, y + h - 2, style.borderStartColor(), style.borderEndColor());
            gui.fillGradient(x + w - 2, y + 2, x + w - 1, y + h - 2, style.borderStartColor(), style.borderEndColor());
        } else {
            gui.fillGradient(x + 1, y + 1, x + w - 1, y + h - 1, style.backgroundStartColor(), style.backgroundEndColor());

            gui.fill(x + 1, y, x + w - 1, y + 1, style.borderStartColor());
            gui.fill(x + 1, y + h - 1, x + w - 1, y + h, style.borderEndColor());
            gui.fillGradient(x, y + 1, x + 1, y + h - 1, style.borderStartColor(), style.borderEndColor());
            gui.fillGradient(x + w - 1, y + 1, x + w, y + h - 1, style.borderStartColor(), style.borderEndColor());
        }
    }

    private static void drawCutCornerBox(GuiGraphics gui, int x, int y, int w, int h, int startColor, int endColor) {
        gui.fill(x + 1, y, x + w - 1, y + 1, startColor);
        gui.fillGradient(x, y + 1, x + w, y + h - 1, startColor, endColor);
        gui.fill(x + 1, y + h - 1, x + w - 1, y + h, endColor);
    }

}
