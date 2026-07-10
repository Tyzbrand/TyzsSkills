package com.tyzsskills.api.interfaces;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.records.PlayerContext;
import com.tyzsskills.api.records.*;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * ReadOnly interface used to access skill information
 */
public interface ISkill {

    @NotNull String getID();

    int getMaximumLevel();

    /**
     * Gets the skill prices list.
     * @return a list of prices (1st price in the list is the price from lvl 0 to lvl 1).
     */
    @NotNull List<Integer> getPrices();

    /**
     * Gets the skill price to buy the specified level.
     * This represents the price for a single upgrade (from level - 1 to the specified level),
     * NOT the cumulative total cost from the actual player level to the targeted level.
     * @param lvl The Targeted level.
     * @return The SP cost required to unlock the specified level.
     */
    int getPrice(int lvl);


    Enums.SkillType getType();


    String getCategory();


    boolean isPurchasable();

    boolean isRefundable();

    boolean isVisible();

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


    /**
     * Retrieves all registered value sets for this skill
     * @return An unmodifiable map where the key is the ValueSet ID
     * and the value is the corresponding {@link ValueSet} object
     */
    @NotNull Map<String, ValueSet> getValues();

    /**
     * Gets a specific value set by its ID
     * @param key The unique ID of the value set (as defined in your JSON data)
     * @return The {@link ValueSet} associated with the key if found
     */
    @Nullable
    ValueSet getValueSet(@NotNull String key);

    /**
     * Returns a list of all modifiers affected by the skill
     * @return A {@link List} of {@link Modifier} objects
     */
    List<Modifier> getModifiers();


    /**
     * Gets all skill incompatibilities.
     * @return A list containing all incompatible skill ids. If there are no incompatibilities, return an empty list.
     */
    @NotNull List<String> getRawIncompatibilities();

    @NotNull Map<String, Integer> getRawPrerequisites();

    /**
     * Gets the global player level required to purchase this skill.
     * @return The level requirement for the skill, {@code -1} if the skill doesn't have one.
     */
    int getRequiredLevel();

    @NotNull CompoundTag getSpecificParameters();

}
