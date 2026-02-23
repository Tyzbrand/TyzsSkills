package com.tyzsskills.api.records;

import com.tyzsskills.api.Enums;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.List;

/**
 * Data container to register default JSON generation for custom skills
 */
public record SkillPrefab(boolean active, String id, int maximumLevel,
                          List<Integer> prices, List<Float> values, Enums.SkillType type, Enums.CategoryType category,
                          String modifier, AttributeModifier.Operation operation, boolean purchasable,
                          String icon, String displayName, String description, String unit, boolean isTrait, int powerWeight) {
}
