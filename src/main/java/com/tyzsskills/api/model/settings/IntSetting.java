package com.tyzsskills.api.model.settings;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.records.SettingMeta;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public final class IntSetting extends Setting<Integer> {
    private final int min;
    private final int max;


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

    public int min(){return min;}
    public int max(){return max;}



    //region NETWORK
    @Override
    protected void writeData(@NotNull FriendlyByteBuf buf) {
        buf.writeInt(get());
        buf.writeInt(getDefault());
        buf.writeInt(min);
        buf.writeInt(max);
    }

    static @NotNull IntSetting  readData(@NotNull FriendlyByteBuf buf, @NotNull SettingMeta meta){
        var value = buf.readInt();
        var def = buf.readInt();
        var min = buf.readInt();
        var max = buf.readInt();

        var setting = new IntSetting(meta, def, min, max);
        setting.set(value);

        return setting;
    }
    //endregion
}
