package com.tyzsskills.impl.server.wrappers;

import com.tyzsskills.api.interfaces.IXpManager;
import com.tyzsskills.impl.server.xp.XpManager;
import net.minecraft.server.level.ServerPlayer;

public class XpWrapper implements IXpManager {

    @Override
    public void addXP(ServerPlayer player, float amount) {
        XpManager.addXP(player, amount);
    }

    @Override
    public void removeXP(ServerPlayer player, float amount) {
        XpManager.removeXP(player, amount);
    }

    @Override
    public void setXP(ServerPlayer player, float amount) {
        XpManager.setXP(player, amount);
    }

    @Override
    public float getXP(ServerPlayer player) {
        return XpManager.getXP(player);
    }
}
