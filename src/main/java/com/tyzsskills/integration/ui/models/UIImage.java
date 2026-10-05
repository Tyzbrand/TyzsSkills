package com.tyzsskills.integration.ui.models;

import com.tyzsskills.integration.ui.UIElement;
import com.tyzsskills.integration.ui.records.UIStyles.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class UIImage extends UIElement {

    private ResourceLocation texture;
    private int textureSize;

    private final boolean isUniqueTexture;
    private Supplier<ImageStyle> style;


    public UIImage(int offsetX, int offsetY, int width, int height, ResourceLocation texture, int textureSize) {
        super(offsetX, offsetY, width, height);

        this.texture = texture;
        this.textureSize = textureSize;

        isUniqueTexture = true;
    }

    public UIImage(int offsetX, int offsetY, Supplier<ImageStyle> style) {
        super(offsetX, offsetY, style.get().width(), style.get().height());

        this.style = style;

        isUniqueTexture = false;
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        if(isUniqueTexture) gui.blit(texture, x, y, 0, 0, width, height, textureSize, textureSize);
        else {
            var currentStyle = style.get();
            gui.blit(currentStyle.image().texture(), x, y, currentStyle.u(), currentStyle.v(),
                    currentStyle.width(), currentStyle.height(), currentStyle.image().size(), currentStyle.image().size());
        }
    }
}
