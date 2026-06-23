package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.SkillBehavior;
import com.tyzsskills.api.tools.TagMatchTool;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.*;

public class KeepsakeEffect extends SkillBehavior {

    private final Map<UUID, Map<Integer, ItemStack>> SAVED_HOTBARS = new HashMap<>();

    @Override
    public void registerEvent(IEventBus eventBus, ISkill skill) {
        registerAction(
                eventBus,
                skill,
                LivingDeathEvent.class,
                LivingDeathEvent::getEntity,
                this::onPlayerDeath
        );

        registerAction(
                eventBus,
                EventPriority.LOWEST,
                skill,
                PlayerEvent.Clone.class,
                PlayerEvent.Clone::getEntity,
                this::onPlayerClone
        );
    }

    private void onPlayerDeath(LivingDeathEvent event, ServerPlayer player, ISkill skill, int lvl) {
        if (player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) return;

        Map<Integer, ItemStack> keptItems = new HashMap<>();

        var values = skill.getValueSet("saved_slots");
        if(values == null) return;

        var slotAmount = (int)values.getValue(lvl);
        slotAmount = Math.clamp(slotAmount, 0, 41);

        for (int i = 0; i < slotAmount; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.isEmpty()) continue;
            if(TagMatchTool.isItemInList(skill.getSpecificParameters(), "item_blacklist", stack)) continue;

            keptItems.put(i, stack.copy());
            player.getInventory().setItem(i, ItemStack.EMPTY);
        }

        SAVED_HOTBARS.put(player.getUUID(), keptItems);
    }

    private void onPlayerClone(PlayerEvent.Clone event, ServerPlayer player, ISkill skill, int lvl) {
        if (!event.isWasDeath()) return;

        UUID uid = player.getUUID();

        if (SAVED_HOTBARS.containsKey(uid)) {
            Map<Integer, ItemStack> items = SAVED_HOTBARS.remove(uid);

            items.forEach((slotId, stack) -> {
                player.getInventory().setItem(slotId, stack);
            });

            notifyClient(player, skill);
        }
    }
}
