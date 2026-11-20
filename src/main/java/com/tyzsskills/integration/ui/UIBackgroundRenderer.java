package com.tyzsskills.integration.ui;

import com.tyzsskills.integration.ui.records.UIStyles;
import net.minecraft.client.gui.GuiGraphics;

public class UIBackgroundRenderer {
    public static void render(GuiGraphics gui, int x, int y, int w, int h, UIStyles.BackgroundStyle style) {
        if(style == null || style.isNone() || w <= 0 || h <= 0) return;

        var bgStart = style.backgroundStartColor();
        var bgEnd = style.backgroundEndColor();
        var border = style.border();

        if(border.isNone()) {
            switch (style.type()) {
                case FULL -> drawFullBox(gui, x, y, w, h, bgStart, bgEnd);
                case CUT -> drawCutBox(gui, x, y, w, h, bgStart, bgEnd);
                case NONE -> {}
            }
            return;
        }

        var bdStart = border.borderStartColor();
        var bdEnd = border.borderEndColor();

        switch (border.type()) {
            case INNER -> {
                switch (style.type()) {
                    case FULL -> drawFullBox(gui, x, y, w, h, bgStart, bgEnd);
                    case CUT -> drawCutBox(gui, x, y, w, h, bgStart, bgEnd);
                    case NONE -> {}
                }
                drawFullBorder(gui, x + 1, y + 1, w - 2, h - 2, bdStart, bdEnd);
            }

            case OUTER -> {
                drawFullBox(gui, x + 1, y + 1, w - 2, h - 2, bgStart, bgEnd);
                switch (style.type()) {
                    case FULL -> drawFullBorder(gui, x, y, w, h, bdStart, bdEnd);
                    case CUT -> drawCutBorder(gui, x, y, w, h, bdStart, bdEnd);
                    case NONE -> {}
                }
            }
            case NONE -> {}
        }
    }



    private static void drawFullBox(GuiGraphics gui, int x, int y, int w, int h, int color) {
        drawFullBox(gui, x, y, w, h, color, color);
    }
    private static void drawFullBox(GuiGraphics gui, int x, int y, int w, int h, int startColor, int endColor) {
        if(w <= 0 || h <= 0) return;
        if(startColor == endColor) gui.fill(x, y, x + w, y + h, startColor);
        else gui.fillGradient(x, y, x + w, y + h, startColor, endColor);
    }
    private static void drawCutBox(GuiGraphics gui, int x, int y, int w, int h, int color) {
        drawCutBox(gui, x, y, w, h, color, color);
    }
    private static void drawCutBox(GuiGraphics gui, int x, int y, int w, int h, int startColor, int endColor) {
        if(w <= 0 || h <= 0) return;
        if(w < 3 || h < 3) {
            drawFullBox(gui, x, y, w, h, startColor, endColor);
            return;
        }

        gui.fill(x + 1, y, x + w - 1, y + 1, startColor);
        gui.fillGradient(x, y + 1, x + w, y + h - 1, startColor, endColor);
        gui.fill(x + 1, y + h - 1, x + w - 1, y + h, endColor);
    }


    private static void drawFullBorder(GuiGraphics gui, int x, int y, int w, int h, int color) {
        drawFullBorder(gui, x, y, w, h, color, color);
    }
    private static void drawFullBorder(GuiGraphics gui, int x, int y, int w, int h, int startColor, int endColor) {
        if(w <= 0 || h <= 0) return;
        if(w == 1 || h == 1) {
            drawFullBox(gui, x, y, w, h, startColor, endColor);
            return;
        }

        gui.fill(x, y, x + w, y + 1, startColor);
        gui.fill(x, y + h - 1, x + w, y + h, endColor);

        if(h > 2) {
            gui.fillGradient(x, y + 1, x + 1, y + h - 1, startColor, endColor);
            gui.fillGradient(x + w - 1, y + 1, x + w, y + h - 1, startColor, endColor);
        }
    }
    private static void drawCutBorder(GuiGraphics gui, int x, int y, int w, int h, int color) {
        drawCutBorder(gui, x, y, w, h, color, color);
    }
    private static void drawCutBorder(GuiGraphics gui, int x, int y, int w, int h, int startColor, int endColor) {
        if(w < 2 || h < 2) return;

        if(w > 2) {
            gui.fill(x + 1, y, x + w - 1, y + 1, startColor);
            gui.fill(x + 1, y + h - 1, x + w - 1, y + h, endColor);
        }

        if(h > 2) {
            gui.fillGradient(x, y + 1, x + 1, y + h - 1, startColor, endColor);
            gui.fillGradient(x + w - 1, y + 1, x + w, y + h - 1, startColor, endColor);
        }
    }
}
