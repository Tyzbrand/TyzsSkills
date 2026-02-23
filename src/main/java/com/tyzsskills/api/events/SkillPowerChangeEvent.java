
package com.tyzsskills.api.events;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class SkillPowerChangeEvent extends Event implements ICancellableEvent {
    private final int oldPower;
    private int newPower;
    private final ServerPlayer player;

    public SkillPowerChangeEvent(ServerPlayer player, int oldPower, int newPower){
        this.player = player;
        this.oldPower = oldPower;
        this.newPower = newPower;
    }

    public int getOldPower(){return oldPower;}
    public int getNewPower(){return newPower;}
    public ServerPlayer getPlayer(){return player;}

    public void setNewPower(int power){if(power >= 0) this.newPower = power;}

}
