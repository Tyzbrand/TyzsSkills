package com.tyzsskills.api.interfaces;

import net.minecraft.server.level.ServerPlayer;

/**
 * Interface used to manage player's power
 * Client sync is handled automatically
 * NOTE: Power is handled automatically when a trait level is modified
 */
public interface IPowerManager {

    /**
     * @return current power
     */
    int getPower(ServerPlayer player);

    /**
     * @param amount amount to add (must be > 0)
     */
    void addPower(ServerPlayer player, int amount);

    /**
     * @param amount amount to withdraw (works if the player can afford, must be > 0)
     * NOTE: The player's power can't go below 0
     */
    void removePower(ServerPlayer player, int amount);

    /**
     * @param amount amount to set (must be >= 0)
     */
    void setPower(ServerPlayer player, int amount);
}

