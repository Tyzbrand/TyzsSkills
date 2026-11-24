package com.tyzsskills.integration.ui.records;

import com.tyzsskills.api.Enums;

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
    public record ToggleStyle(int width, int height, int uFalse, int vFalse, int uTrue, int vTrue, int uFalseHover, int vFalseHover, int uTrueHover, int vTrueHover, UITexture image){
        public ToggleStyle(int width, int height, int uFalse, int vFalse, int uTrue, int vTrue, UITexture image){
            this(width, height, uFalse, vFalse, uTrue, vTrue, uFalse, vFalse, uTrue, vTrue, image);
        }
    }
    public record BorderStyle(int borderStartColor, int borderEndColor, Enums.BorderType type, float thickness) {
        public static final BorderStyle NONE = new BorderStyle(0, 0, Enums.BorderType.NONE, 0f);

        public BorderStyle {
            if(type == null || thickness <= 0f) type = Enums.BorderType.NONE;
        }

        public BorderStyle(int borderColor, Enums.BorderType type) {
            this(borderColor, borderColor, type, 1f);
        }

        public BorderStyle(int borderColor, Enums.BorderType type, float thickness) {
            this(borderColor, borderColor, type, thickness);
        }

        public BorderStyle(int borderStartColor, int borderEndColor, Enums.BorderType type) {
            this(borderStartColor, borderEndColor, type, 1f);
        }

        public boolean isNone() {
            return type == Enums.BorderType.NONE;
        }
    }

    public record BackgroundStyle(int backgroundStartColor, int backgroundEndColor, Enums.BackgroundType type, int paddingX, int paddingY, BorderStyle border) {
        public static final BackgroundStyle NONE =
                new BackgroundStyle(0, 0, Enums.BackgroundType.NONE, 0, 0, BorderStyle.NONE);

        public BackgroundStyle {
            if(type == null) type = Enums.BackgroundType.NONE;
            if(border == null) border = BorderStyle.NONE;
        }

        public BackgroundStyle(int startColor, int endColor, Enums.BackgroundType type, int paddingX, int paddingY) {
            this(startColor, endColor, type, paddingX, paddingY, BorderStyle.NONE);
        }

        public BackgroundStyle(int color, Enums.BackgroundType type, int paddingX, int paddingY) {
            this(color, color, type, paddingX, paddingY, BorderStyle.NONE);
        }

        public BackgroundStyle(int color, Enums.BackgroundType type) {
            this(color, color, type, 4, 4, BorderStyle.NONE);
        }

        public boolean isNone() { return type == Enums.BackgroundType.NONE; }
        public boolean hasBorder() { return !border.isNone(); }

        //Inner Fluent
        public BackgroundStyle withBorder(BorderStyle border) {
            return new BackgroundStyle(backgroundStartColor, backgroundEndColor, type, paddingX, paddingY, border);
        }
        public BackgroundStyle withPadding(int padX, int padY) {
            return new BackgroundStyle(backgroundStartColor, backgroundEndColor, type, padX, padY, border);
        }
    }
}


