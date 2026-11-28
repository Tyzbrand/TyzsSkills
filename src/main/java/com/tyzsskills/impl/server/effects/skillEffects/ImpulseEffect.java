package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.SkillBehavior;
import com.tyzsskills.impl.client.active.TagMatchManager;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class ImpulseEffect extends SkillBehavior {
    @Override
    public void registerEvent(IEventBus eventBus, ISkill skill) {
        registerAction(
                eventBus,
                skill,
                PlayerInteractEvent.RightClickItem.class,
                PlayerInteractEvent.RightClickItem::getEntity,
                this::onItemFinish
        );
    }

    private void onItemFinish( PlayerInteractEvent.RightClickItem event, ServerPlayer player, ISkill skill, int lvl){
        if(player.isCrouching()) return;
        var itemStack = event.getItemStack();
        if(!(itemStack.getItem() instanceof CrossbowItem) || !CrossbowItem.isCharged(itemStack)) return;

        var speedValues = skill.getValueSet("repulse_speed");
        var durabilityValues = skill.getValueSet("durability_penalty");

        if(speedValues == null) {
            registerMissingKey(player, skill, "repulse_speed");
            return;
        }

        if(durabilityValues == null){
            registerMissingKey(player, skill, "durability_penalty");
            return;
        }

        var rawSpeed = speedValues.getValue(lvl);
        var rawDurability = durabilityValues.getValue(lvl);

        if(TagMatchManager.isItemInList(skill.getSpecificParameters(), "item_blacklist", itemStack)) return;

        var server = player.getServer();
        if(server == null) return;


        var currentVelocity = player.getDeltaMovement().scale(0.33);
        var recoilVelocity = player.getLookAngle().scale(-rawSpeed);

        var finalVelocity = recoilVelocity.add(currentVelocity);
        if (finalVelocity.length() > 3.85) finalVelocity = finalVelocity.normalize().scale(3.85);


        player.setDeltaMovement(finalVelocity);
        player.hurtMarked = true;


        var durabilityPenalty = Math.abs(Math.round(rawDurability));
        if(durabilityPenalty > 0){
            var slot = LivingEntity.getSlotForHand(event.getHand());
            if (!itemStack.isEmpty() && player.isAlive()){
                server.tell(new TickTask(server.getTickCount(), () -> itemStack.hurtAndBreak(durabilityPenalty, player, slot)));
            }
        }


        notifyClient(player, skill);
    }
}
