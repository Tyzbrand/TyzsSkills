package com.tyzsskills.api.events.spells;

import com.tyzsskills.api.interfaces.ISpell;
import com.tyzsskills.api.model.SpellEvent;
import com.tyzsskills.api.records.SpellProperty;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.NotNull;

public abstract class SpellPropertyPurchaseEvent extends SpellEvent {
    private final SpellProperty property;
    public SpellProperty getProperty() {return property;}

    private final int oldLevel;
    public int getOldLevel() {return oldLevel;}

    private final int newLevel;
    public int getNewLevel() {return newLevel;}


    public SpellPropertyPurchaseEvent(@NotNull ServerPlayer player, @NotNull ISpell spell, SpellProperty property, int oldLevel, int newLevel) {
        super(player, spell);

        this.property = property;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }

    public static class Pre extends SpellPropertyPurchaseEvent implements ICancellableEvent{
        public Pre(@NotNull ServerPlayer player, @NotNull ISpell spell, SpellProperty property, int oldLevel, int newLevel) {
            super(player, spell, property, oldLevel, newLevel);
        }
    }

    public static class Post extends SpellPropertyPurchaseEvent{
        public Post(@NotNull ServerPlayer player, @NotNull ISpell spell, SpellProperty property, int oldLevel, int newLevel) {
            super(player, spell, property, oldLevel, newLevel);
        }
    }
}
