package com.tyzsskills.integration.ui.records;

public class UIStyles{

    public record ButtonStyle(int width, int height, int u, int v, int uHover, int vHover, int uDisabled, int vDisabled, UITexture image) {

        public ButtonStyle(int width, int height, int u, int v, UITexture texture) {
            this(width, height, u, v, u, v, u, v, texture);
        }

        public ButtonStyle(int width, int height, int u, int v, int uHover, int vHover, UITexture texture){
            this(width, height, u, v, uHover, vHover, u, v, texture);
        }
    }
    public record ImageStyle(int width, int height, int u, int v, UITexture image){}
    public record ScrollBarStyle(int width, int height, int u, int v, int uHover, int vHover, UITexture image){}
    public record BackgroundStyle(int backgroundColor, int borderColor, int paddingX, int paddingY){}
}


