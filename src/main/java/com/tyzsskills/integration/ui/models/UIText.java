package com.tyzsskills.integration.ui.models;

import com.tyzsskills.api.Enums;
import com.tyzsskills.integration.ui.abstracts.UIElement;
import com.tyzsskills.integration.ui.records.UIStyles;
import com.tyzsskills.integration.ui.renderers.UIBackgroundRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class UIText extends UIElement {

    private final Font FONT = Minecraft.getInstance().font;
    private final Supplier<MutableComponent> textExtractor;

    private Supplier<UIStyles.BackgroundStyle> backgroundStyle = () -> UIStyles.BackgroundStyle.NONE;

    private boolean shadow;

    private Enums.TextAlignment alignment = Enums.TextAlignment.LEFT;
    private int wrapWidth = -1;

    private Component cachedComponent;
    private Language cachedLanguage;
    private int cachedEffectiveWrap = -1;
    private List<FormattedCharSequence> cachedLines = List.of();
    private int cachedMaxLineWidth = 0;

    //fluent
    public UIText withWrapWidth(int wrapWidth){this.wrapWidth = wrapWidth; return this;}
    public UIText withAlignment(Enums.TextAlignment alignment){this.alignment = alignment; return this;}
    public UIText withBackground(Supplier<UIStyles.BackgroundStyle> backgroundStyle) {
        this.backgroundStyle = backgroundStyle != null ? backgroundStyle : () -> UIStyles.BackgroundStyle.NONE;
        return this;
    }

    public UIText(int offsetX, int offsetY, Supplier<MutableComponent> textExtractor) {
        this(offsetX, offsetY, textExtractor, true);
    }

    public UIText(int offsetX, int offsetY, Supplier<MutableComponent> textExtractor, boolean shadow) {
        super(offsetX, offsetY, 0, 0);
        this.textExtractor = textExtractor;
        this.shadow = shadow;
    }

    //PARENT
    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        var finalText = textExtractor.get();
        if (finalText == null) return;

        var drawX = getTextRawX();
        var drawY = super.getRawY();
        var textWidth = getTextRawWidth();
        var textHeight = getTextRawHeight();

        if (textWidth <= 0 || textHeight <= 0) return;

        renderBackground(gui, drawX, drawY, textWidth, textHeight);

        if (wrapWidth > 0) {
            var lines = getWrappedLines(finalText);
            var emptyRightPixels = shadow ? 0 : 1;
            var currentY = drawY;

            for (var line : lines) {
                var lineW = Math.max(0, FONT.width(line) - emptyRightPixels);
                var lineX = switch (alignment) {
                    case LEFT -> drawX;
                    case CENTER -> drawX + (textWidth - lineW) / 2;
                    case RIGHT -> drawX + (textWidth - lineW);
                };

                gui.drawString(FONT, line, lineX, currentY, 0xFFFFFFFF, shadow);
                currentY += FONT.lineHeight;
            }
        }
        else gui.drawString(FONT, finalText, drawX, drawY, 0xFFFFFFFF, shadow);
    }


    @Override
    public int getRawWidth() {
        int textW = getTextRawWidth();
        return textW > 0 ? textW + (getPaddingX() * 2) : 0;
    }
    @Override
    public int getRawHeight() {
        var textH = getTextRawHeight();
        return textH > 0 ? textH + (getPaddingY() * 2) : 0;
    }
    @Override
    public int getRawX() {
        return getTextRawX() - getPaddingX();
    }
    @Override
    public int getRawY() {
        return super.getRawY() - getPaddingY();
    }
    @Override
    protected float getPivotX() {
        var rawX = getRawX();
        var rawW = getRawWidth();

        return switch (alignment) {
            case RIGHT -> getTextRawX() + getTextRawWidth();
            case CENTER -> rawX + (rawW / 2f);
            case LEFT -> switch (scalePivot) {
                case CENTER -> rawX + (rawW / 2f);
                case TOP_RIGHT, BOTTOM_RIGHT -> rawX + rawW;
                case TOP_LEFT, BOTTOM_LEFT -> super.getRawX();
            };
        };
    }


    //IMPLEMENTATION
    private void renderBackground(GuiGraphics gui, int textX, int textY, int textW, int textH){
        var currentStyle = backgroundStyle.get();
        if(currentStyle == null || currentStyle.isNone()) return;

        var bgX = textX - currentStyle.paddingX();
        var bgY = textY - currentStyle.paddingY();
        var bgW = textW + currentStyle.paddingX() * 2;
        var bgH = textH + currentStyle.paddingY() * 2;

        UIBackgroundRenderer.render(gui, bgX, bgY, bgW, bgH, currentStyle);
    }

    private List<FormattedCharSequence> getWrappedLines(Component text) {
        var effectiveWrap = Math.max(1, shadow ? wrapWidth : wrapWidth + 1);
        var currentLang = Language.getInstance();

        if(currentLang != cachedLanguage || effectiveWrap != cachedEffectiveWrap || !Objects.equals(text, cachedComponent)) {
            cachedComponent = text;
            cachedLanguage = currentLang;
            cachedEffectiveWrap = effectiveWrap;
            cachedLines = FONT.split(text, effectiveWrap);

            var maxW = 0;
            for (var line : cachedLines) maxW = Math.max(maxW, FONT.width(line));

            cachedMaxLineWidth = maxW;
        }
        return cachedLines;
    }

    //UTILS
    private int getTextRawWidth() {
        var finalText = textExtractor.get();
        if(finalText == null) return 0;

        var emptyRightPixels = shadow ? 0 : 1;
        if(wrapWidth > 0) {
            getWrappedLines(finalText);
            return cachedMaxLineWidth > 0 ? cachedMaxLineWidth - emptyRightPixels : 0;
        }

        var w = FONT.width(finalText);
        return w > 0 ? w - emptyRightPixels : 0;
    }
    private int getTextRawHeight() {
        var finalText = textExtractor.get();
        if(finalText == null || getTextRawWidth() <= 0) return 0;

        var emptyBottomPixels = shadow ? 1 : 2;
        if(wrapWidth > 0) {
            var lines = getWrappedLines(finalText);
            return Math.max(FONT.lineHeight, lines.size() * FONT.lineHeight) - emptyBottomPixels;
        }
        return FONT.lineHeight - emptyBottomPixels;
    }
    private int getTextRawX() {
        var baseX = super.getRawX();
        var boxWidth = super.getRawWidth();
        var textW = getTextRawWidth();

        return switch (alignment) {
            case RIGHT -> baseX + boxWidth - textW;
            case CENTER -> baseX + (boxWidth - textW) / 2;
            case LEFT -> baseX;
        };
    }
    private int getPaddingX() {return backgroundStyle.get().paddingX();}
    private int getPaddingY() {return backgroundStyle.get().paddingY();}

}
