package com.tyzsskills.client.screen;

import com.tyzsskills.Config;
import com.tyzsskills.client.ClientCache;
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

    private static final long fadeIn = 300L;
    private static final long fadeOut = 500L;

    private static final float SCALE = 0.5f;
    private static final int MARGIN = 4;


    public static void ShowLevelUp(int level, int spReward){
        if(!Config.SHOW_LEVEL_OVERLAY.get()) return;

        var mc = Minecraft.getInstance();
        if(mc.player == null) return;
        if(mc.player.isCreative() && !Config.SHOW_OVERLAYS_IN_CREATIVE.get()) return;

        long now = System.currentTimeMillis();
        long duration = (long)(Config.LEVEL_DURATION.get() * 1000);

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
        long duration = (long)(Config.LEVEL_DURATION.get() * 1000);

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

        int cfgBg = ClientCache.ParseColor(Config.LEVEL_BG_COLOR.get(), 0xAA000000);
        int cfgBorder = ClientCache.ParseColor(Config.LEVEL_BD_COLOR.get(), 0xFFD6AD55);
        int cfgText = ClientCache.ParseColor(Config.LEVEL_TEXT_COLOR.get(), 0xFFFFFFFF);
        int spBaseColor = ClientCache.ParseColor(Config.LEVEL_SCD_TEXT_COLOR.get(), 0xFFD6AD55);

        int spBaseAlpha = (spBaseColor >> 24) & 0xFF;
        int spFinalAlpha = (int)(spBaseAlpha * alpha);
        int spFinal = (spFinalAlpha << 24) | (spBaseColor & 0x00FFFFFF);

        String spTextRaw = Component.translatable("gui.tyzs_skills.SP").getString().toUpperCase();

        MutableComponent text = Component.translatable("overlay.tyzs_skills.level")
                .append(Component.literal(" " + String.valueOf(currentLevel)))
                .append(Component.literal(" [+" + accumulatedSp + " " + spTextRaw + "]").withColor(spFinal));

        int textWidth = font.width(text);
        int padding = 6;
        int height = 12 + (padding * 2);
        int width = textWidth + (padding * 2);

        gui.pose().pushPose();
        gui.pose().scale(SCALE, SCALE, 1.0f);


        int scaledScreenHeight = (int)(screenHeight / SCALE);
        int baseX = (int)(MARGIN / SCALE);
        int baseY = scaledScreenHeight - height - (int)(MARGIN / SCALE);

        baseX += (int)(Config.LEVEL_OFFSET_X.get() / SCALE);
        baseY -= (int)(Config.LEVEL_OFFSET_Y.get() / SCALE);

        int boxHeightWithMargin = height + 4;
        int y = baseY - boxHeightWithMargin;

        y += (int)yOffset;

        int bgFinal = ((int)(((cfgBg >> 24) & 0xFF) * alpha) << 24) | (cfgBg & 0x00FFFFFF);
        int borderFinal = ((int)(((cfgBorder >> 24) & 0xFF) * alpha) << 24) | (cfgBorder & 0x00FFFFFF);
        int textFinal = ((int)(((cfgText >> 24) & 0xFF) * alpha) << 24) | (cfgText & 0x00FFFFFF);

        renderTooltipStyleRect(gui, baseX, y, width, height, bgFinal);
        renderTooltipStyleBorder(gui, baseX, y, width, height, borderFinal);

        gui.drawString(font, text, baseX + padding, y + padding + 2, textFinal, true);

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