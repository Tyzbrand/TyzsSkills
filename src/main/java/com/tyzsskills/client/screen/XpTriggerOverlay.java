package com.tyzsskills.client.screen;

import com.tyzsskills.Config;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;

import java.util.Locale;

public class XpTriggerOverlay implements LayeredDraw.Layer {

    private static float accumulatedXp = 0f;
    private static long lastUpdateTime = 0L;

    private static long appearanceTime = 0L;

    private static final long duration = 3000L;
    private static final long fadeIn = 300L;
    private static final long fadeOut = 500L;

    private static final float SCALE = 0.5f;
    private static final int MARGIN = 4;

    private static final int COLOR_BG = 0xAA000000;
    private static final int COLOR_BORDER = 0xFFFFFF;
    private static final int COLOR_TEXT = 0xFFFFFF;
    private static final int COLOR_TEXT_XP = 0xA2E158;

    public static void AddXp(float amount){
        if(!Config.SHOW_XP_OVERLAY.get()) return;

        var mc = Minecraft.getInstance();
        if(mc.player == null) return;
        if(mc.player.isCreative() && !Config.SHOW_OVERLAYS_IN_CREATIVE.get()) return;

        long now = System.currentTimeMillis();

        boolean isActive = (now - lastUpdateTime < duration) && (accumulatedXp > 0);

        if(!isActive) {
            accumulatedXp = amount;
            appearanceTime = now;
        } else {accumulatedXp += amount;}

        lastUpdateTime = now;
    }

    public static void Clear() {
        accumulatedXp = 0f;
        lastUpdateTime = 0L;
        appearanceTime = 0L;
    }

    @Override
    public void render(GuiGraphics gui, DeltaTracker deltaTracker) {
        long now = System.currentTimeMillis();
        long timeSinceUpdate = now - lastUpdateTime;

        if (timeSinceUpdate > duration || accumulatedXp <= 0) return;

        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        long timeSinceAppearance = now - appearanceTime;

        float alpha = 1.0f;
        float yOffset = 0f;

        if (timeSinceAppearance < fadeIn) {
            float progress = (float) timeSinceAppearance / fadeIn;
            progress = (float) Math.sin(progress * Math.PI / 2);

            alpha = progress;
            yOffset = (1.0f - progress) * 20;
        }
        else if (timeSinceUpdate > (duration - fadeOut)) {alpha = (float) (duration - timeSinceUpdate) / fadeOut;}

        alpha = Mth.clamp(alpha, 0f, 1f);
        if (alpha <= 0.05f) return;

        String amountStr = (accumulatedXp == (long) accumulatedXp)
                ? String.format("%d", (long) accumulatedXp) : String.format(Locale.US, "%.1f", accumulatedXp);

        MutableComponent text = Component.literal("+ " + amountStr + " ")
                .append(Component.translatable("gui.tyzs_skills.xp"));

        int textWidth = font.width(text);
        int padding = 6;
        int height = 12 + (padding * 2);
        int width = textWidth + (padding * 2);

        gui.pose().pushPose();
        gui.pose().scale(SCALE, SCALE, 1.0f);

        int x = (int)(MARGIN / SCALE);
        int scaledScreenHeight = (int)(screenHeight / SCALE);
        int y = scaledScreenHeight - height - (int)(MARGIN / SCALE);

        y += (int) yOffset;

        int alphaInt = (int) (alpha * 255);
        int bgWithAlpha = (alphaInt << 24) | (COLOR_BG & 0x00FFFFFF);
        int borderWithAlpha = (alphaInt << 24) | (COLOR_BORDER & 0x00FFFFFF);
        int textWithAlpha = (alphaInt << 24) | (COLOR_TEXT & 0x00FFFFFF);

        renderTooltipStyleRect(gui, x, y, width, height, bgWithAlpha);
        renderTooltipStyleBorder(gui, x, y, width, height, borderWithAlpha);

        gui.drawString(font, text, x + padding, y + padding + 2, textWithAlpha, true);

        gui.pose().popPose();
    }


    private void renderTooltipStyleRect(GuiGraphics gui, int x, int y, int width, int height, int color) {
        gui.fill(x, y + 1, x + width, y + height - 1, color);
        gui.fill(x + 1, y, x + width - 1, y + 1, color);
        gui.fill(x + 1, y + height - 1, x + width - 1, y + height, color);
    }

    private void renderTooltipStyleBorder(GuiGraphics gui, int x, int y, int width, int height, int color) {
        gui.fill(x + 1, y, x + width - 1, y + 1, color);
        gui.fill(x + 1, y + height - 1, x + width - 1, y + height, color);
        gui.fill(x, y + 1, x + 1, y + height - 1, color);
        gui.fill(x + width - 1, y + 1, x + width, y + height - 1, color);
    }
}