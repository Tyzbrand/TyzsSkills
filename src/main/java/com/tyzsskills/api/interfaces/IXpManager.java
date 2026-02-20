package com.tyzsskills.api.interfaces;

import net.minecraft.server.level.ServerPlayer;

/**
 * Interface used to manage player skill xp
 * Client sync is handled automatically
 */
public interface IXpManager {

    /**
     * @return current player skill xp
     */
    float getXP(ServerPlayer player);

    /**
     * @param amount amount to add (must be > 0f)
     */
    void addXP(ServerPlayer player, float amount);

    /**
     * @param amount amount to remove (works if the player can afford, must be > 0f)
     * NOTE: The player skill xp can't go below 0
     */
    void removeXP(ServerPlayer player, float amount);

    /**
     * @param amount amount to set (must be >= 0)
     */
    void setXP(ServerPlayer player, float amount);
}
