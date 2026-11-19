package com.tyzsskills.api.model.settings;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.records.SettingMeta;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public final class BoolSetting extends Setting<Boolean> {
    public BoolSetting(@NotNull SettingMeta meta, @NotNull Boolean defaultValue) {
        super(meta, Enums.SettingType.BOOL, defaultValue);
    }

    @Override
    protected @NotNull Boolean validate(@NotNull Boolean raw) {
        return raw;
    }

    //region NETWORK
    @Override
    protected void writeData(@NotNull FriendlyByteBuf buf) {
        buf.writeBoolean(get());
        buf.writeBoolean(getDefault());
    }

    static @NotNull BoolSetting readData(@NotNull FriendlyByteBuf buf, @NotNull SettingMeta meta){
        var value = buf.readBoolean();
        var def = buf.readBoolean();

        var setting = new BoolSetting(meta, def);
        setting.set(value);

        return setting;
    }
    //endregion

    //region IO

    //endregion
}
