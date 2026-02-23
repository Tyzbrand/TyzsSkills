package com.tyzsskills.api.interfaces;

import com.tyzsskills.api.records.SkillPrefab;
import com.tyzsskills.impl.server.model.SkillBehavior;
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


    //Functions below  are used for skill creation

    /**
     * Registers a default skill configuration to be generated as a JSON file
     * @param prefab The skill prefab to generate
     * NOTE: If the JSON already exists in the config folder, it will NOT be overwritten
     * DISCLAIMER: This method must be called BEFORE the server starts (e.g., FMLCommonSetupEvent)
     */
    void registerSkillPrefab(SkillPrefab prefab);

    /**
     * Registers a custom behavior linked to a skill ID
     * When the skill JSON is loaded by the server, it will automatically attach this logic
     * @param id The valid ID of the targeted skill (in lowercase)
     * @param behavior The behavior logic class to attach
     * DISCLAIMER: This method must be called BEFORE the server starts (e.g., FMLCommonSetupEvent)
     */
    void registerSkillBehavior(String id, SkillBehavior behavior);

    /**
     * Triggers the skill activation overlay on the client side
     * Useful for custom events handled outside of standard SkillBehaviors
     * @param id The valid ID of the targeted skill used as ref icon (in lowercase)
     */
    void triggerSkillActivationOverlay(ServerPlayer player, String id);




}
