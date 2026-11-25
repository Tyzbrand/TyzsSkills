package com.tyzsskills.integration.ui.renderers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tyzsskills.Settings;
import net.minecraft.client.gui.GuiGraphics;

public class UIShadowRenderer {
    private static final float SHADOW_STRENGTH = 0.055f;
    private static final float[][] DIRS_4_DIAG = {
            { 0.7071f, 0.7071f }, { -0.7071f, 0.7071f }, { 0.7071f, -0.7071f }, { -0.7071f, -0.7071f }
    };
    private static final float[][] DIRS_8_MAIN = {
            { 1f, 0f }, { -1f, 0f }, { 0f, 1f }, { 0f, -1f },
            { 0.7071f, 0.7071f }, { -0.7071f, 0.7071f }, { 0.7071f, -0.7071f }, { -0.7071f, -0.7071f }
    };
    private static final float[][] DIRS_8_STAGGERED = {
            { 0.9239f, 0.3827f }, { -0.9239f, 0.3827f }, { 0.9239f, -0.3827f }, { -0.9239f, -0.3827f },
            { 0.3827f, 0.9239f }, { -0.3827f, 0.9239f }, { 0.3827f, -0.9239f }, { -0.3827f, -0.9239f }
    };

    public static void render(GuiGraphics gui, float shadowSize, float elementAlpha, Runnable drawSilhouette) {
        if (shadowSize <= 0f || elementAlpha <= 0.05f || !Settings.Client.RENDER_SHADOWS.get()) return;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        var baseAlpha = SHADOW_STRENGTH * elementAlpha;

        drawRing(gui, shadowSize * .35f, baseAlpha, DIRS_4_DIAG, drawSilhouette);
        drawRing(gui, shadowSize * .68f, baseAlpha * .6f, DIRS_8_MAIN, drawSilhouette);
        drawRing(gui, shadowSize, baseAlpha * .3f, DIRS_8_STAGGERED, drawSilhouette);

        gui.setColor(1f, 1f, 1f, 1f);
    }
    private static void drawRing(GuiGraphics gui, float radius, float alpha, float[][] dirs, Runnable drawSilhouette) {
        if (alpha <= 0.004f) return;

        gui.setColor(0f, 0f, 0f, alpha);

        for (var dir : dirs) {
            gui.pose().pushPose();
            gui.pose().translate(dir[0] * radius, dir[1] * radius, 0f);
            drawSilhouette.run();
            gui.pose().popPose();
        }
    }
}
