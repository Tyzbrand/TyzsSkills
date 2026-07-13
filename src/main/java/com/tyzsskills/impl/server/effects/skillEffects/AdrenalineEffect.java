package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.SkillBehavior;
import com.tyzsskills.impl.server.skills.SkillManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class AdrenalineEffect extends SkillBehavior {

    @Override
    public void registerEvent(IEventBus eventBus, String skillId) {
        registerAction(
                eventBus,
                skillId,
                LivingIncomingDamageEvent.class,
                LivingIncomingDamageEvent::getEntity,
                this::onIncomingDamage
                );
    }

    private void onIncomingDamage(LivingIncomingDamageEvent event, ServerPlayer player, ISkill skill, int lvl) {
        var healthThresholds = skill.getValueSet("health_threshold");
        var effectDurations = skill.getValueSet("effect_duration");

        if(healthThresholds == null || effectDurations == null) return;

        float currentHealth = player.getHealth();
        float maxHealth = player.getMaxHealth();

        float healthThreshold = healthThresholds.getValue(lvl);
        float healthFlag = (healthThreshold / 100f) * maxHealth;

        float effectDuration = effectDurations.getValue(lvl);
        int durationInTick = Math.max((int)(effectDuration * 20f), 20);


        if(currentHealth <= healthFlag && !player.hasEffect(MobEffects.MOVEMENT_SPEED)){
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, durationInTick, 2));
            player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, durationInTick, 0));
            notifyClient(player, skill);
        }
    }


}
