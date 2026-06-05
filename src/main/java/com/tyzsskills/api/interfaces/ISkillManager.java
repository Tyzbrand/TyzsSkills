package com.tyzsskills.api.interfaces;

import com.tyzsskills.api.records.SkillPrefab;
import com.tyzsskills.api.model.SkillBehavior;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Interface used to manage player and server skills.
 * Client sync is handled automatically when not specified.
 */
public interface ISkillManager {

    //======================SKILL ACTIONS======================
    /**
     * @param skillId Valid id of the targeted skill (in lower case).
     * @param newLevel New level to overwrite the current one (must be >= {@code 0})
     * @implNote If the new level exceeds the skill's max level, the change will fail.
     */
    void setSkillLevel(@NotNull ServerPlayer player, @NotNull String skillId, int newLevel);

    /**
     * Resets all the player skills back to level {@code 0}.
     * @implNote Works only for loaded skills. Client is not synchronized.
     */
    void resetSkillLevels(@NotNull ServerPlayer player);

    /**
     * @param skillId Valid id of the targeted skill (in lowercase).
     * @param amount The amount of levels to add (must be > {@code 0}).
     * @return {@code true} if the addition is successful, {@code false} otherwise.
     * @implNote If the new level exceeds the skill's max level, the addition will fail.
     */
    boolean tryAddSkillLevel(@NotNull ServerPlayer player, @NotNull String skillId, int amount);

    /**
     * @param skillId Valid id of the targeted skill (in lowercase).
     * @param amount The amount of levels to remove (must be > {@code 0}).
     * @return {@code true} if the withdrawal is successful, {@code false} otherwise.
     * @implNote The player must have sufficient level for the skill.
     */
    boolean tryRemoveSkillLevel(@NotNull ServerPlayer player, @NotNull String skillId, int amount);


    /**
     * Tries to buy a skill according to the rules of the mod (not only a verification).
     * @param skillId Valid id of the targeted skill (in lowercase).
     * @return {@code true} if the skill has been purchased, {@code false} otherwise.
     * @implNote If the process is successful data are handled automatically (sp, skill lvl and stats).
     */
    boolean tryBuySkill(@NotNull ServerPlayer player, @NotNull String skillId);


    /**
     * Tries to refund a skill according to the rules of the mod (not only a verification).
     * @param skillId Valid id of the targeted skill (in lowercase).
     * @return {@code true} if the skill has been refunded, {@code false} otherwise.
     * @implNote If the process is successful data are handled automatically (sp, skill lvl, and stats).
     */
    boolean tryRefundSkill(@NotNull ServerPlayer player, @NotNull String skillId);

    /**
     * Tries to buy the maximum levels a player can afford, according to the rules of the mod (not only a verification).
     * @param skillId Valid id of the targeted skill (in lowercase).
     * @return {@code true} if the bulk has succeeded, {@code false} otherwise.
     * @implNote If the process is successful data are handled automatically (sp, skill lvl, and stats).
     */
    boolean tryBulkBuy(@NotNull ServerPlayer player, @NotNull String skillId);

    /**
     * Tries to refund the skill levels to 0, according to the rules of the mod (not only a verification).
     * @param skillId Valid id of the targeted skill (in lowercase).
     * @return {@code true} if the bulk has succeeded, {@code false} otherwise.
     * NOTE: if the process is successful data are handled automatically (sp, skill lvl, and stats).
     */
    boolean tryBulkRefund(@NotNull ServerPlayer player, @NotNull String skillId);


    //======================SKILL DATA======================
    /**
     * @param skillId Skill id to check.
     * @return {@code true} if the server skill list contains the skill for the specified id, {@code false} otherwise.
     */
    boolean isSkillLoaded(@NotNull String skillId);

    /**
     * @param skillId Valid id of the targeted skill (in lowercase).
     * @return Targeted skill's level (returns {@code 0} if the skill is invalid).
     */
    int getSkillLevel(@NotNull ServerPlayer player, @NotNull String skillId);

    /**
     * @return A copy of the server skill list (contains all loaded skills).
     */
    @NotNull List<ISkill> getSkillList();

    /**
     * @param skillId Valid id of the targeted skill (in lowercase).
     * @return The {@link ISkill} linked to the specified id, or {@code null} if it doesn't exist.
     */
    @Nullable ISkill getSkill(@NotNull String skillId);


    //======================BOOKMARKS======================
    /**
     * Triggers a bookmark if the skill isn't already bookmarked and vice versa.
     * @param skillID Valid id of the targeted skill (in lowercase).
     */
    void triggerSkillBookmark(@NotNull ServerPlayer player, @NotNull String skillID);

    /**
     * @param skillID Valid id of the targeted skill (in lowercase).
     * @return {@code true} if the skill is bookmarked by the player, {@code false} otherwise.
     */
    boolean isSkillBookmarked(@NotNull ServerPlayer player, @NotNull String skillID);

    /**
     * @return A copy of player's bookmarks (as a read-only list of skill IDs).
     */
    @NotNull @Unmodifiable List<String> getBookmarkedSkillIDs(@NotNull ServerPlayer player);

    //======================MISC======================
    /**
     * Triggers the skill activation overlay on the client side.
     * Useful for custom events handled outside of standard SkillBehaviors.
     * @param skillId The valid ID of the targeted skill used as ref icon (in lowercase).
     */
    void triggerSkillActivationOverlay(@NotNull ServerPlayer player, @NotNull String skillId);


}
