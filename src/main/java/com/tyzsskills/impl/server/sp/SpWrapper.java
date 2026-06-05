package com.tyzsskills.impl.server.sp;

import com.tyzsskills.api.interfaces.ISpManager;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class SpWrapper implements ISpManager {

    @Override
    public int getSp(@NotNull ServerPlayer player) {return SpManager.getSP(player);}

    @Override
    public boolean tryAddSp(@NotNull ServerPlayer player, int amount) {return SpManager.tryAddSp(player, amount);}

    @Override
    public boolean tryRemoveSp(@NotNull ServerPlayer player, int amount) {return SpManager.tryRemoveSp(player, amount);}

    @Override
    public void setSp(@NotNull ServerPlayer player, int newAmount) {SpManager.setSp(player, newAmount);}

    @Override
    public void resetSp(@NotNull ServerPlayer player) {SpManager.resetSp(player);}
}
