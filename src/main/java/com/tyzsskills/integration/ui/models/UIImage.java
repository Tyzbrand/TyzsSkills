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
        var currentTexture = isUniqueTexture ? texture.get() : null;
        var currentStyle = !isUniqueTexture && style != null ? style.get() : null;

        if(isUniqueTexture ? currentTexture == null : currentStyle == null) return;

        blitImage(gui, currentTexture, currentStyle);
    }


    //UTILS
    private void blitImage(GuiGraphics gui, UITexture currentTexture, ImageStyle currentStyle) {
        if (isUniqueTexture) {
            gui.blit(currentTexture.texture(), getRawX(), getRawY(), 0, 0,
                    getRawWidth(), getRawHeight(), currentTexture.width(), currentTexture.height());
        } else {
            gui.blit(currentStyle.image().texture(), getRawX(), getRawY(),
                    currentStyle.u(), currentStyle.v(),
                    currentStyle.width(), currentStyle.height(),
                    currentStyle.image().width(), currentStyle.image().height());
        }
    }
}
