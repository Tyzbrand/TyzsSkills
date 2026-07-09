package com.tyzsskills.impl.client.ui;

import net.minecraft.resources.ResourceLocation;

public class UIStyles{

    public record ButtonStyle(int width, int height, int u, int v, int uHover, int vHover, ResourceLocation texture,
                          int textureSize) {
        public ButtonStyle(int width, int height, int u, int v, ResourceLocation texture, int textureSize) {
        this(width, height, u, v, u, v, texture, textureSize);
        }
    }

    public record ImageStyle(int width, int height, int u, int v, ResourceLocation texture, int textureSize){}
}
