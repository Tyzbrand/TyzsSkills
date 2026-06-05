package com.tyzsskills.impl.server.Level;

import com.tyzsskills.api.interfaces.ILevelManager;
import com.tyzsskills.api.records.LevelData;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class LevelWrapper implements ILevelManager {

    @Override
    public int getLevel(@NotNull ServerPlayer player) {return LevelManager.getLevel(player);}

    @Override
    public boolean tryAddLevel(@NotNull ServerPlayer player, int amount) {return LevelManager.tryAddLevel(player, amount);}

    @Override
    public boolean tryRemoveLevel(@NotNull ServerPlayer player, int amount) {return LevelManager.tryRemoveLevel(player, amount);}

    @Override
    public void setLevel(@NotNull ServerPlayer player, int newLevel) {LevelManager.setLevel(player, newLevel);}

    @Override
    public void resetLevel(@NotNull ServerPlayer player) {LevelManager.resetLevel(player);}

    @Override
    public @NotNull LevelData getLevelData(int level) {return LevelManager.getLevelData(level);}
}
