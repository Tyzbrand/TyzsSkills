package com.tyzsskills.api.model.settings;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.records.SettingMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract sealed class Setting<T> permits IntSetting, BoolSetting, FloatSetting, ColorSetting, EnumSetting {
    private final SettingMeta meta;
    private final Enums.SettingType type;
    private final T defaultValue;
    private volatile T value;

    protected String comment = "";
    public final Setting<T> withComment(String comment){this.comment = comment; return this;}

    public Setting(@NotNull SettingMeta meta, Enums.SettingType type, @NotNull T defaultValue) {
        this.meta = meta;
        this.type = type;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public @NotNull String range() {
        return String.format("default: %s", getDefault());
    }

    public final @NotNull SettingMeta meta() {return meta;}
    public final @NotNull T getDefault() {return defaultValue;}

    public final boolean isDefault(){return Objects.equals(value, defaultValue);}
    public final Enums.SettingType type(){return type;}
    public final @NotNull String comment(){return comment;}
    public final @NotNull String key(){return meta.key();}

    public void set(@NotNull T value){this.value = validate(value);}
    public @NotNull T get() {return value;}
    public final void reset(){set(defaultValue);}
    public @NotNull String serialize(){return String.valueOf(get());}

    protected abstract @NotNull T validate(@NotNull T raw);

    protected abstract void parse(@NotNull String raw);
    public final void safeParse(@NotNull String raw){
        try{parse(raw);}
        catch (Exception ex){throw new IllegalStateException("Unable to parse setting with key: " + meta.key(), ex);}
    }
}
