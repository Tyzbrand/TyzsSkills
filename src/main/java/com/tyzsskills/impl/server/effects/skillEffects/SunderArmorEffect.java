package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.SkillBehavior;
import com.tyzsskills.api.tools.TagMatchTool;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.ArrayList;
import java.util.List;


public class SunderArmorEffect extends SkillBehavior {

    private final EquipmentSlot[] ARMOR_SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    @Override
    public void registerEvent(IEventBus eventBus, ISkill skill) {
        registerAction(
                eventBus,
                EventPriority.LOWEST,
                skill,
                LivingIncomingDamageEvent.class,
                event -> event.getSource().getEntity(),
                this::onPlayerAttack
        );
    }

    private void onPlayerAttack(LivingIncomingDamageEvent event, ServerPlayer player, ISkill skill, int lvl) {
        if(!(event.getEntity() instanceof ServerPlayer target)) return;

        var chances = skill.getValueSet("success_probability");
        var damagePercentages = skill.getValueSet("damage_percentage");
        if(chances == null || damagePercentages == null) return;

        var chance = chances.getValue(lvl);
        var damagePercentage = damagePercentages.getValue(lvl);

        if(player.getRandom().nextFloat() < (chance/100f)) {

            var validSlots = getValidArmorSlots(target);
            if (validSlots.isEmpty()) return;

            float totalArmorDamage = event.getAmount() * damagePercentage / 100f;
            int damagePerPiece = Math.max(1, Math.round(totalArmorDamage / validSlots.size()));

            for (EquipmentSlot slot : validSlots) {
                ItemStack piece = target.getItemBySlot(slot);
                piece.hurtAndBreak(damagePerPiece, target, slot);
            }
            notifyClient(player, skill);
        }
    }

    private List<EquipmentSlot> getValidArmorSlots(ServerPlayer target) {
        var validSlots = new ArrayList<EquipmentSlot>();
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack piece = target.getItemBySlot(slot);
            if (!piece.isEmpty() && piece.isDamageableItem()) {
                validSlots.add(slot);
            }
        }
        return validSlots;
    }
}
