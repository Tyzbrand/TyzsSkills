package com.tyzsskills.api.model.settings;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.records.SettingMeta;
import org.jetbrains.annotations.NotNull;

public final class IntSetting extends Setting<Integer> {
    private final int min;
    public int min(){return min;}
    private final int max;
    public int max(){return max;}


    public IntSetting(@NotNull SettingMeta meta, @NotNull Integer defaultValue, int min, int max) {
        super(meta, Enums.SettingType.INT, defaultValue);

        this.min = min;
        this.max = max;
    }

    @Override
    protected @NotNull Integer validate(@NotNull Integer raw) {
        return Math.clamp(raw, min, max);
    }

    @Override
    public @NotNull String range() {
        var finalMax = max == Integer.MAX_VALUE ? "∞" : String.valueOf(max);
        return String.format("default: %s, min: %s, max: %s", getDefault(), min, finalMax);
    }

    @Override
    public void parse(@NotNull String raw) {
        set(Integer.valueOf(raw));
    }
}
