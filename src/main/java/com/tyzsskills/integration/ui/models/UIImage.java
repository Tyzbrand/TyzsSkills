package com.tyzsskills.integration.ui.models;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tyzsskills.integration.ui.abstracts.UIElement;
import com.tyzsskills.integration.ui.records.UIStyles.ImageStyle;
import com.tyzsskills.integration.ui.records.UITexture;
import net.minecraft.client.gui.GuiGraphics;

import java.util.function.Supplier;

public class UIImage extends UIElement {
    private Supplier<UITexture> texture = () -> null;

    private final boolean isUniqueTexture;
    private Supplier<ImageStyle> style;

    private float shadowSize = 0f;

    //fluent
    public UIImage withShadow(float size) {this.shadowSize = Math.max(0f, size); return this;}


    public UIImage(int offsetX, int offsetY, int width, int height, Supplier<UITexture> texture) {
        super(offsetX, offsetY, width, height);

        this.texture = texture;

        isUniqueTexture = true;
    }

    public UIImage(int offsetX, int offsetY, Supplier<ImageStyle> style) {
        super(offsetX, offsetY, style.get().width(), style.get().height());

        this.style = style;

        isUniqueTexture = false;
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        var currentTexture = isUniqueTexture ? texture.get() : null;
        var currentStyle = !isUniqueTexture && style != null ? style.get() : null;

        if (isUniqueTexture ? currentTexture == null : currentStyle == null) return;
        if (shadowSize > 0f) {
            drawCenteredShadow(gui, currentTexture, currentStyle);

            var shade = isCurrentShaded() ? .3f : 1f;
            gui.setColor(shade, shade, shade, getCurrentAlpha());
        }
        blitImage(gui, currentTexture, currentStyle);
    }


    //UTILS
    private void blitImage(GuiGraphics gui, UITexture currentTexture, ImageStyle currentStyle) {
        if (isUniqueTexture) {
            gui.blit(currentTexture.texture(), getRawX(), getRawY(), 0, 0,
                    getRawWidth(), getRawHeight(), currentTexture.size(), currentTexture.size());
        } else {
            gui.blit(currentStyle.image().texture(), getRawX(), getRawY(),
                    currentStyle.u(), currentStyle.v(),
                    currentStyle.width(), currentStyle.height(),
                    currentStyle.image().size(), currentStyle.image().size());
        }
    }

    private void drawCenteredShadow(GuiGraphics gui, UITexture currentTexture, ImageStyle currentStyle) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        var baseAlpha = SHADOW_STRENGTH * getCurrentAlpha();

        drawShadowRing(gui, currentTexture, currentStyle, shadowSize * .35f, baseAlpha, DIRS_4_DIAG);
        drawShadowRing(gui, currentTexture, currentStyle, shadowSize * .68f, baseAlpha * .6f, DIRS_8_MAIN);
        drawShadowRing(gui, currentTexture, currentStyle, shadowSize, baseAlpha * .3f, DIRS_8_STAGGERED);
    }
    private void drawShadowRing(GuiGraphics gui, UITexture currentTexture, ImageStyle currentStyle, float radius, float alpha, float[][] dirs) {
        if (alpha <= 0.004f) return;

        gui.setColor(0f, 0f, 0f, alpha);

        for (var dir : dirs) {
            gui.pose().pushPose();
            gui.pose().translate(dir[0] * radius, dir[1] * radius, 0f);

            blitImage(gui, currentTexture, currentStyle);

            gui.pose().popPose();
        }
    }

    //DATA
    private static final float SHADOW_STRENGTH = 0.055f;
    private static final float[][] DIRS_4_DIAG = {{ 0.7071f, 0.7071f }, { -0.7071f, 0.7071f }, { 0.7071f, -0.7071f }, { -0.7071f, -0.7071f}};
    private static final float[][] DIRS_8_MAIN = {
            { 1f, 0f }, { -1f, 0f }, { 0f, 1f }, { 0f, -1f },
            { 0.7071f, 0.7071f }, { -0.7071f, 0.7071f }, { 0.7071f, -0.7071f }, { -0.7071f, -0.7071f }
    };
    private static final float[][] DIRS_8_STAGGERED = {
            { 0.9239f, 0.3827f }, { -0.9239f, 0.3827f }, { 0.9239f, -0.3827f }, { -0.9239f, -0.3827f },
            { 0.3827f, 0.9239f }, { -0.3827f, 0.9239f }, { 0.3827f, -0.9239f }, { -0.3827f, -0.9239f }
    };
}
