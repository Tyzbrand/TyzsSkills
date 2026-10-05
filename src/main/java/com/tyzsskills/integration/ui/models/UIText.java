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

    private Enums.TextAlignment alignment = Enums.TextAlignment.LEFT;
    private int wrapWidth = -1;

    //fluent
    public UIText withWrapWidth(int wrapWidth){this.wrapWidth = wrapWidth; return this;}
    public UIText withAlignment(Enums.TextAlignment alignment){this.alignment = alignment; return this;}
    public UIText withBackground(Supplier<UIStyles.BackgroundStyle> backgroundStyle, Supplier<Boolean> showBackgroundBorder) {
        this.backgroundStyle = backgroundStyle; this.showBackgroundBorder = showBackgroundBorder; return this;}

    public UIText(int offsetX, int offsetY, int width, int height, Supplier<MutableComponent> textExtractor) {
        super(offsetX, offsetY, width, height);
        this.textExtractor = textExtractor;
    }

    //PARENT
    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        var finalText = textExtractor.get();
        if (finalText == null) return;

        var drawX = x;
        int textWidth;
        int textHeight;

        if (wrapWidth > 0) {
            var lines = FONT.split(finalText, wrapWidth);
            var maxLineW = 0;
            for (var line : lines) {
                maxLineW = Math.max(maxLineW, FONT.width(line));
            }
            textWidth = maxLineW;
            textHeight = Math.max(FONT.lineHeight, lines.size() * FONT.lineHeight);
        } else {
            textWidth = FONT.width(finalText);
            textHeight = FONT.lineHeight;

            var localWidth = scale != 0f ? (int) (width / scale) : width;
            switch (alignment) {
                case RIGHT -> drawX = x + localWidth - textWidth;
                case CENTER -> drawX = x + (localWidth - textWidth) / 2;
                case LEFT -> drawX = x;
            }
        }

        renderBackground(gui, drawX, y, textWidth, textHeight);

        if (wrapWidth > 0) gui.drawWordWrap(FONT, finalText, x, y, wrapWidth, 0xFFFFFFFF);
        else gui.drawString(FONT, finalText, drawX, y, 0xFFFFFFFF, false);

    }

    //IMPLEMENTATION
    private void renderBackground(GuiGraphics gui, int textX, int textY, int textW, int textH){
        if(backgroundStyle == null) return;

        var currentStyle = backgroundStyle.get();

        var bgX = textX - currentStyle.paddingX();
        var bgY = textY - currentStyle.paddingY();
        var bgW = textW + (currentStyle.paddingX() * 2);
        var bgH = textH + (currentStyle.paddingY() * 2);

        gui.fill(bgX, bgY + 1, bgX + bgW, bgY + bgH - 1, currentStyle.backgroundColor());
        gui.fill(bgX + 1, bgY, bgX + bgW - 1, bgY + 1, currentStyle.backgroundColor());
        gui.fill(bgX + 1, bgY + bgH - 1, bgX + bgW - 1, bgY + bgH, currentStyle.backgroundColor());

        if (!showBackgroundBorder.get()) return;

        gui.fill(bgX + 1, bgY, bgX + bgW - 1, bgY + 1, currentStyle.borderColor());
        gui.fill(bgX + 1, bgY + bgH - 1, bgX + bgW - 1, bgY + bgH, currentStyle.borderColor());
        gui.fill(bgX, bgY + 1, bgX + 1, bgY + bgH - 1, currentStyle.borderColor());
        gui.fill(bgX + bgW - 1, bgY + 1, bgX + bgW, bgY + bgH - 1, currentStyle.borderColor());

    }
}
