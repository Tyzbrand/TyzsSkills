package com.tyzsskills.api.ui;

import com.tyzsskills.api.ui.UIStyles.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class UIImage extends UIElement{

    public ResourceLocation texture;
    public int textureSize;
    private final boolean isUniqueTexture;

    public ImageStyle style;


    public UIImage(int offsetX, int offsetY, int width, int height, ResourceLocation texture, int textureSize) {
        super(offsetX, offsetY, width, height);

        this.texture = texture;
        this.textureSize = textureSize;

        isUniqueTexture = true;
    }

    public UIImage(int x, int y, ImageStyle style) {
        super(x, y, style.width(), style.height());

        this.style = style;

        isUniqueTexture = false;
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        if(isUniqueTexture) gui.blit(texture, x, y, 0, 0, width, height, textureSize, textureSize);
        else gui.blit(style.texture(), x, y, style.u(), style.v(), style.width(), style.height(), style.textureSize(), style.textureSize());
    }
}
