package com.tyzsskills.api.interfaces;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Interface used to manage player xp.
 * Client Sync is handled automatically when not specified.
 * Client Overlay is handled automatically when not specified.
 * Client Statistics is handled automatically when not specified.
 */
public interface IXpManager {

    /**
     * @return The player's xp.
     */
    float getXp(@NotNull ServerPlayer player);

    /**
     * @param amount Amount of xp to add (must be > {@code 0f}).
     */
    boolean tryAddXp(@NotNull ServerPlayer player, float amount);

    /**
     * @param amount Amount of xp to add (must be > {@code 0f}).
     */
    boolean tryRemoveXp(@NotNull ServerPlayer player, float amount);


    /**
     * @param newAmount New amount to overwrite the current one (must be >= {@code 0})
     * @param triggerOverlay Determines if an overlay will display the gain of the change.
     */
    void setXp(@NotNull ServerPlayer player, float newAmount, boolean triggerOverlay);

    /**
     * Resets the player's xp amount back to {@code 0}.
     * @implNote Client is not synchronized. Player's stats are not affected. Overlay is not affected.
     */
    void resetXp(@NotNull ServerPlayer player);
}
