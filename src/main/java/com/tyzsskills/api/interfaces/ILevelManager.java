package com.tyzsskills.api.interfaces;

import com.tyzsskills.api.records.LevelData;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Interface used to manage player level.
 * Client sync is handled automatically when not specified.
 */
public interface ILevelManager {

    /**
     * @return Current player level.
     */
    int getLevel(@NotNull ServerPlayer player);

    /**
     * @param amount Amount of levels to add (must be > {@code 0}).
     * @return {@code true} if the addition is successful, {@code false} otherwise.
     */
    boolean tryAddLevel(@NotNull ServerPlayer player, int amount);


    /**
     * @param amount Amount of levels to withdraw (works if the player can afford, must be > {@code 0}).
     * @return {@code true} if the withdrawal is successful, {@code false} otherwise.
     * @implNote The player's level can't go below {@code 1}.
     */
    boolean tryRemoveLevel(@NotNull ServerPlayer player, int amount);

    /**
     * @param newLevel New level to overwrite the current one (must be >= {@code 1}).
     */
    void setLevel(@NotNull ServerPlayer player, int newLevel);

    /**
     * Resets the player's level back to {@code 1}.
     * @implNote Client is not synchronized.
     */
    void resetLevel(@NotNull ServerPlayer player);

    /**
     * Lets you access to the current player's level data (xp goal and sp reward).
     * @return {@link LevelData} for the current player's level.
     */
    default @NotNull LevelData getCurrentLevelData(@NotNull ServerPlayer player){return getLevelData(getLevel(player));}

    /**
     * Lets you access to the specified level data (xp goal and sp reward).
     * @return {@link LevelData} for the specified level.
     */
    @NotNull LevelData getLevelData(int level);

    /**
     * @return The xp goal for the specified level.
     * @implNote Corresponds to the amount of xp needed to complete the specified level.
     */
    default float getXpGoal(int level){return getLevelData(level).goal();}

    /**
     * @return The sp reward for completing the specified level.
     * @implNote Corresponds to the rewards won when the specified level is completed.
     */
    default int getSpReward(int level){return getLevelData(level).reward();}

}
