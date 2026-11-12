package com.tyzsskills.api.events.metadata;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.NotNull;

public class XpChangeEvent extends Event implements ICancellableEvent {
    private final float oldAmount;
    public float getOldAmount(){return oldAmount;}

    private float newAmount;
    public float getNewAmount(){return newAmount;}
    public void setNewAmount(float amount){if(amount >= 0) this.newAmount = amount;}

    private final ServerPlayer player;
    public @NotNull ServerPlayer getPlayer(){return player;}

    public XpChangeEvent(@NotNull ServerPlayer player, float oldAmount, float newAmount){
        this.player = player;
        this.oldAmount = oldAmount;
        this.newAmount = newAmount;
    }
}
