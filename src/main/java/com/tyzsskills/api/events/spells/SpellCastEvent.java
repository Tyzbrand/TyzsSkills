package com.tyzsskills.api.events.spells;

import com.tyzsskills.api.interfaces.ISpell;
import com.tyzsskills.api.model.SpellEvent;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.NotNull;

public abstract class SpellCastEvent extends SpellEvent {
    public SpellCastEvent(@NotNull ServerPlayer player, @NotNull ISpell spell) {
        super(player, spell);
    }

    public static class Pre extends SpellCastEvent implements ICancellableEvent{
        public Pre(@NotNull ServerPlayer player, @NotNull ISpell spell) {
            super(player, spell);
        }
    }

    public static class Post extends SpellCastEvent{
        public Post(@NotNull ServerPlayer player, @NotNull ISpell spell) {
            super(player, spell);
        }
    }
}
