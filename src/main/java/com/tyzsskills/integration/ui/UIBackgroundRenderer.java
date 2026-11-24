package com.tyzsskills.integration.ui;

import com.tyzsskills.integration.ui.records.UIStyles;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;

public class UIBackgroundRenderer {
    public static void render(GuiGraphics gui, int x, int y, int w, int h, UIStyles.BackgroundStyle style) {
        if(style == null || style.isNone() || w <= 0 || h <= 0) return;
        var bgStart = style.backgroundStartColor();
        var bgEnd = style.backgroundEndColor();
        var border = style.border();
        if(border.isNone()) {
            switch (style.type()) {
                case FULL -> drawFullBox(gui, x, y, w, h, bgStart, bgEnd);
                case CUT -> drawCutBox(gui, x, y, w, h, 1f, bgStart, bgEnd);
                case NONE -> {}
            }
            return;
        }
        var bdStart = border.borderStartColor();
        var bdEnd = border.borderEndColor();
        var t = border.thickness();
        switch (border.type()) {
            case INNER -> {
                switch (style.type()) {
                    case FULL -> drawFullBox(gui, x, y, w, h, bgStart, bgEnd);
                    case CUT -> drawCutBox(gui, x, y, w, h, t, bgStart, bgEnd);
                    case NONE -> {}
                }
                drawFullBorder(gui, x + t, y + t, w - t * 2f, h - t * 2f, t, bdStart, bdEnd);
            }
            case OUTER -> {
                drawFullBox(gui, x + t, y + t, w - t * 2f, h - t * 2f, bgStart, bgEnd);
                switch (style.type()) {
                    case FULL -> drawFullBorder(gui, x, y, w, h, t, bdStart, bdEnd);
                    case CUT -> drawCutBorder(gui, x, y, w, h, t, bdStart, bdEnd);
                    case NONE -> {}
                }
            }
            case NONE -> {}
        }
    }
    private static void drawFullBox(GuiGraphics gui, float x, float y, float w, float h, int startColor, int endColor) {
        fillRect(gui, x, y, w, h, startColor, endColor);
    }
    private static void drawCutBox(GuiGraphics gui, float x, float y, float w, float h, float cut, int startColor, int endColor) {
        if(w <= 0f || h <= 0f) return;
        if(w < cut * 3f || h < cut * 3f) {
            drawFullBox(gui, x, y, w, h, startColor, endColor);
            return;
        }
        fillRect(gui, x + cut, y, w - cut * 2f, cut, startColor, startColor);
        fillRect(gui, x, y + cut, w, h - cut * 2f, startColor, endColor);
        fillRect(gui, x + cut, y + h - cut, w - cut * 2f, cut, endColor, endColor);
    }
    private static void drawFullBorder(GuiGraphics gui, float x, float y, float w, float h, float t, int startColor, int endColor) {
        if(w <= 0f || h <= 0f || t <= 0f) return;
        if(w <= t * 2f || h <= t * 2f) {
            drawFullBox(gui, x, y, w, h, startColor, endColor);
            return;
        }
        fillRect(gui, x, y, w, t, startColor, startColor);
        fillRect(gui, x, y + h - t, w, t, endColor, endColor);
        fillRect(gui, x, y + t, t, h - t * 2f, startColor, endColor);
        fillRect(gui, x + w - t, y + t, t, h - t * 2f, startColor, endColor);
    }
    private static void drawCutBorder(GuiGraphics gui, float x, float y, float w, float h, float t, int startColor, int endColor) {
        if(w < t * 2f || h < t * 2f || t <= 0f) return;
        if(w > t * 2f) {
            fillRect(gui, x + t, y, w - t * 2f, t, startColor, startColor);
            fillRect(gui, x + t, y + h - t, w - t * 2f, t, endColor, endColor);
        }
        if(h > t * 2f) {
            fillRect(gui, x, y + t, t, h - t * 2f, startColor, endColor);
            fillRect(gui, x + w - t, y + t, t, h - t * 2f, startColor, endColor);
        }
    }
    private static void fillRect(GuiGraphics gui, float x, float y, float w, float h, int startColor, int endColor) {
        if(w <= 0f || h <= 0f) return;
        var matrix = gui.pose().last().pose();
        var buffer = gui.bufferSource().getBuffer(RenderType.gui());
        buffer.addVertex(matrix, x, y, 0f).setColor(startColor);
        buffer.addVertex(matrix, x, y + h, 0f).setColor(endColor);
        buffer.addVertex(matrix, x + w, y + h, 0f).setColor(endColor);
        buffer.addVertex(matrix, x + w, y, 0f).setColor(startColor);
    }
}
