package com.tyzsskills.api.records;

import com.tyzsskills.api.Enums;

import java.util.List;
import java.util.Map;

/**
 * Data container to register default JSON generation for custom skills
 */
public record SkillPrefab(boolean active, String id, int maximumLevel,
                          List<Integer> prices, Enums.SkillType type, Enums.CategoryType category,
                          boolean purchasable, String icon, String displayName, String description,
                          List<Modifier> modifiers, Map<String, ValueSet> customValues) {
}
