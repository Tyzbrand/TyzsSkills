package com.tyzsskills.api.interfaces;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.records.*;
import org.jetbrains.annotations.NotNull;

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

    /**
     * Checks if the player can purchase the next skill level.
     * @param currentLvl The current skill level of the player.
     * @param currentSP The current amount of SP the player possesses.
     * @return {@code true} if the player can afford the next skill level, {@code false} otherwise.
     */
    boolean canBuy(int currentLvl, int currentSP);

    /**
     * Checks if the player can refund their current skill level.
     * @param currentLvl The current skill level of the player.
     * @param refundEnabled The current state of the refund system configuration.
     * @return {@code true} if the skill level can be refunded, {@code false} otherwise.
     */
    boolean canRefund(int currentLvl, boolean refundEnabled);

    /**
     * Calculates the maximum number of skill levels a player can purchase at once.
     * @param currentLvl The current skill level of the player.
     * @param availableSp The current amount of SP the player possesses.
     * @return A {@link BulkPurchaseResult} indicating how many levels can be bought and the total cost. Never null.
     */
    BulkPurchaseResult checkBulkPurchase(int currentLvl, int availableSp);

    /**
     * Calculates the total amount of SP returned from a complete skill refund.
     * @param currentLvl The current skill level of the player.
     * @param refundPercentage The current value of the refund percentage configuration.
     * @return The total amount of SP the player will receive from the bulk refund.
     */
    int checkBulkRefund(int currentLvl, float refundPercentage);



}
