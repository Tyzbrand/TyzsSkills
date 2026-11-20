package com.tyzsskills.api.model.settings;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.records.SettingMeta;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract sealed class Setting<T> permits IntSetting, BoolSetting, FloatSetting, ColorSetting {
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
    public final @NotNull T get() {return value;}
    public final boolean isDefault(){return Objects.equals(value, defaultValue);}
    public final Enums.SettingType type(){return type;}
    public final @NotNull String comment(){return comment;}
    public final @NotNull String key(){return meta.key();}

    public void set(@NotNull T value){this.value = validate(value);}
    public final void reset(){set(defaultValue);}

    @SuppressWarnings("unchecked")
    public final void applyValueFrom(@NotNull Setting<?> model){
        if(type != model.type) throw new IllegalArgumentException("Setting type mismatch for key: " + key());
        this.set((T) model.get());
    }

    protected abstract @NotNull T validate(@NotNull T raw);
    protected abstract void writeData(@NotNull FriendlyByteBuf buf);


    //region NETWORK
    public static final StreamCodec<FriendlyByteBuf, Setting<?>> STREAM_CODEC = StreamCodec.of(Setting::encode, Setting::decode);

    private static void encode(FriendlyByteBuf buf, Setting<?> setting) {
        buf.writeEnum(setting.type());
        SettingMeta.writeToBuffer(buf, setting.meta());
        setting.writeData(buf);
    }

    private static Setting<?> decode(FriendlyByteBuf buf) {
        var type = buf.readEnum(Enums.SettingType.class);
        var meta = SettingMeta.readFromBuffer(buf);

        return switch (type) {
            case BOOL  -> BoolSetting.readData(buf, meta);
            case INT   -> IntSetting.readData(buf, meta);
            case FLOAT -> FloatSetting.readData(buf, meta);
            case COLOR -> ColorSetting.readData(buf, meta);
        };
    }
    //endregion
}
