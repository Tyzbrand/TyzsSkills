package com.tyzsskills.integration.ui.interfaces;

import com.tyzsskills.integration.ui.abstracts.UIElement;
import com.tyzsskills.integration.ui.renderers.UIShadowRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public interface IShadowRenderable<T extends UIElement> {
    record ShadowSource(ResourceLocation texture, int u, int v, int w, int h, int texW, int texH) {}

    T withShadow(Supplier<Float> sizeSupplier);
    @Nullable ShadowSource getShadowSource();
    default T withShadow(float size) { return withShadow(() -> size); }


    default void renderShadow(GuiGraphics gui, int x, int y, float shadowSize, float alpha) {
        var src = getShadowSource();
        if(src == null) return;

        UIShadowRenderer.render(gui, src.texture(), x, y, src.u(), src.v(), src.w(), src.h(), src.texW(), src.texH(), shadowSize, alpha);
    }
}
