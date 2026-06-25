package com.tyzsskills.api.records;

import com.tyzsskills.api.model.SpellBehavior;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record SpellPrefab(boolean active, @NotNull String id, int price, @NotNull String icon, @NotNull String displayName,
                          @NotNull String description, @NotNull List<SpellProperty> properties, @NotNull SpellBehavior behavior) {
}
