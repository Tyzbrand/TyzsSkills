package com.tyzsskills.api.model.settings;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.records.SettingMeta;
import org.jetbrains.annotations.NotNull;

public final class ColorSetting extends Setting<String>{
    public static final String REGEX = "^#?([0-9a-fA-F]{6}|[0-9a-fA-F]{8})$";

    private int hex;
    public int getHex() {return hex;}

    public ColorSetting(@NotNull SettingMeta meta, @NotNull String defaultValue) {
        super(meta, Enums.SettingType.COLOR, defaultValue);
        hex = calculateHex();
    }

    @Override
    public void set(@NotNull String value) {
        super.set(value);
        hex = calculateHex();
    }

    @Override
    protected @NotNull String validate(@NotNull String raw) {
        return raw.matches(REGEX) ? raw : getDefault();
    }


    @Override
    public @NotNull String range() {
        var def = super.range();
        return def + ", format: #AARRGGBB";
    }

    @Override
    public void parse(@NotNull String raw) {
        if(!raw.matches(REGEX)) throw new IllegalArgumentException("Invalid color: " + raw);
        set(raw);
    }

    //UTILS
    private int calculateHex(){
        var clean = get().replace("#", "");
        if (clean.length() == 6) clean = "FF" + clean;

        return (int) Long.parseLong(clean, 16);
    }
}
