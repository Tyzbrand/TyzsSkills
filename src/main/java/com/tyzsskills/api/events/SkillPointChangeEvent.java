package com.tyzsskills.api.events;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class SkillPointChangeEvent extends Event implements ICancellableEvent {
    private final int oldAmount;
    private int newAmount;
    private final ServerPlayer player;

    public SkillPointChangeEvent(ServerPlayer player, int oldAmount, int newAmount){
        this.player = player;
        this.oldAmount = oldAmount;
        this.newAmount = newAmount;
    }

    public int getOldAmount(){return oldAmount;}
    public int getNewAmount(){return newAmount;}
    public ServerPlayer getPlayer(){return player;}

    public void setNewAmount(int amount){if(amount >= 0) this.newAmount = amount;}

}
