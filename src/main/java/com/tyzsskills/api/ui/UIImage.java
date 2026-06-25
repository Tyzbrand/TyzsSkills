package com.tyzsskills.api.ui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class UIImage extends UIElement{

    public ResourceLocation texture;
    public int u, v, textureSize;
    private final boolean isUniqueTexture;


    public UIImage(int x, int y, int width, int height, ResourceLocation texture, int textureSize) {
        super(x, y, width, height);

        this.texture = texture;
        this.textureSize = textureSize;

        isUniqueTexture = true;
    }

    public UIImage(int x, int y, int width, int height, int u, int v, ResourceLocation texture,  int textureSize) {
        super(x, y, width, height);

        this.u = u; this.v = v;
        this.texture = texture;
        this.textureSize = textureSize;

        isUniqueTexture = false;
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        if(isUniqueTexture) gui.blit(texture, x, y, 0, 0, width, height, textureSize, textureSize);
        else gui.blit(texture, x, y, u, v, width, height, textureSize, textureSize);
    }
}
