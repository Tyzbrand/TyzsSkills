package com.tyzsskills.api.records;

import com.tyzsskills.api.Enums;
import net.minecraft.network.FriendlyByteBuf;

public record SettingMeta(String key, Enums.SettingCategory category, String displayName, String description) {
    public SettingMeta(String key, Enums.SettingCategory category){
        this(key, category,
                String.format("settings.tyzs_skills.%s.%s.displayName", category.name().toLowerCase(), key),
                String.format("settings.tyzs_skills.%s.%s.description", category.name().toLowerCase(), key)
        );
    }

    public boolean isClient(){return category().isClient();}

    //region NETWORK
    public static void writeToBuffer(FriendlyByteBuf buffer, SettingMeta meta){
        buffer.writeUtf(meta.key());
        buffer.writeEnum(meta.category());
    }

    public static SettingMeta readFromBuffer(FriendlyByteBuf buffer){
        var readKey = buffer.readUtf();
        var readCat = buffer.readEnum(Enums.SettingCategory.class);
        return new SettingMeta(readKey, readCat);
    }
    //endregion
}
