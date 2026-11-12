package com.tyzsskills.api.events.spells;

import com.tyzsskills.api.interfaces.ISpell;
import com.tyzsskills.api.model.SpellEvent;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.NotNull;

public abstract class SpellUnassignEvent extends SpellEvent {
    private final int slot;
    public int getSlot() {return slot;}

    public SpellUnassignEvent(@NotNull ServerPlayer player, @NotNull ISpell spell, int slot) {
        super(player, spell);
        this.slot = slot;
    }

    public static class Pre extends SpellUnassignEvent implements ICancellableEvent{
        public Pre(@NotNull ServerPlayer player, @NotNull ISpell spell, int slot) {
            super(player, spell, slot);
        }
    }

    public static class Post extends SpellUnassignEvent{
        public Post(@NotNull ServerPlayer player, @NotNull ISpell spell, int slot) {
            super(player, spell, slot);
        }
    }
}
