package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.SkillBehavior;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Enemy;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class StealthEffect extends SkillBehavior {


    @Override
    public void registerEvent(IEventBus eventBus, ISkill skill) {
        registerAction(
                eventBus,
                skill,
                LivingEvent.LivingVisibilityEvent.class,
                LivingEvent.LivingVisibilityEvent::getEntity,
                this::onLivingVisibility
        );
    }

    private void onLivingVisibility(LivingEvent.LivingVisibilityEvent event, ServerPlayer player, ISkill skill, int lvl) {

        Entity observer = event.getLookingEntity();
        if (!(observer instanceof Enemy)) return;

        var values = skill.getValueSet("range_reduction");
        if(values == null) return;

        float reductionPercent = values.getValue(lvl);
        double multiplier = 1.0 - (reductionPercent / 100.0);

        event.modifyVisibility(multiplier);
    }

}
