package com.tyzsskills.api.events.spells;

import com.tyzsskills.api.interfaces.ISpell;
import com.tyzsskills.api.model.SpellEvent;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.NotNull;

public abstract class SpellPurchaseEvent extends SpellEvent {
    public SpellPurchaseEvent(@NotNull ServerPlayer player, @NotNull ISpell spell) {
        super(player, spell);
    }

    public static class Pre extends SpellPurchaseEvent implements ICancellableEvent{
        public Pre(@NotNull ServerPlayer player, @NotNull ISpell spell) {
            super(player, spell);
        }
    }

    public static class Post extends SpellPurchaseEvent{
        public Post(@NotNull ServerPlayer player, @NotNull ISpell spell) {
            super(player, spell);
        }
    }
}
