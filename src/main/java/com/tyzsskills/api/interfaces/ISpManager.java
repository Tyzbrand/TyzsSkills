package com.tyzsskills.api.interfaces;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Interface used to manage player skill points (SP).
 * Client sync is handled automatically when not specified.
 */
public interface ISpManager {

    /**
     * @return current SP amount
     */
    int getSp(@NotNull ServerPlayer player);

    /**
     * @param amount Amount to add (must be > {@code 0}).
     * @return {@code true} if the addition is successful, {@code false} otherwise.
     */
    boolean tryAddSp(@NotNull ServerPlayer player, int amount);


    /**
     * @param amount Amount to withdraw (works if the player can afford, must be > {@code 0}).
     * @return {@code true} if the withdrawal is successful, {@code false} otherwise.
     * @implNote The player sp can't go below {@code 0}.
     */
    boolean tryRemoveSp(@NotNull ServerPlayer player, int amount);

    /**
     * @param newAmount New amount to overwrite the current one (must be >= {@code 0}).
     * @implNote Player's stats are not affected.
     */
    void setSp(@NotNull ServerPlayer player, int newAmount);

    /**
     * Resets the player's sp amount back to {@code 0}.
     * @implNote Client is not synchronized. Player's stats are not affected.
     */
    void resetSp(@NotNull ServerPlayer player);


}
