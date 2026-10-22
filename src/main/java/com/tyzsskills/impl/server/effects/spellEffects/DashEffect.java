package com.tyzsskills.impl.server.effects.spellEffects;

import com.tyzsskills.api.interfaces.ISpell;
import com.tyzsskills.api.model.SpellBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class DashEffect extends SpellBehavior {

    @Override
    public boolean onSpellCast(@NotNull ServerPlayer player, @NotNull ISpell spell) {
        player.displayClientMessage(Component.literal("WORKING"), false);

        return true;
    }
}
