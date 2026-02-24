package com.tyzsskills.integration.kubejs.events;

import dev.latvian.mods.kubejs.event.KubeEvent;
import net.minecraft.server.level.ServerPlayer;

public class PlayerResetEventJS implements KubeEvent {
    private final ServerPlayer player;
    public PlayerResetEventJS(ServerPlayer player){
        this.player = player;
    }

    public ServerPlayer getPlayer(){return player;}
}
