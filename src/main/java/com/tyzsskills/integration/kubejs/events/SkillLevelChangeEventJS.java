package com.tyzsskills.integration.kubejs.events;

import dev.latvian.mods.kubejs.event.KubeEvent;
import net.minecraft.server.level.ServerPlayer;

public class SkillLevelChangeEventJS implements KubeEvent {
    private final int oldLevel;
    private int newLevel;
    private final ServerPlayer player;

    private boolean canceled = false;

    public SkillLevelChangeEventJS(ServerPlayer player, int oldLevel, int newLevel){
        this.player = player;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }

    public int getOldLevel(){return oldLevel;}
    public int getNewLevel(){return newLevel;}
    public ServerPlayer getPlayer(){return player;}

    public void setNewLevel(int level){if(level >= 1) this.newLevel = level;}

    public void cancel(){this.canceled = true;}
    public boolean isCanceled(){return canceled;}
}
