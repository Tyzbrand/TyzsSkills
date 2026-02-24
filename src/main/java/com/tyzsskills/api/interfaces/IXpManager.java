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
     * NOTE: This will trigger client overlay
     */
    void addXP(ServerPlayer player, float amount);

    /**
     * @param amount amount to add (must be > 0f)
     * @param showOverlay if true, the visual xp gain overlay will be triggered on client
     * @param applyLimits if true, the addition will be affected by limits
     */
    void addXP(ServerPlayer player, float amount, boolean showOverlay, boolean applyLimits);



    /**
     * @param amount amount to remove (works if the player can afford, must be > 0f)
     * NOTE: The player skill xp can't go below 0
     */
    void removeXP(ServerPlayer player, float amount);

    /**
     * @param amount amount to set (must be >= 0)
     */
    void setXP(ServerPlayer player, float amount);

    /**
     * @param amount amount to set (must be >= 0)
     * @param applyLimits if true, the change will be affected by limits
     */
    void setXP(ServerPlayer player, float amount, boolean applyLimits);
}
