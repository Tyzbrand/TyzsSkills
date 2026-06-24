package com.tyzsskills.api.interfaces;

import com.tyzsskills.api.records.SpellProperty;
import com.tyzsskills.api.records.SpellPropertyContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public interface ISpell {

    @NotNull String getID();

    /**
     * @return a resourceLocation path as a string
     */
    @NotNull String getIcon();

    /**
     * @return a localization key
     */
    @NotNull String getDisplayName();

    /**
     * @return a localization key
     */
    @NotNull String getDescription();

    @Nullable SpellProperty getProperty(@NotNull String propertyKey);

    @NotNull @Unmodifiable List<SpellProperty> getProperties();

    default int getMaximumLevelForProperty(@NotNull String propertyKey){
        var property = getProperty(propertyKey);
        return property != null ? property.getMaximumLevel() : 0;
    }

    boolean canBuy(@NotNull SpellPropertyContext ctx, boolean purchaseEnabled);
}
