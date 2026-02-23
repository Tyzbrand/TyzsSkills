package com.tyzsskills.api.events;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.Event;

public class PlayerResetEvent extends Event {
    private final ServerPlayer player;
    public PlayerResetEvent(ServerPlayer player){
        this.player = player;
    }

    public ServerPlayer getPlayer(){return player;}
}
