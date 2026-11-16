package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.SkillBehavior;
import com.tyzsskills.impl.client.active.TagMatchManager;
import net.minecraft.server.level.ServerPlayer;
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
        var values = skill.getValueSet("repulse_power");
        if(values == null) return;
        var value = values.getValue(lvl);

        var itemStack = event.getItemStack();
        var parameters = skill.getSpecificParameters();

        if(!(itemStack.getItem() instanceof CrossbowItem) || !CrossbowItem.isCharged(itemStack)) return;
        if(!parameters.getBoolean("works_in_creative") && player.isCreative()) return;
        if(parameters.getBoolean("sneak_to_avoid") && player.isCrouching()) return;
        if(TagMatchManager.isItemInList(parameters, "item_blacklist", itemStack)) return;

        var speed = 1 + value/100f;
        var recoil = player.getLookAngle().scale(-speed);
        var velocity = player.getDeltaMovement().scale(0.33);

        player.setDeltaMovement(velocity.add(recoil));
        player.hurtMarked = true;
        notifyClient(player, skill);
    }
}
