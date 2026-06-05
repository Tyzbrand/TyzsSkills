package com.tyzsskills.impl.server.xp;

import com.tyzsskills.api.interfaces.IXpManager;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class XpWrapper implements IXpManager {
    @Override
    public float getXp(@NotNull ServerPlayer player) {return XpManager.getXP(player);}

    @Override
    public boolean tryAddXp(@NotNull ServerPlayer player, float amount) {return XpManager.tryAddXp(player, amount);}

    @Override
    public boolean tryRemoveXp(@NotNull ServerPlayer player, float amount) {return XpManager.tryRemoveXp(player, amount);}

    @Override
    public void setXp(@NotNull ServerPlayer player, float newAmount, boolean triggerOverlay) {XpManager.setXp(player, newAmount, triggerOverlay);}

    @Override
    public void resetXp(@NotNull ServerPlayer player) {XpManager.resetXp(player);}
}
