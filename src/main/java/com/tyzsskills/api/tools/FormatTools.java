package com.tyzsskills.api.tools;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class FormatTools {
    public static String defaultFloat(float value){
        return FORMATER.get().format(value);
    }
    public static String bigFloat(float value){
        float absValue = Math.abs(value);

        if(absValue >= 1_000_000_000f){return FORMATER.get().format(value / 1_000_000_000f) + "B";}
        if (absValue >= 1_000_000f) {return FORMATER.get().format(value / 1_000_000f) + "M";}
        if (absValue >= 10_000f) {return FORMATER.get().format(value / 1_000f) + "k";}
        return FORMATER.get().format(value);
    }

    private static final ThreadLocal<DecimalFormat> FORMATER = ThreadLocal.withInitial(() -> {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');

        DecimalFormat format = new DecimalFormat("0.#", symbols);
        format.setRoundingMode(RoundingMode.DOWN);
        return format;
    });
}
