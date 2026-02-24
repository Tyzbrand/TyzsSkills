package com.tyzsskills.impl.server.wrappers;

import com.tyzsskills.api.interfaces.ILevelManager;
import com.tyzsskills.impl.server.Level.LevelManager;
import net.minecraft.server.level.ServerPlayer;

public class LevelWrapper implements ILevelManager {

    @Override
    public void addLevel(ServerPlayer player, int amount) {
        LevelManager.addLevel(player, amount);
    }

    @Override
    public void addLevel(ServerPlayer player, int amount, boolean applyLimits) {
        LevelManager.addLevel(player, amount, applyLimits);
    }

    @Override
    public void removeLevel(ServerPlayer player, int amount) {
        LevelManager.removeLevel(player, amount);
    }

    @Override
    public void setLevel(ServerPlayer player, int amount) {
        LevelManager.setLevel(player, amount);
    }

    @Override
    public void setLevel(ServerPlayer player, int amount, boolean applyLimits) {
        LevelManager.setLevel(player, amount, applyLimits);
    }

    @Override
    public int getLevel(ServerPlayer player) {
        return LevelManager.getLevel(player);
    }
}
