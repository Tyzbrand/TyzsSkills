package com.tyzsskills.api.events.spells;

import com.tyzsskills.api.interfaces.ISpell;
import com.tyzsskills.api.model.SpellEvent;
import com.tyzsskills.api.records.Cooldown;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.NotNull;

public abstract class SpellCooldownStartEvent extends SpellEvent {
    protected Cooldown cooldown;
    public Cooldown getCooldown() {return cooldown;}


    public SpellCooldownStartEvent(@NotNull ServerPlayer player, @NotNull ISpell spell, @NotNull Cooldown cooldown) {
        super(player, spell);
        this.cooldown = cooldown;
    }

    public static class Pre extends SpellCooldownStartEvent implements ICancellableEvent{
        public Pre(@NotNull ServerPlayer player, @NotNull ISpell spell, @NotNull Cooldown cooldown) {
            super(player, spell, cooldown);
        }

        public void setDuration(long newDuration){
            if(newDuration < 0) return;
            cooldown = new Cooldown(getPlayer().server.overworld().getGameTime() + newDuration, newDuration);
        }
    }

    public static class Post extends SpellCooldownStartEvent{
        public Post(@NotNull ServerPlayer player, @NotNull ISpell spell, @NotNull Cooldown cooldown) {
            super(player, spell, cooldown);
        }
    }
}
