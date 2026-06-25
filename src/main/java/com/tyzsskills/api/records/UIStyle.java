package com.tyzsskills.api.records;

import net.minecraft.resources.ResourceLocation;

public record UIStyle(int width, int height, int u, int v, int uHover, int vHover, ResourceLocation texture, int textureSize) {
    public UIStyle(int width, int height, int u, int v, ResourceLocation texture, int textureSize){
        this(width, height, u, v, u, v, texture, textureSize);
    }
}
