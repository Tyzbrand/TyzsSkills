package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.SkillBehavior;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class ResistanceEffect  extends SkillBehavior {

    @Override
    public void registerEvent(IEventBus eventBus, ISkill skill) {
        registerAction(
                eventBus,
                EventPriority.LOW,
                skill,
                LivingIncomingDamageEvent.class,
                LivingIncomingDamageEvent::getEntity,
                this::onIncomingDamage
        );
    }

    private void onIncomingDamage(LivingIncomingDamageEvent event, ServerPlayer player, ISkill skill, int lvl) {
        var values = skill.getValueSet("damage_resistance");
        if(values == null) return;

        var damageSource = event.getSource();
        if(damageSource.is(DamageTypeTags.BYPASSES_RESISTANCE)) return;


        float value = values.getValue(lvl);
        if(value >= 100f){
            event.setCanceled(true);
            return;
        }

        float damageAbsorption = 1f - (value / 100f);
        float newDmg = event.getAmount() * damageAbsorption;

        event.setAmount(newDmg);
    }
}
