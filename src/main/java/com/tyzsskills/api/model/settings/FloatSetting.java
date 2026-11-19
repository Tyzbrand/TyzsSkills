package com.tyzsskills.api.model.settings;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.records.SettingMeta;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public final class FloatSetting extends Setting<Float> {
    private final float min;
    private final float max;

    public FloatSetting(@NotNull SettingMeta meta, @NotNull Float defaultValue, float min, float max) {
        super(meta, Enums.SettingType.FLOAT, defaultValue);

        this.min = min;
        this.max = max;
    }

    @Override
    protected @NotNull Float validate(@NotNull Float raw) {
        return Math.clamp(raw, min, max);
    }

    @Override
    public @NotNull String range() {
        var finalMax = max == Float.MAX_VALUE ? "∞" : String.valueOf(max);
        return String.format("default: %s, min: %s, max: %s", getDefault(), min, finalMax);
    }

    public float min(){return min;}
    public float max(){return max;}

    //region NETWORK
    @Override
    protected void writeData(@NotNull FriendlyByteBuf buf) {
        buf.writeFloat(get());
        buf.writeFloat(getDefault());
        buf.writeFloat(min);
        buf.writeFloat(max);
    }

    static @NotNull FloatSetting readData(@NotNull FriendlyByteBuf buf, @NotNull SettingMeta meta){
        var value = buf.readFloat();
        var def = buf.readFloat();
        var min = buf.readFloat();
        var max = buf.readFloat();

        var setting = new FloatSetting(meta, def, min, max);
        setting.set(value);

        return setting;
    }
    //endregion
}
