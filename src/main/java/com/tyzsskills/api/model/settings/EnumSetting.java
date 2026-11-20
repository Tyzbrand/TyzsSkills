package com.tyzsskills.api.model.settings;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.records.SettingMeta;
import org.jetbrains.annotations.NotNull;

public final class EnumSetting<E extends Enum<E>> extends Setting<E>{
    private final Class<E> enumClass;
    public @NotNull Class<E> enumClass(){return enumClass;}

    public EnumSetting(@NotNull SettingMeta meta, @NotNull E defaultValue) {
        super(meta, Enums.SettingType.ENUM, defaultValue);
        enumClass = defaultValue.getDeclaringClass();
    }

    @Override
    protected @NotNull E validate(@NotNull E raw) {
        return raw;
    }

    @Override
    public @NotNull String range() {
        var sb = new StringBuilder();
        sb.append(String.format("default: %s, possible values: ", getDefault().name()));

        var items = getDefault().getDeclaringClass().getEnumConstants();
        for(var item : items) sb.append(String.format("|%s", item.name()));
        sb.append("|");

        return sb.toString();
    }

    //region NETWORK
    @Override
    public @NotNull String serialize() {return get().name();}

    @Override
    public void parse(@NotNull String raw){
        var trimmed = raw.trim();
        for (var constant : enumClass.getEnumConstants()) {
            if (constant.name().equalsIgnoreCase(trimmed)) {
                set(constant);
                return;
            }
        }
    }
    //endregion

}
