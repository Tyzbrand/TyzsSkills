package com.tyzsskills.api.events.spells;

import com.tyzsskills.api.interfaces.ISpell;
import com.tyzsskills.api.model.SpellEvent;
import com.tyzsskills.api.records.SpellProperty;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class SpellPropertyLevelChangeEvent extends SpellEvent {
    private final SpellProperty property;
    public SpellProperty getProperty() {return property;}

    private final int oldLevel;
    public int getOldLevel() {return oldLevel;}

    private final int newLevel;
    public int getNewLevel() {return newLevel;}

    public SpellPropertyLevelChangeEvent(@NotNull ServerPlayer player, @NotNull ISpell spell, SpellProperty property, int oldLevel, int newLevel) {
        super(player, spell);
        this.property = property;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }
}
