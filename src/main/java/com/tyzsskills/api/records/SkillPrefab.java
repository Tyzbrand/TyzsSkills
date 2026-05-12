package com.tyzsskills.api.records;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.model.SkillBehavior;
import com.tyzsskills.api.model.SkillConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Data container to register default JSON generation for custom skills
 */
public record SkillPrefab(boolean active, @NotNull String id, int maximumLevel,
                          @NotNull List<Integer> prices, @NotNull Enums.SkillType type, @NotNull String category,
                          @NotNull String icon, @NotNull String displayName, @NotNull String description,
                          @Nullable List<Modifier> modifiers, @Nullable Map<String, ValueSet> customValues, @Nullable SkillBehavior behavior,
                          @Nullable SkillConfiguration config) {
}
