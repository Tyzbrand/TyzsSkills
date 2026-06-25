package com.tyzsskills.api.model;

import com.tyzsskills.api.interfaces.ISpell;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public abstract class SpellBehavior {
    public abstract boolean onSpellCast(@NotNull ServerPlayer player, @NotNull ISpell spell);
}
