package com.tyzsskills.api.interfaces;

import net.minecraft.server.level.ServerPlayer;

import java.util.List;

/**
 * Interface used to manage player and server skills
 * Client sync is handled automatically
 */
public interface ISkillManager {

    /**
     * @param id Skill id to check
     * @return true if the server skill list contains the skill for the specified id, false otherwise
     */
    boolean isSkillLoaded(String id);

    /**
     * @param id Valid id of the targeted skill (in lowercase)
     * @return targeted skill level (returns 0 if the skill is invalid)
     */
    int getSkillLevel(ServerPlayer player, String id);

    /**
     * @param id Valid id of the targeted skill (in lowercase)
     * @param amount The amount to add (must be > 0)
     * NOTE: if the new level exceeds the skill's max level, extra levels will not be added
     */
    void addSkillLevel(ServerPlayer player, String id, int amount);

    /**
     * @param id Valid id of the targeted skill (in lowercase)
     * @param amount The amount to remove (works if the player has sufficient levels, must be > 0)
     */
    void removeSkillLevel(ServerPlayer player, String id, int amount);

    /**
     * @param id Valid id of the targeted skill (in lower case)
     * @param amount The amount to set (must be >= 0)
     * NOTE: if the new level exceeds the skill's max level, extra levels will not be added
     */
    void setSkillLevel(ServerPlayer player, String id, int amount);

    /**
     * Tries to buy a skill according to the rules of the mod (not only a verification)
     * @param id Valid id of the targeted skill (in lowercase)
     * @return true if the skill has been purchased, false otherwise
     * NOTE: if the process is successful data are handled automatically (sp, skill lvl and stats)
     */
    boolean buySkill(ServerPlayer player, String id);

    /**
     * Tries to refund a skill according to the rules of the mod (not only a verification)
     * @param id Valid id of the targeted skill (in lowercase)
     * @return true if the skill has been refunded, false otherwise
     * NOTE: if the process is successful data are handled automatically (sp, skill lvl, and stats)
     */
    boolean refundSkill(ServerPlayer player, String id);

    /**
     * @return a copy of the server skill list (contains all loaded skills)
     */
    List<ISkill> getSkillList();

    /**
     * @param id Valid id of the targeted skill (in lowercase)
     * @return the skill linked to the specified id, or null if it doesn't exist
     */
    ISkill getSkill(String id);

    /**
     * @param id Valid id of the targeted skill (in lowercase)
     * NOTE: if the skill is already bookmarked, it will be removed from bookmarks and vice versa
     */
    void triggerSkillBookmark(ServerPlayer player, String id);

    /**
     * @param id Valid id of the targeted skill (in lowercase)
     * @return true if the skill is bookmarked by the player, false otherwise
     */
    boolean isSkillBookmarked(ServerPlayer player, String id);

    /**
     * @return a copy of player's bookmarks (as a list of skill IDs)
     */
    List<String> getBookmarkedSkillIDs(ServerPlayer player);

}
