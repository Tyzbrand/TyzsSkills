package com.tyzsskills.impl.server.wrappers;

import com.tyzsskills.api.interfaces.ISpManager;
import com.tyzsskills.impl.server.sp.SpManager;
import net.minecraft.server.level.ServerPlayer;

public class SpWrapper implements ISpManager {

    @Override
    public void addSP(ServerPlayer player, int amount) {
        SpManager.addSP(player, amount);
    }

    @Override
    public void removeSP(ServerPlayer player, int amount) {
        SpManager.removeSP(player, amount);
    }

    @Override
    public void setSP(ServerPlayer player, int amount) {
        SpManager.setSp(player, amount);
    }

    @Override
    public int getSP(ServerPlayer player) {
        return SpManager.getSP(player);
    }
}
