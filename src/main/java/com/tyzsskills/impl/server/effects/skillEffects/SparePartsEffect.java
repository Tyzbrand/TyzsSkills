package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.SkillBehavior;
import com.tyzsskills.api.records.ValueSet;
import com.tyzsskills.api.tools.TagMatchTool;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SparePartsEffect extends SkillBehavior {
    @Override
    public void registerEvent(IEventBus eventBus, String skillId) {
        registerAction(
                eventBus,
                skillId,
                PlayerEvent.ItemCraftedEvent.class,
                PlayerEvent.ItemCraftedEvent::getEntity,
                this::onItemCrafted
        );
    }

    private void onItemCrafted(PlayerEvent.ItemCraftedEvent event, ServerPlayer player, ISkill skill, int lvl) {
        if(player.isCreative()) return;

        var values = skill.getValueSet("refund_chance");
        var maxMaterials = skill.getValueSet("max_materials");
        if (values == null || maxMaterials == null) return;

        var container = event.getInventory();
        var ingredientCount = 0;
        Item firstIngredientType = null;
        var allIdentical = true;

        for (int i = 0; i < container.getContainerSize(); i++) {
            var ingredient = container.getItem(i);

            if (ingredient.isEmpty()) continue;

            ingredientCount++;

            if (firstIngredientType == null) firstIngredientType = ingredient.getItem();
            else if (firstIngredientType != ingredient.getItem()) allIdentical = false;
        }

        if(ingredientCount <= 1) return;
        if ((ingredientCount == 4 || ingredientCount == 9) && allIdentical) return;

        if(TagMatchTool.isItemInList(skill.getSpecificParameters(), "product_blacklist", event.getCrafting())) return;

        var chance = values.getValue(lvl);
        var maxRefunds = (int) maxMaterials.getValue(lvl);
        var refundedCount = 0;

        List<Integer> validSlots = new ArrayList<>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            var ingredient = container.getItem(i);
            if (ingredient.isEmpty() || ingredient.getItem().hasCraftingRemainingItem(ingredient)) continue;
            if(TagMatchTool.isItemInList(skill.getSpecificParameters(), "ingredient_blacklist", ingredient)) continue;
            validSlots.add(i);
        }

        Collections.shuffle(validSlots);

        for (int slot : validSlots) {
            if (refundedCount >= maxRefunds) break;

            if (player.getRandom().nextFloat() < (chance / 100f)) {
                var ingredient = container.getItem(slot);
                var refundedItem = new ItemStack(ingredient.getItem(), 1);

                var drop = new ItemEntity(player.level(),
                        player.getX(),
                        player.getEyeY() - 0.2,
                        player.getZ(),
                        refundedItem
                );

                var look = player.getLookAngle();
                drop.setDeltaMovement(look.x * 0.3, (look.y * 0.3) + 0.1, look.z * 0.3);
                drop.setPickUpDelay(20);
                player.level().addFreshEntity(drop);

                refundedCount++;
            }
        }

        if(refundedCount > 0) notifyClient(player, skill);
    }
}
