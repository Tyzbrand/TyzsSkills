package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.TyzsSkillsAPI;
import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.SkillBehavior;
import com.tyzsskills.impl.server.registries.EffectRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public class DivineSalvationEffect extends SkillBehavior {
    @Override
    public void registerEvent(IEventBus eventBus, ISkill skill) {
        registerAction(
                eventBus,
                EventPriority.LOWEST,
                skill,
                LivingDamageEvent.Pre.class,
                LivingDamageEvent.Pre::getEntity,
                this::onIncomingDamage
        );
    }

    private void onIncomingDamage(LivingDamageEvent.Pre event, ServerPlayer player, ISkill skill, int lvl){
        var durationValues = skill.getValueSet("reprieve_duration");
        var powerValues = skill.getValueSet("repulse_power");
        var radiusValues = skill.getValueSet("repulse_radius");
        if(durationValues == null || powerValues == null || radiusValues == null) return;

        var target = event.getEntity();
        if(target.getHealth() + target.getAbsorptionAmount() <= event.getNewDamage()){
            var durationTick = (int)(durationValues.getValue(lvl) * 20);
            var searchBox = player.getBoundingBox().inflate(radiusValues.getValue(lvl));
            var closeEntities = player.level().getEntitiesOfClass(LivingEntity.class, searchBox);

            for(var entity : closeEntities){
                if(entity == player) continue;

                var speed = powerValues.getValue(lvl);
                entity.setDeltaMovement(entity.getLookAngle().scale(-speed));
                entity.hurtMarked = true;
            }

            target.addEffect(new MobEffectInstance(EffectRegistry.REPRIEVE, durationTick, 0, false, false, true));
            target.stopUsingItem();
            target.setHealth(1f);
            target.setAbsorptionAmount(0f);

            event.setNewDamage(0f);

            TyzsSkillsAPI.skills().tryRemoveSkillLevel(player, skill, 1);
            notifyClient(player, skill);
        }
    }
}
