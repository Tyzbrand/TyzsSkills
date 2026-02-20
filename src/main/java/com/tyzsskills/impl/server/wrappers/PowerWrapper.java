package com.tyzsskills.impl.server.wrappers;

import com.tyzsskills.api.interfaces.IPowerManager;
import com.tyzsskills.impl.server.power.PowerManager;
import net.minecraft.server.level.ServerPlayer;

public class PowerWrapper implements IPowerManager {

    @Override
    public void addPower(ServerPlayer player, int amount) {
        PowerManager.AddPower(player, amount);
    }

    @Override
    public void removePower(ServerPlayer player, int amount) {
        PowerManager.RemovePower(player, amount);
    }

    @Override
    public void setPower(ServerPlayer player, int amount) {
        PowerManager.SetPower(player, amount);
    }

    @Override
    public int getPower(ServerPlayer player) {
        return PowerManager.GetPower(player);
    }
}
