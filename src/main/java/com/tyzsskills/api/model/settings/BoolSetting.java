package com.tyzsskills.api.model.settings;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.records.SettingMeta;
import org.jetbrains.annotations.NotNull;

public final class BoolSetting extends Setting<Boolean> {
    public BoolSetting(@NotNull SettingMeta meta, @NotNull Boolean defaultValue) {
        super(meta, Enums.SettingType.BOOL, defaultValue);
    }

    @Override
    protected @NotNull Boolean validate(@NotNull Boolean raw) {
        return raw;
    }

    @Override
    public void parse(@NotNull String raw) {
        if (!"true".equalsIgnoreCase(raw) && !"false".equalsIgnoreCase(raw))
            throw new IllegalArgumentException("Invalid boolean: " + raw);

        set(Boolean.parseBoolean(raw));
    }
}
