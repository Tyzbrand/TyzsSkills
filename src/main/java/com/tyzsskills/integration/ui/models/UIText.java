package com.tyzsskills.integration.ui.models;

import com.tyzsskills.api.Enums;
import com.tyzsskills.integration.ui.UIElement;
import com.tyzsskills.integration.ui.records.UIStyles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.MutableComponent;

import java.util.function.Supplier;

public class UIText extends UIElement {

    private final Font FONT = Minecraft.getInstance().font;
    private final Supplier<MutableComponent> textExtractor;

    private Supplier<UIStyles.BackgroundStyle> backgroundStyle;
    private Supplier<Boolean> showBackgroundBorder = () -> false;

    private boolean shadow;

    private Enums.TextAlignment alignment = Enums.TextAlignment.LEFT;
    private int wrapWidth = -1;

    //fluent
    public UIText withWrapWidth(int wrapWidth){this.wrapWidth = wrapWidth; return this;}
    public UIText withAlignment(Enums.TextAlignment alignment){this.alignment = alignment; return this;}
    public UIText withBackground(Supplier<UIStyles.BackgroundStyle> backgroundStyle, Supplier<Boolean> showBackgroundBorder) {
        this.backgroundStyle = backgroundStyle; this.showBackgroundBorder = showBackgroundBorder; return this;}

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

        renderBackground(gui, drawX, drawY, textWidth, textHeight);

        if (wrapWidth > 0) gui.drawWordWrap(FONT, finalText, drawX, drawY, wrapWidth, 0xFFFFFFFF);
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

        if (wrapWidth > 0) return scalePivot == Enums.ScalePivot.CENTER ? rawX + (rawW / 2f) : super.getRawX();

        return switch (alignment) {
            case RIGHT -> getTextRawX() + getTextRawWidth();
            case CENTER -> rawX + (rawW / 2f);
            case LEFT -> scalePivot == Enums.ScalePivot.CENTER ? rawX + (rawW / 2f) : super.getRawX();
        };
    }
    @Override
    protected float getPivotY() {return scalePivot == Enums.ScalePivot.CENTER ? getRawY() + (getRawHeight() / 2f) : getRawY();}


    //IMPLEMENTATION
    private void renderBackground(GuiGraphics gui, int textX, int textY, int textW, int textH){
        if (backgroundStyle == null) return;
        var currentStyle = backgroundStyle.get();
        if (currentStyle == null) return;

        var bgX = textX - currentStyle.paddingX();
        var bgY = textY - currentStyle.paddingY();
        var bgW = textW + (currentStyle.paddingX() * 2);
        var bgH = textH + (currentStyle.paddingY() * 2);

        UIBackground.renderBackground(gui, bgX, bgY, bgW, bgH, currentStyle, showBackgroundBorder.get());
    }

    //UTILS
    private int getTextRawWidth() {
        var finalText = textExtractor.get();
        if (finalText == null) return 0;

        var emptyLeftPixels = shadow ? 0 : 1;

        if (wrapWidth > 0) {
            var lines = FONT.split(finalText, wrapWidth);
            var maxLineW = 0;
            for (var line : lines) maxLineW = Math.max(maxLineW, FONT.width(line));
            return maxLineW - emptyLeftPixels;
        }
        return FONT.width(finalText) - emptyLeftPixels;
    }
    private int getTextRawHeight() {
        var finalText = textExtractor.get();
        if (finalText == null) return 0;

        var emptyBottomPixels = shadow ? 1 : 2;

        if (wrapWidth > 0) {
            var lines = FONT.split(finalText, wrapWidth);
            return Math.max(FONT.lineHeight, lines.size() * FONT.lineHeight) - emptyBottomPixels;
        }
        return FONT.lineHeight - emptyBottomPixels;
    }
    private int getTextRawX() {
        var baseX = super.getRawX();
        if (wrapWidth > 0) return baseX;

        var boxWidth = super.getRawWidth();
        var textW = getTextRawWidth();
        return switch (alignment) {
            case RIGHT -> baseX + boxWidth - textW;
            case CENTER -> baseX + (boxWidth - textW) / 2;
            case LEFT -> baseX;
        };
    }
    private int getPaddingX() {return backgroundStyle != null && backgroundStyle.get() != null ? backgroundStyle.get().paddingX() : 0;}
    private int getPaddingY() {return backgroundStyle != null && backgroundStyle.get() != null ? backgroundStyle.get().paddingY() : 0;}

}
