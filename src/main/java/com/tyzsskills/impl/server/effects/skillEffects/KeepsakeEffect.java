package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.impl.server.model.Skill;
import com.tyzsskills.impl.server.model.SkillBehaviour;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.*;

public class KeepsakeEffect extends SkillBehaviour {

    private static final Map<UUID, Map<Integer, ItemStack>> SAVED_HOTBARS = new HashMap<>();

    @Override
    public void onPlayerDeath(LivingDeathEvent event, ServerPlayer player, int lvl, Skill skill) {
        if (player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) return;


        Map<Integer, ItemStack> keptItems = new HashMap<>();

        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty()) {
                keptItems.put(i, stack.copy());
                player.getInventory().setItem(i, ItemStack.EMPTY);
            }
        }


        SAVED_HOTBARS.put(player.getUUID(), keptItems);
    }

    @Override
    public void onPlayerClone(PlayerEvent.Clone event, ServerPlayer player, int lvl, Skill skill) {
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
