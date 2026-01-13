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

public class LevelTriggerOverlay implements LayeredDraw.Layer {

    private static int currentLevel = 0;
    private static int accumulatedSp = 0;

    private static long lastUpdateTime = 0L;
    private static long appearanceTime = 0L;

    private static final long duration = 4000L;
    private static final long fadeIn = 300L;
    private static final long fadeOut = 500L;

    private static final float SCALE = 0.5f;
    private static final int MARGIN = 4;

    // COULEURS
    private static final int COLOR_BG = 0xAA000000;
    private static final int COLOR_BORDER = 0xFFD6AD55;
    private static final int COLOR_TEXT = 0xFFFFFF;
    private static final int COLOR_TEXT_SP = 0xFFD6AD55;

    public static void ShowLevelUp(int level, int spReward){
        if(!Config.SHOW_LEVEL_OVERLAY.get()) return;

        var mc = Minecraft.getInstance();
        if(mc.player == null) return;
        if(mc.player.isCreative() && !Config.SHOW_OVERLAYS_IN_CREATIVE.get()) return;

        long now = System.currentTimeMillis();

        boolean isActive = (now - lastUpdateTime < duration);

        if(!isActive) {
            currentLevel = level;
            accumulatedSp = spReward;
            appearanceTime = now;
        } else {
            currentLevel = Math.max(currentLevel, level);
            accumulatedSp += spReward;
        }
        lastUpdateTime = now;
    }

    public static void Clear() {
        accumulatedSp = 0;
        currentLevel = 0;
        lastUpdateTime = 0L;
        appearanceTime = 0L;
    }

    @Override
    public void render(GuiGraphics gui, DeltaTracker deltaTracker) {
        long now = System.currentTimeMillis();
        long timeSinceUpdate = now - lastUpdateTime;

        if (timeSinceUpdate > duration || currentLevel <= 0) return;

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
        else if (timeSinceUpdate > (duration - fadeOut)) {
            alpha = (float) (duration - timeSinceUpdate) / fadeOut;
        }

        alpha = Mth.clamp(alpha, 0f, 1f);
        if (alpha <= 0.05f) return;


        String spTextRaw = Component.translatable("gui.tyzs_skills.SP").getString().toUpperCase();

        MutableComponent text = Component.translatable("overlay.tyzs_skills.level")
                .append(Component.literal(String.valueOf(currentLevel)))
                .append(Component.literal(" [+" + accumulatedSp + " " + spTextRaw + "]").withColor(COLOR_TEXT_SP));

        int textWidth = font.width(text);
        int padding = 6;
        int height = 12 + (padding * 2);
        int width = textWidth + (padding * 2);

        gui.pose().pushPose();
        gui.pose().scale(SCALE, SCALE, 1.0f);


        int scaledScreenHeight = (int)(screenHeight / SCALE);
        int baseX = (int)(MARGIN / SCALE);
        int baseY = scaledScreenHeight - height - (int)(MARGIN / SCALE);

        int boxHeightWithMargin = height + 4;
        int y = baseY - boxHeightWithMargin;

        y += (int)yOffset;

        int alphaInt = (int) (alpha * 255);
        int bgWithAlpha = (alphaInt << 24) | (COLOR_BG & 0x00FFFFFF);
        int borderWithAlpha = (alphaInt << 24) | (COLOR_BORDER & 0x00FFFFFF);
        int textWithAlpha = (alphaInt << 24) | (COLOR_TEXT & 0x00FFFFFF);

        renderTooltipStyleRect(gui, baseX, y, width, height, bgWithAlpha);
        renderTooltipStyleBorder(gui, baseX, y, width, height, borderWithAlpha);

        gui.drawString(font, text, baseX + padding, y + padding + 2, textWithAlpha, true);

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