package com.tyzsskills.integration.ui.renderers;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tyzsskills.Settings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class UIShadowRenderer {
    private static final int SUPERSAMPLE = 2;
    private static final float SHADOW_STRENGTH = .9f; // Valeur elevée = Ombre + présente/sombre (ombre plus marquée).
    private static final float SIGMA_FACTOR = .5f;    // Valeur elevée = Flou + large/diffus (ombre plus éloignée).

    private record ShadowKey(ResourceLocation texture, int u, int v, int w, int h, int texW, int texH, int sizeTenths) {}
    private record CachedShadow(ResourceLocation location, int texW, int texH, int padGui) {
        static final CachedShadow EMPTY = new CachedShadow(null, 0, 0, 0);
    }

    private static final Map<ShadowKey, CachedShadow> CACHE = new HashMap<>();

    public static void render(GuiGraphics gui, ResourceLocation texture, int x, int y, int u, int v, int w, int h, int texW, int texH, float shadowSize, float elementAlpha) {
        if(texture == null || shadowSize <= 0f || elementAlpha <= .05f || !Settings.Client.RENDER_SHADOWS.get()) return;
        if(w <= 0 || h <= 0 || texW <= 0 || texH <= 0) return;

        var key = new ShadowKey(texture, u, v, w, h, texW, texH, Math.round(shadowSize * 10f));

        var cached = CACHE.computeIfAbsent(key, k -> generateShadow(k, shadowSize));
        if(cached.location() == null) return;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        gui.setColor(1f, 1f, 1f, elementAlpha);
        gui.blit(cached.location(),
                x - cached.padGui(), y - cached.padGui(),
                w + cached.padGui() * 2, h + cached.padGui() * 2,
                0f, 0f,
                cached.texW(), cached.texH(),
                cached.texW(), cached.texH());

        gui.setColor(1f, 1f, 1f, 1f);
    }

    public static void clearCache() {
        var texManager = Minecraft.getInstance().getTextureManager();
        for(var entry : CACHE.values()) {
            if(entry.location() != null) texManager.release(entry.location());
        }
        CACHE.clear();
    }


    private static CachedShadow generateShadow(ShadowKey key, float shadowSize) {
        var resource = Minecraft.getInstance().getResourceManager().getResource(key.texture());

        if(resource.isEmpty()) return CachedShadow.EMPTY;

        try (var stream = resource.get().open();
             var srcImg = NativeImage.read(stream)) {

            var sigma = shadowSize * SUPERSAMPLE * SIGMA_FACTOR;
            var kernelRadius = Math.max(1, (int) Math.ceil(sigma * 3f));

            var padGui = (int) Math.ceil((float) kernelRadius / SUPERSAMPLE);
            var padPx = padGui * SUPERSAMPLE;

            var innerW = key.w() * SUPERSAMPLE;
            var innerH = key.h() * SUPERSAMPLE;

            var outW = innerW + padPx * 2;
            var outH = innerH + padPx * 2;

            var alphaGrid = new float[outW * outH];
            var scaleX = (float) srcImg.getWidth() / key.texW();
            var scaleY = (float) srcImg.getHeight() / key.texH();

            for(int py = 0; py < innerH; py++) {
                var srcY = Math.clamp((int) ((key.v() + (py + 0.5f) / SUPERSAMPLE) * scaleY), 0, srcImg.getHeight() - 1);
                var dstRow = (py + padPx) * outW + padPx;

                for(int px = 0; px < innerW; px++) {
                    var srcX = Math.clamp((int) ((key.u() + (px + 0.5f) / SUPERSAMPLE) * scaleX), 0, srcImg.getWidth() - 1);
                    var a = (srcImg.getPixelRGBA(srcX, srcY) >>> 24) & 0xFF;

                    alphaGrid[dstRow + px] = a / 255f;
                }
            }

            var kernel = buildGaussianKernel(kernelRadius, sigma);
            var temp = blurHorizontal(alphaGrid, outW, outH, kernel, kernelRadius);
            var blurred = blurVertical(temp, outW, outH, kernel, kernelRadius);
            var shadowImg = new NativeImage(outW, outH, true);

            for(int y = 0; y < outH; y++) {
                var row = y * outW;
                for(int x = 0; x < outW; x++) {
                    var a = Math.clamp(Math.round(blurred[row + x] * SHADOW_STRENGTH * 255f), 0, 255);
                    shadowImg.setPixelRGBA(x, y, a << 24);
                }
            }

            var dynTex = new DynamicTexture(shadowImg);
            dynTex.setFilter(true, false);

            var rl = Minecraft.getInstance().getTextureManager().register("tyzs_ui_shadow", dynTex);
            return new CachedShadow(rl, outW, outH, padGui);
        }
        catch (Exception e) {return CachedShadow.EMPTY;}

    }
    private static float[] buildGaussianKernel(int radius, float sigma) {
        var kernel = new float[radius * 2 + 1];

        var sum = 0f;

        var twoSigmaSq = 2f * sigma * sigma;
        for(int i = -radius; i <= radius; i++) {
            var v = (float)Math.exp(-(i * i) / twoSigmaSq);
            kernel[i + radius] = v;
            sum += v;
        }

        for(int i = 0; i < kernel.length; i++) kernel[i] /= sum;
        return kernel;
    }

    private static float[] blurHorizontal(float[] src, int w, int h, float[] kernel, int radius) {
        var dst = new float[w * h];
        for(int y = 0; y < h; y++) {
            var row = y * w;

            for(int x = 0; x < w; x++) {
                var acc = 0f;

                for(int k = -radius; k <= radius; k++) {
                    var sx = x + k;
                    if (sx >= 0 && sx < w) acc += src[row + sx] * kernel[k + radius];
                }
                dst[row + x] = acc;
            }
        }
        return dst;
    }

    private static float[] blurVertical(float[] src, int w, int h, float[] kernel, int radius) {
        var dst = new float[w * h];
        for(int y = 0; y < h; y++) {
            var row = y * w;

            for(int x = 0; x < w; x++) {
                var acc = 0f;

                for(int k = -radius; k <= radius; k++) {
                    var sy = y + k;
                    if (sy >= 0 && sy < h) acc += src[sy * w + x] * kernel[k + radius];
                }
                dst[row + x] = acc;
            }
        }
        return dst;
    }
}
