package com.tyzsskills.api.interfaces;

import net.minecraft.server.level.ServerPlayer;

/**
 * Interface used to manage player skill level
 * Client sync is handled automatically
 */
public interface ILevelManager {

    /**
     * @return current player skill level
     */
    int getLevel(ServerPlayer player);

    /**
     * @param amount amount to add (must be > 0)
     */
    void addLevel(ServerPlayer player, int amount);

    /**
     * @param amount amount to withdraw (works if the player can afford, must be > 0)
     * NOTE: the player skill level can't go below 1
     */
    void removeLevel(ServerPlayer player, int amount);

    /**
     * @param amount amount to set (must be >= 1)
     */
    void setLevel(ServerPlayer player, int amount);
}
