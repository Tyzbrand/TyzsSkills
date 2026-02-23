package com.tyzsskills.impl.client.screen;

import com.tyzsskills.Config;
import com.tyzsskills.impl.client.ClientCache;
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

    private static final long fadeIn = 300L;
    private static final long fadeOut = 500L;

    private static final float SCALE = 0.5f;
    private static final int MARGIN = 4;

    public static void AddXp(float amount, boolean triggersOverlay){
        if(!Config.SHOW_XP_OVERLAY.get()) return;
        if(!triggersOverlay) return;

        var mc = Minecraft.getInstance();
        if(mc.player == null) return;
        if(mc.player.isCreative() && !Config.SHOW_OVERLAYS_IN_CREATIVE.get()) return;

        long now = System.currentTimeMillis();
        long duration = (long)(Config.XP_DURATION.get() * 1000);

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
        long duration = (long)(Config.XP_DURATION.get() * 1000);

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

        int cfgBg = ClientCache.ParseColor(Config.XP_BG_COLOR.get(), 0xAA000000);
        int cfgBorder = ClientCache.ParseColor(Config.XP_BD_COLOR.get(), 0xFFFFFFFF);
        int cfgText = ClientCache.ParseColor(Config.XP_TEXT_COLOR.get(), 0xFFFFFFFF);

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

        x += (int)(Config.XP_OFFSET_X.get() / SCALE);
        y -= (int)(Config.XP_OFFSET_Y.get() / SCALE);

        y += (int)yOffset;

        int bgBaseAlpha = (cfgBg >> 24) & 0xFF;
        int bgFinalAlpha = (int)(bgBaseAlpha * alpha);
        int bgFinal = (bgFinalAlpha << 24) | (cfgBg & 0x00FFFFFF);

        int borderBaseAlpha = (cfgBorder >> 24) & 0xFF;
        int borderFinalAlpha = (int)(borderBaseAlpha * alpha);
        int borderFinal = (borderFinalAlpha << 24) | (cfgBorder & 0x00FFFFFF);

        int textBaseAlpha = (cfgText >> 24) & 0xFF;
        int textFinalAlpha = (int)(textBaseAlpha * alpha);
        int textFinal = (textFinalAlpha << 24) | (cfgText & 0x00FFFFFF);


        renderTooltipStyleRect(gui, x, y, width, height, bgFinal);
        renderTooltipStyleBorder(gui, x, y, width, height, borderFinal);

        gui.drawString(font, text, x + padding, y + padding + 2, textFinal, true);

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