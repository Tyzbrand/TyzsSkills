package com.tyzsskills.api.tools;

public class ColorTools {
    public static int darken(int color, float factor) {
        var alpha = (color >>> 24) & 0xFF;
        var red   = (color >>> 16) & 0xFF;
        var green = (color >>> 8) & 0xFF;
        var blue  = color & 0xFF;

        red   = (int) (red * factor);
        green = (int) (green * factor);
        blue  = (int) (blue * factor);

        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
    public static int darken(int color){return darken(color, .5f);}

    public static int lighten(int color, float factor) {
        var alpha = (color >>> 24) & 0xFF;
        var red   = (color >>> 16) & 0xFF;
        var green = (color >>> 8) & 0xFF;
        var blue  = color & 0xFF;

        red   = red   + Math.round((255 - red) * factor);
        green = green + Math.round((255 - green) * factor);
        blue  = blue  + Math.round((255 - blue) * factor);

        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
    public static int lighten(int color){return lighten(color, .2f);}
}
