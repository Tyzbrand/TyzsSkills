package com.tyzsskills.api.model;

import com.tyzsskills.api.interfaces.ISpell;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.NotNull;

public abstract class SpellEvent extends Event {
    private final ServerPlayer player;
    public @NotNull ServerPlayer getPlayer(){return player;}

    private final ISpell spell;
    public @NotNull ISpell getSpell(){return spell;}

    public SpellEvent(@NotNull ServerPlayer player, @NotNull ISpell spell){
        this.player = player;
        this.spell = spell;
    }
}
