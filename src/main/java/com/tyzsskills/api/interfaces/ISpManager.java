package com.tyzsskills.api.interfaces;

import net.minecraft.server.level.ServerPlayer;

/**
 * Interface used to manage player skill points (SP)
 * Client sync is handled automatically
 */
public interface ISpManager {

    /**
     * @return current SP amount
     */
    int getSP(ServerPlayer player);

    /**
     * @param amount amount to add (must be > 0)
     */
    void addSP(ServerPlayer player, int amount);

    /**
     * @param amount amount to add (must be > 0)
     * @param applyLimits if true, the addition will be affected by limits
     */
    void addSP(ServerPlayer player, int amount, boolean applyLimits);

    /**
     * @param amount amount to withdraw (works if the player can afford, must be > 0)
     * NOTE: The player sp can't go below 0
     */
    void removeSP(ServerPlayer player, int amount);

    /**
     * @param amount amount to set (must be >= 0)
     */
    void setSP(ServerPlayer player, int amount);


    /**
     * @param amount amount to set (must be >= 0)
     * @param applyLimits if true, the change will be affected by limits
     */
    void setSP(ServerPlayer player, int amount, boolean applyLimits);
}
