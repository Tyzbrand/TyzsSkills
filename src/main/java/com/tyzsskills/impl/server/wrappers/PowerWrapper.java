package com.tyzsskills.impl.server.wrappers;

import com.tyzsskills.api.interfaces.IPowerManager;
import com.tyzsskills.impl.server.power.PowerManager;
import net.minecraft.server.level.ServerPlayer;

public class PowerWrapper implements IPowerManager {

    @Override
    public void addPower(ServerPlayer player, int amount) {
        PowerManager.addPower(player, amount);
    }

    @Override
    public void removePower(ServerPlayer player, int amount) {
        PowerManager.removePower(player, amount);
    }

    @Override
    public void setPower(ServerPlayer player, int amount) {
        PowerManager.setPower(player, amount);
    }

    @Override
    public int getPower(ServerPlayer player) {
        return PowerManager.getPower(player);
    }
}
