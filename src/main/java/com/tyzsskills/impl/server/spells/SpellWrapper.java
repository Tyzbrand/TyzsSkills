package com.tyzsskills.impl.server.spells;

import com.tyzsskills.api.interfaces.ISpell;
import com.tyzsskills.api.interfaces.ISpellManager;
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

public class SpellWrapper implements ISpellManager {
    @Override
    public @NotNull @UnmodifiableView Collection<? extends ISpell> getServerSpells() {
        return SpellManager.getServerSpells();
    }

    @Override
    public @Nullable ISpell getSpell(@NotNull String spellId){
        return SpellManager.getSpell(spellId);
    }

    @Override
    public boolean isSpellPurchased(@NotNull ServerPlayer player, @NotNull ISpell spell){
        return SpellManager.isSpellPurchased(player, spell);
    }

    @Override
    public boolean isSpellAssigned(@NotNull ServerPlayer player, @NotNull ISpell spell){
        return SpellManager.isSpellAssigned(player, spell);
    }

    @Override
    public boolean isSlotFree(@NotNull ServerPlayer player, int slot){
        return SpellManager.isSlotFree(player, slot);
    }

    @Override
    public @NotNull String getSpellInSlot(@NotNull ServerPlayer player, int slot){
        return SpellManager.getSpellInSlot(player, slot);
    }

    @Override
    public int getMaxSlots(@NotNull ServerPlayer player){
        return SpellManager.getMaxSlots(player);
    }

    @Override
    public @NotNull @UnmodifiableView Map<String, Integer> getPlayerProperties(@NotNull ServerPlayer player){
        return SpellManager.getPurchasedProperties(player);
    }

    @Override
    @NotNull @UnmodifiableView
    public List<String> getPlayerSpells(@NotNull ServerPlayer player){
        return SpellManager.getPurchasedSpells(player);
    }

    @Override
    public @NotNull @UnmodifiableView Map<Integer, String> getPlayerSlots(@NotNull ServerPlayer player){
        return SpellManager.getPlayerSlots(player);
    }

    @Override
    public int getPropertyLevel(@NotNull ServerPlayer player, @NotNull SpellProperty property){
        return SpellManager.getPropertyLevel(player, property);
    }

    @Override
    public @Nullable Cooldown getSpellCooldown(@NotNull ServerPlayer player, @NotNull ISpell spell){
        return SpellManager.getSpellCooldown(player, spell);
    }

    @Override
    public @NotNull @UnmodifiableView Map<String, Cooldown> getPlayerCooldowns(@NotNull ServerPlayer player){
        return SpellManager.getActiveCooldowns(player);
    }

    @Override
    public @NotNull Context.Spell getSpellContext(@NotNull ServerPlayer player, @NotNull ISpell spell){
        return SpellManager.getSpellContext(player, spell);
    }

    @Override
    public @NotNull Context.Property getPropertyContext(@NotNull ServerPlayer player, @NotNull SpellProperty property){
        return SpellManager.getPropertyContext(player, property);
    }

}
