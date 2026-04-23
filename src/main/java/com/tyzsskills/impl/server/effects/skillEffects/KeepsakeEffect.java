package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.SkillBehavior;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.*;

public class KeepsakeEffect extends SkillBehavior {

    private static final Map<UUID, Map<Integer, ItemStack>> SAVED_HOTBARS = new HashMap<>();

    @Override
    public void onPlayerDeath(LivingDeathEvent event, ServerPlayer player, int lvl, ISkill skill) {
        if (player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) return;

        Map<Integer, ItemStack> keptItems = new HashMap<>();

        var values = skill.getValueSet("saved_slots");
        if(values == null) return;

        var slotAmount = (int)values.getValue(lvl);
        slotAmount = Math.max(0, Math.min(41, slotAmount));

        for (int i = 0; i < slotAmount; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty()) {
                keptItems.put(i, stack.copy());
                player.getInventory().setItem(i, ItemStack.EMPTY);
            }
        }

        SAVED_HOTBARS.put(player.getUUID(), keptItems);
    }

    @Override
    public void onPlayerClone(PlayerEvent.Clone event, ServerPlayer player, int lvl, ISkill skill) {
        if (!event.isWasDeath()) return;

        UUID uid = player.getUUID();

        if (SAVED_HOTBARS.containsKey(uid)) {
            Map<Integer, ItemStack> items = SAVED_HOTBARS.remove(uid);

            items.forEach((slotId, stack) -> {
                player.getInventory().setItem(slotId, stack);
            });
        }
    }
}
