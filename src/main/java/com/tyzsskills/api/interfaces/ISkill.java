package com.tyzsskills.api.interfaces;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.model.SkillContext;
import com.tyzsskills.api.records.*;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collections;
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


    Enums.CategoryType getCategory();


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
     * Checks if the player can purchase the next skill level.
     * @return {@code true} if the player can afford the next skill level, {@code false} otherwise.
     */
    boolean canBuy(@NotNull SkillContext ctx);


    /**
     * Checks if the player can refund their current skill level.
     * @param refundEnabled The current state of the refund system configuration.
     * @return {@code true} if the skill level can be refunded, {@code false} otherwise.
     */
    boolean canRefund(@NotNull SkillContext ctx, boolean refundEnabled);


    /**
     * Calculates the maximum number of skill levels a player can purchase at once.
     * @return A {@link BulkPurchaseResult} indicating how many levels can be bought and the total cost. Never null.
     */
    @NotNull BulkPurchaseResult checkBulkBuy(@NotNull SkillContext ctx);


    /**
     * Calculates the total amount of SP returned from a complete skill refund.
     * @param refundPercentage The current value of the refund percentage configuration.
     * @param refundEnabled The current state of the refund system configuration.
     * @return The total amount of SP the player will receive from the bulk refund.
     */
    int checkBulkRefund(@NotNull SkillContext ctx, float refundPercentage, boolean refundEnabled);

    /**
     * Gets the global player level required to purchase this skill.
     * @return The level requirement for the skill, {@code -1} if the skill doesn't have one.
     */
    int getRequiredLevel();

    /**
     * Checks if a specified skill is incompatible with this one.
     * @param skillID Valid id of the targeted skill (in lowercase).
     * @return {@code true} if the specified skill is marked as incompatible, {@code false} otherwise.
     */
    boolean isSkillIncompatible(@NotNull String skillID);

    /**
     * Gets all skill incompatibilities.
     * @return A list containing all incompatible skill ids. If there are no incompatibilities, return an empty list.
     */
    @NotNull List<String> getRawIncompatibilities();


    /**
     * Adds an incompatibility to the skill.
     * @param id Valid id of the targeted incompatible skill (in lowercase).
     */
    void addIncompatibility(@NotNull String id);

    void removeIncompatibility(@NotNull String id);



    /**
     * Gets all skill that are mutually exclusive with this one (as ids).
     * Applies only to the skills the player currently possesses.
     * @param ownedSkillIds A list containing all skill IDs currently owned by the player.
     * @return A list containing all incompatible skill ids the player possesses. If there are no incompatibilities, return an empty list.
     */
    @NotNull List<String> getIncompatibilities(@NotNull List<String> ownedSkillIds);

    @NotNull List<String> getRawPrerequisites();
    @NotNull List<String> getPrerequisites(@NotNull List<String> ownedSkillIds);
    void removePrerequisite(@NotNull String id);


    /**
     * Checks if the player meets the global level requirement for this skill.
     * @param playerLvl The current lvl of the player. Use -1 to bypass the check.
     * @return {@code true} if the player's level is equal to or greater than the requirement, {@code false} otherwise.
     */
    default boolean meetsLevelRequirement(int playerLvl){
        if (getRequiredLevel() <= 1 || playerLvl == -1) return true;
        return playerLvl >= getRequiredLevel();
    }

    boolean isAvailable(@NotNull SkillContext ctx);

}
