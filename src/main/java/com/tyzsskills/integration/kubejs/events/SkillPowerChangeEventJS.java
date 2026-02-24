package com.tyzsskills.integration.kubejs.events;

import dev.latvian.mods.kubejs.event.KubeEvent;
import net.minecraft.server.level.ServerPlayer;

public class SkillPowerChangeEventJS implements KubeEvent {
    private final int oldPower;
    private int newPower;
    private final ServerPlayer player;

    private boolean canceled = false;

    public SkillPowerChangeEventJS(ServerPlayer player, int oldPower, int newPower){
        this.player = player;
        this.oldPower = oldPower;
        this.newPower = newPower;
    }

    public int getOldPower(){return oldPower;}
    public int getNewPower(){return newPower;}
    public ServerPlayer getPlayer(){return player;}

    public void setNewPower(int power){if(power >= 0) this.newPower = power;}

    public void cancel(){this.canceled = true;}
    public boolean isCanceled(){return canceled;}
}
