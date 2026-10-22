package com.tyzsskills.integration.ui.models;

import com.tyzsskills.impl.client.Styles;
import com.tyzsskills.integration.ui.UIElement;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class UIEditBox extends UIElement {
    private final Consumer<String> onValueChange;
    private final TextFieldHelper textHelper;

    private String value = "";
    private MutableComponent placeholder;
    private int maxLength = 32;
    private boolean shadow = true;
    private boolean isFocused = false;

    private long lastActionTime = 0L;
    private int displayOffset = 0;

    //FLuents
    public UIEditBox withPlaceholder(MutableComponent placeholder){this.placeholder = placeholder; return this;}
    public UIEditBox withShadow(boolean shadow){this.shadow = shadow; return this;}


    public UIEditBox(int offsetX, int offsetY, int width, int height, Consumer<String> onValueChange, Supplier<String> initialValue) {
        super(offsetX, offsetY, width, height);

        this.onValueChange = onValueChange;
        value = initialValue.get();

        textHelper = new TextFieldHelper(
                () -> this.value,
                this::setValue,
                TextFieldHelper.createClipboardGetter(Minecraft.getInstance()),
                TextFieldHelper.createClipboardSetter(Minecraft.getInstance()),
                text -> text.length() <= this.maxLength
        );
    }

    public UIEditBox(int offsetX, int offsetY, int width, int height, Consumer<String> onValueChange) {
        this(offsetX, offsetY, width, height, onValueChange, () -> "");
    }

    @Override
    protected void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        var drawX = getRawX();
        var drawY = getRawY() + (getRawHeight() - Styles.FONT.lineHeight) / 2 + 1;

        if (value.isEmpty() && !isFocused) {
            if (placeholder != null) gui.drawString(Styles.FONT, placeholder, drawX, drawY, 0xFFFFFFFF, shadow);
            return;
        }

        var availableWidth = Math.max(1, getRawWidth() - Styles.FONT.width("|") - 2);
        updateDisplayOffset(availableWidth);
        var visibleText = Styles.FONT.plainSubstrByWidth(value.substring(displayOffset), availableWidth);

        gui.drawString(Styles.FONT, visibleText, drawX, drawY, 0xFFFFFFFF, shadow);

        if (!isFocused) return;

        var visibleStart = displayOffset;
        var visibleEnd = displayOffset + visibleText.length();
        var cursorPos = Mth.clamp(textHelper.getCursorPos(), 0, value.length());
        var selectionPos = Mth.clamp(textHelper.getSelectionPos(), 0, value.length());


        if (cursorPos != selectionPos) {
            var start = Mth.clamp(Math.min(cursorPos, selectionPos), visibleStart, visibleEnd);
            var end = Mth.clamp(Math.max(cursorPos, selectionPos), visibleStart, visibleEnd);
            if (start != end) {
                var selX1 = drawX + Styles.FONT.width(value.substring(visibleStart, start));
                var selX2 = drawX + Styles.FONT.width(value.substring(visibleStart, end));
                gui.fill(selX1, drawY - 1, selX2, drawY + Styles.FONT.lineHeight, 0x803366FF);
            }
        }

        var showCursor = ((Util.getMillis() - lastActionTime) / 500L) % 2L == 0L;
        if (showCursor && cursorPos >= visibleStart && cursorPos <= visibleEnd) {
            var cursorX = drawX + Styles.FONT.width(value.substring(visibleStart, cursorPos));
            gui.drawString(Styles.FONT, "|", cursorX, drawY, 0xFFD0D0D0, shadow);
        }
    }

    @Override
    protected boolean onClick(int mouseX, int mouseY) {
        this.isFocused = isHovering(mouseX, mouseY);
        if (this.isFocused) {
            this.lastActionTime = Util.getMillis();

            var relativeX = Math.max(0, mouseX - getRawX());
            var visiblePart = value.substring(Mth.clamp(displayOffset, 0, value.length()));
            var textBeforeClick = Styles.FONT.plainSubstrByWidth(visiblePart, relativeX);
            var clickedIndex = displayOffset + textBeforeClick.length();

            if (clickedIndex < value.length()) {
                var distToChar = relativeX - Styles.FONT.width(textBeforeClick);
                var charWidth = Styles.FONT.width(String.valueOf(value.charAt(clickedIndex)));
                if (distToChar > charWidth / 2) clickedIndex++;
            }
            this.textHelper.setCursorPos(clickedIndex, Screen.hasShiftDown());
            return true;
        }
        return false;
    }

    @Override
    protected boolean onCharTyped(char codePoint, int modifiers) {
        if (!isFocused) return false;

        this.lastActionTime = Util.getMillis();
        return textHelper.charTyped(codePoint);
    }

    @Override
    protected boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (!isFocused) return false;

        if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            isFocused = false;
            return true;
        }

        this.lastActionTime = Util.getMillis();
        textHelper.keyPressed(keyCode);
        return true;
    }

    @Override
    public UITooltip getHoveredTooltip(int mouseX, int mouseY) {
        if(isFocused) return null;
        return super.getHoveredTooltip(mouseX, mouseY);
    }

    //UTILS
    private void updateDisplayOffset(int availableWidth) {
        var cursorPos = Mth.clamp(textHelper.getCursorPos(), 0, value.length());

        if (displayOffset > value.length()) displayOffset = value.length();
        if (cursorPos < displayOffset) displayOffset = cursorPos;

        while (displayOffset < cursorPos && Styles.FONT.width(value.substring(displayOffset, cursorPos)) > availableWidth) {
            displayOffset++;
        }

        while (displayOffset > 0 && Styles.FONT.width(value.substring(displayOffset - 1)) <= availableWidth) {
            displayOffset--;
        }
    }

    public void setValue(String newValue) {
        this.value = newValue;
        this.lastActionTime = Util.getMillis();
        if (onValueChange != null) onValueChange.accept(this.value);
    }

    //GETTERS
    public String getValue() { return value; }
    public boolean isFocused() { return isFocused; }
}
