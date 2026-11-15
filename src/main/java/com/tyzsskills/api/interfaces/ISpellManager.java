package com.tyzsskills.api.interfaces;

import com.tyzsskills.api.model.Context;
import com.tyzsskills.api.records.Cooldown;
import com.tyzsskills.api.records.SpellProperty;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.List;
import java.util.Map;

//TODO: javadoc
public interface ISpellManager {

    @NotNull @UnmodifiableView Collection<? extends ISpell> getServerSpells();

    @Nullable ISpell getSpell(@NotNull String spellId);

    default boolean isSpellLoaded(@NotNull String spellId){
        return this.getSpell(spellId) != null;
    }

    boolean isSpellPurchased(@NotNull ServerPlayer player, @NotNull ISpell spell);

    boolean isSpellAssigned(@NotNull ServerPlayer player, @NotNull ISpell spell);

    boolean isSlotFree(@NotNull ServerPlayer player, int slot);

    @NotNull String getSpellInSlot(@NotNull ServerPlayer player, int slot);

    @NotNull @UnmodifiableView Map<String, Integer> getPlayerProperties(@NotNull ServerPlayer player);

    @NotNull @UnmodifiableView List<String> getPlayerSpells(@NotNull ServerPlayer player);

    @NotNull @UnmodifiableView Map<Integer, String> getPlayerSlots(@NotNull ServerPlayer player);

    int getPropertyLevel(@NotNull ServerPlayer player, @NotNull SpellProperty property);

    @Nullable Cooldown getSpellCooldown(@NotNull ServerPlayer player, @NotNull ISpell spell);
    @NotNull @UnmodifiableView Map<String, Cooldown> getPlayerCooldowns(@NotNull ServerPlayer player);

    @NotNull Context.Spell getSpellContext(@NotNull ServerPlayer player, @NotNull ISpell spell);
    @NotNull Context.Property getPropertyContext(@NotNull ServerPlayer player, @NotNull SpellProperty property);
}
