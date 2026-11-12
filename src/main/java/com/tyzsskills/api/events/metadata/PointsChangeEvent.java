package com.tyzsskills.api.events.metadata;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.NotNull;

public class PointsChangeEvent extends Event implements ICancellableEvent {
    private final int oldAmount;
    public int getOldAmount(){return oldAmount;}

    private int newAmount;
    public int getNewAmount(){return newAmount;}
    public void setNewAmount(int amount){if(amount >= 0) this.newAmount = amount;}

    private final ServerPlayer player;
    public @NotNull ServerPlayer getPlayer(){return player;}

    public PointsChangeEvent(@NotNull ServerPlayer player, int oldAmount, int newAmount){
        this.player = player;
        this.oldAmount = oldAmount;
        this.newAmount = newAmount;
    }
}
