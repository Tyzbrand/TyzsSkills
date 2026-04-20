package com.tyzsskills.api.interfaces;

import java.util.List;

/**
 * This is a readOnly interface used to access player's client side data
 * This data is automatically synced from the server
 * NOTE: Make sure to access these functions from the client side ONLY
 */
public interface IClientDataManager {


    int getLevel();
    int getSP();
    float getXP();

    /**
     * @return the amount of SP the player will earn when he'll reach the goal (level up)
     */
    int getSpReward();

    /**
     * @return the amount of XP the player must reach to level up
     */
    float getXpGoal();

    float getAllTimeXP();
    float getSessionXP();
    int getSpEarned();
    int getSpSpent();
    int getOwnedSkills();

    /**
     * Access the skill list synced from the server
     * @return A list of skill IDs
     * NOTE: This list is identical across all clients
     */
    List<String> getSkillList();

    /**
     * Access the player's bookmarks
     * @return A list of bookmarked skill IDs
     */
    List<String> getBookmarks();

    /**
     * @param id Valid id of the targeted skill (in lower case)
     * @return true if the skill is bookmarked by the player, false otherwise
     */
    boolean isSkillBookmarked(String id);

    /**
     * @param id Valid id of the targeted skill (in lower case)
     * @return the player's cached level for the targeted skill (returns 0 if invalid)
     */
    int getSkillLevel(String id);

    /**
     * Retrieves the IDs of all skills that the player has at least one level in
     * @return A list of unique ids for all skills currently unlocked (level > 0)
     */
    List<String> getPurchasedSkills();



}
