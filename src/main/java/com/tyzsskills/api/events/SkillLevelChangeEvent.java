package com.tyzsskills.api.events;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class SkillLevelChangeEvent extends Event implements ICancellableEvent {
    private final int oldLevel;
    private int newLevel;
    private final ServerPlayer player;

    public SkillLevelChangeEvent(ServerPlayer player, int oldLevel, int newLevel){
        this.player = player;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }

    public int getOldLevel(){return oldLevel;}
    public int getNewLevel(){return newLevel;}
    public ServerPlayer getPlayer(){return player;}

    public void setNewLevel(int level){if(level >= 1) this.newLevel = level;}

}
