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
    public record BackgroundStyle(int backgroundStartColor, int backgroundEndColor, int borderStartColor, int borderEndColor, int paddingX, int paddingY){

        public BackgroundStyle(int backgroundColor, int borderColor, int paddingX, int paddingY){
            this(backgroundColor, backgroundColor, borderColor, borderColor, paddingX, paddingY);
        }
    }
    public record ToggleStyle(int width, int height, int uFalse, int vFalse, int uTrue, int vTrue, int uFalseHover, int vFalseHover, int uTrueHover, int vTrueHover, UITexture image){
        public ToggleStyle(int width, int height, int uFalse, int vFalse, int uTrue, int vTrue, UITexture image){
            this(width, height, uFalse, vFalse, uTrue, vTrue, uFalse, vFalse, uTrue, vTrue, image);
        }
    }
}


