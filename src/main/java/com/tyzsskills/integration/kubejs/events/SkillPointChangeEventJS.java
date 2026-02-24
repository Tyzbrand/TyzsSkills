package com.tyzsskills.integration.kubejs.events;

import dev.latvian.mods.kubejs.event.KubeEvent;
import net.minecraft.server.level.ServerPlayer;

public class SkillPointChangeEventJS implements KubeEvent {
    private final ServerPlayer player;
    private final int oldAmount;
    private int newAmount;

    private boolean canceled = false;

    public SkillPointChangeEventJS(ServerPlayer player, int oldAmount, int newAmount){
        this.player = player;
        this.oldAmount = oldAmount;
        this.newAmount = newAmount;
    }

    public ServerPlayer getPlayer(){return player;}
    public int getOldAmount(){return oldAmount;}
    public int getNewAmount(){return newAmount;}

    public void setNewAmount(int amount){
        if(amount >= 0) this.newAmount = amount;
    }

    public void cancel(){this.canceled = true;}
    public boolean isCanceled(){return canceled;}
}
