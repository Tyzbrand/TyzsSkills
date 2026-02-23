package com.tyzsskills.api.events;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class SkillXPChangeEvent extends Event implements ICancellableEvent {
    private final float oldAmount;
    private float newAmount;
    private final ServerPlayer player;

    public SkillXPChangeEvent(ServerPlayer player, float oldAmount, float newAmount){
        this.player = player;
        this.oldAmount = oldAmount;
        this.newAmount = newAmount;
    }

    public float getOldAmount(){return oldAmount;}
    public float getNewAmount(){return newAmount;}
    public ServerPlayer getPlayer(){return player;}

    public void setNewAmount(float amount){if(amount >= 0) this.newAmount = amount;}

}
