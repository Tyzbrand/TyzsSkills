package com.tyzsskills.integration.ui.models;

import com.tyzsskills.integration.ui.abstracts.UIElement;
import com.tyzsskills.integration.ui.interfaces.IShadowRenderable;
import com.tyzsskills.integration.ui.records.UIStyles.ImageStyle;
import com.tyzsskills.integration.ui.records.UITexture;
import net.minecraft.client.gui.GuiGraphics;

import java.util.function.Supplier;

public class UIImage extends UIElement implements IShadowRenderable<UIImage> {
    private Supplier<UITexture> texture = () -> null;

    private final boolean isUniqueTexture;
    private Supplier<ImageStyle> style;

    //fluent
    @Override
    public UIImage withShadow(Supplier<Float> sizeSupplier) {
        this.shadowSize = sizeSupplier != null ? sizeSupplier : () -> 0f;
        return this;
    }


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
        var src = getShadowSource();
        if (src == null) return;
        gui.blit(src.texture(), getRawX(), getRawY(), src.u(), src.v(), src.w(), src.h(), src.texW(), src.texH());
    }


    @Override
    public ShadowSource getShadowSource() {
        if(isUniqueTexture) {
            var tex = texture.get();
            return tex != null ? new ShadowSource(tex.texture(), 0, 0, getRawWidth(), getRawHeight(), tex.width(), tex.height()) : null;
        }

        var s = style != null ? style.get() : null;
        return s != null ? new ShadowSource(s.image().texture(), s.u(), s.v(), s.width(), s.height(), s.image().width(), s.image().height()) : null;
    }
}
