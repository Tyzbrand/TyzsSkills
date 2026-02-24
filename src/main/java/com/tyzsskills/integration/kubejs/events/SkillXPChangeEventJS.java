package com.tyzsskills.integration.kubejs.events;

import dev.latvian.mods.kubejs.event.KubeEvent;
import net.minecraft.server.level.ServerPlayer;

public class SkillXPChangeEventJS implements KubeEvent {
    private final float oldAmount;
    private float newAmount;
    private final ServerPlayer player;

    private boolean canceled = false;

    public SkillXPChangeEventJS(ServerPlayer player, float oldAmount, float newAmount){
        this.player = player;
        this.oldAmount = oldAmount;
        this.newAmount = newAmount;
    }

    public float getOldAmount(){return oldAmount;}
    public float getNewAmount(){return newAmount;}
    public ServerPlayer getPlayer(){return player;}

    public void setNewAmount(float amount){if(amount >= 0) this.newAmount = amount;}

    public void cancel(){this.canceled = true;}
    public boolean isCanceled(){return canceled;}
}
