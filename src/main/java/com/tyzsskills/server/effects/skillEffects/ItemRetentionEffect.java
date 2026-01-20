package com.tyzsskills.server.effects.skillEffects;

import com.tyzsskills.server.model.Skill;
import com.tyzsskills.server.model.SkillBehaviour;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.*;

public class ItemRetentionEffect extends SkillBehaviour {

    private static final Map<UUID, Map<Integer, ItemStack>> SAVED_HOTBARS = new HashMap<>();
    private static final Map<UUID, Float> SAVED_XP_PROGRESS = new HashMap<>();
    private static final Map<UUID, Integer> SAVED_XP_LEVEL = new HashMap<>();

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

        ItemStack offhand = player.getOffhandItem();
        if (!offhand.isEmpty()) {
            keptItems.put(999, offhand.copy()); // 999 = Code pour Offhand
            player.getInventory().offhand.set(0, ItemStack.EMPTY);
        }

        SAVED_HOTBARS.put(player.getUUID(), keptItems);

        SAVED_XP_LEVEL.put(player.getUUID(), player.experienceLevel);
        SAVED_XP_PROGRESS.put(player.getUUID(), player.experienceProgress);

        player.experienceLevel = 0;
        player.experienceProgress = 0;
        player.totalExperience = 0;

    }

    @Override
    public void onPlayerClone(PlayerEvent.Clone event, ServerPlayer player, int lvl, Skill skill) {
        if (!event.isWasDeath()) return;

        UUID uid = player.getUUID();

        if (SAVED_HOTBARS.containsKey(uid)) {
            Map<Integer, ItemStack> items = SAVED_HOTBARS.remove(uid);

            items.forEach((slotId, stack) -> {
                if (slotId == 999) {
                    player.getInventory().offhand.set(0, stack);
                } else {
                    player.getInventory().setItem(slotId, stack);
                }
            });
        }

        if (SAVED_XP_LEVEL.containsKey(uid)) {
            player.experienceLevel = SAVED_XP_LEVEL.remove(uid);
            player.experienceProgress = SAVED_XP_PROGRESS.remove(uid);
    }
        }
}
