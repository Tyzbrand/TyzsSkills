package com.tyzsskills.api.interfaces;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.records.Modifier;
import com.tyzsskills.api.records.ValueSet;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * ReadOnly interface used to access skill information
 */
public interface ISkill {

    String getID();

    int getMaximumLevel();

    /**
     * @return a list of prices (1st price in the list is the price from lvl 0 to lvl 1)
     */
    List<Integer> getPrices();


    Enums.SkillType getType();


    Enums.CategoryType getCategory();


    boolean isPurchasable();

    /**
     * @return a resourceLocation path as a string
     */
    String getIcon();

    /**
     * @return a localization key
     */
    String getDisplayName();

    /**
     * @return a localization key
     */
    String getDescription();

    /**
     * Retrieves all registered value sets for this skill
     * @return An unmodifiable map where the key is the ValueSet ID
     * and the value is the corresponding {@link ValueSet} object
     */
    Map<String, ValueSet> getValues();

    /**
     * Gets a specific value set by its ID
     * @param key The unique ID of the value set (as defined in your JSON data)
     * @return The {@link ValueSet} associated with the key if found
     */
    @Nullable
    ValueSet getValueSet(String key);

    /**
     * Returns a list of all modifiers affected by the skill
     * @return A {@link List} of {@link Modifier} objects
     */
    List<Modifier> getModifiers();



}
