package com.tyzsskills.integration.ui.models;

import com.tyzsskills.integration.ui.UIElement;
import com.tyzsskills.integration.ui.records.UIStyles.*;
import com.tyzsskills.integration.ui.records.UITexture;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class UIImage extends UIElement {
    private Supplier<UITexture> texture = () -> null;

    private final boolean isUniqueTexture;
    private Supplier<ImageStyle> style;


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
        var currentTexture = texture.get();
        if(isUniqueTexture && currentTexture != null)
            gui.blit(currentTexture.texture(), getRawX(), getRawY(), 0, 0, getRawWidth(), getRawHeight(), currentTexture.size(), currentTexture.size());
        else {
            var currentStyle = style.get();
            gui.blit(currentStyle.image().texture(), getRawX(), getRawY(), currentStyle.u(), currentStyle.v(),
                    currentStyle.width(), currentStyle.height(), currentStyle.image().size(), currentStyle.image().size());
        }
    }
}
