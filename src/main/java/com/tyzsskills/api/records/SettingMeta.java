package com.tyzsskills.api.records;

import com.tyzsskills.api.Enums;

public record SettingMeta(String key, Enums.SettingCategory category, String displayName, String description) {
    public SettingMeta(String key, Enums.SettingCategory category){
        this(key, category,
                String.format("settings.tyzs_skills.%s.%s.displayName", category.name().toLowerCase(), key),
                String.format("settings.tyzs_skills.%s.%s.description", category.name().toLowerCase(), key)
        );
    }

    public boolean isClient(){return category().isClient();}
}
