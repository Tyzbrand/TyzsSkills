package com.tyzsskills.api.model.settings;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.records.SettingMeta;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public final class ColorSetting extends Setting<String>{
    public static final String REGEX = "^#?([0-9a-fA-F]{6}|[0-9a-fA-F]{8})$";

    private int hex;

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
        return raw instanceof String s && s.matches(REGEX) ? raw : getDefault();
    }

    public int getHex() {return hex;}

    //region NETWORK
    @Override
    protected void writeData(@NotNull FriendlyByteBuf buf) {
        buf.writeUtf(get());
        buf.writeUtf(getDefault());
    }

    static @NotNull ColorSetting readData(@NotNull FriendlyByteBuf buf, @NotNull SettingMeta meta){
        var value = buf.readUtf();
        var def = buf.readUtf();

        var setting = new ColorSetting(meta, def);
        setting.set(value);

        return setting;
    }
    //endregion

    //UTILS
    private int calculateHex(){
        var clean = get().replace("#", "");
        if (clean.length() == 6) clean = "FF" + clean;

        return (int) Long.parseLong(clean, 16);
    }
}
