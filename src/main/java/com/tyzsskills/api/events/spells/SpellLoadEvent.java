package com.tyzsskills.api.events.spells;

import com.tyzsskills.api.interfaces.ISpell;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.NotNull;

public abstract class SpellLoadEvent extends Event {
    private final ISpell spell;
    public @NotNull ISpell getSpell(){return spell;}

    private SpellLoadEvent(@NotNull ISpell spell){
        this.spell = spell;
    }

    public static class Pre extends SpellLoadEvent implements ICancellableEvent {
        public Pre(@NotNull ISpell spell){
            super(spell);
        }
    }

    public static class Post extends SpellLoadEvent{
        public Post(@NotNull ISpell spell){
            super(spell);
        }
    }
}
