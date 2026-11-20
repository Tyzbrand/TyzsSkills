package com.tyzsskills.api.model.settings;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.records.SettingMeta;
import org.jetbrains.annotations.NotNull;

public final class FloatSetting extends Setting<Float> {
    private final float min;
    public float min(){return min;}
    private final float max;
    public float max(){return max;}

    public FloatSetting(@NotNull SettingMeta meta, @NotNull Float defaultValue, float min, float max) {
        super(meta, Enums.SettingType.FLOAT, defaultValue);

        this.min = min;
        this.max = max;
    }

    @Override
    protected @NotNull Float validate(@NotNull Float raw) {
        if(raw.isNaN()) return getDefault();
        return Math.clamp(raw, min, max);
    }

    @Override
    public @NotNull String range() {
        var finalMax = max == Float.MAX_VALUE ? "∞" : String.valueOf(max);
        return String.format("default: %s, min: %s, max: %s", getDefault(), min, finalMax);
    }

    @Override
    public void parse(@NotNull String raw) {
       set(Float.parseFloat(raw));
    }
}
