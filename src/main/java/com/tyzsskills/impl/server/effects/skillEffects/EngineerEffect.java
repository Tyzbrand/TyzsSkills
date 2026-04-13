package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.impl.server.model.SkillBehavior;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class EngineerEffect extends SkillBehavior {
    @Override
    public void onItemCrafted(PlayerEvent.ItemCraftedEvent event, ServerPlayer player, int lvl, ISkill skill) {
        var values = skill.getValueSet("refund_chance");
        if (values == null) return;

        var chance = values.getValue(lvl);

        var container = event.getInventory();
        var refundedSomething = false;

        for(int i = 0; i < container.getContainerSize(); i++){
            var ingredient = container.getItem(i);

            if(ingredient.isEmpty()) continue;
            if(ingredient.getItem().hasCraftingRemainingItem(ingredient)) continue;

            if (player.getRandom().nextFloat() < (chance / 100f)) {
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

                refundedSomething = true;
            }
        }

        if(refundedSomething) notifyClient(player, skill);
    }
}
