package com.tyzsskills.impl.server.effects.spellEffects;

import com.tyzsskills.api.interfaces.ISpell;
import com.tyzsskills.api.model.SpellBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import org.jetbrains.annotations.NotNull;

public class WirelessEffect extends SpellBehavior {

    @Override
    public boolean onSpellCast(@NotNull ServerPlayer player, @NotNull ISpell spell) {
        player.openMenu(new SimpleMenuProvider(
                (containerId, playerInventory, p) -> ChestMenu.threeRows(containerId, playerInventory, p.getEnderChestInventory()),
                Component.translatable("container.enderchest")
        ));

        return true;
    }
}
