package com.tyzsskills.api.interfaces;

import com.tyzsskills.api.records.SpellProperty;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public interface ISpell {

    @NotNull String getId();

    int getPrice();

    /**
     * @return a resourceLocation
     */
    @NotNull ResourceLocation getIcon();

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
}
