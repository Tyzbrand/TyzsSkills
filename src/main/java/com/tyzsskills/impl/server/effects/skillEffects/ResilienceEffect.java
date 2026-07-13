package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.SkillBehavior;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

public class ResilienceEffect extends SkillBehavior {

    public static final ThreadLocal<Boolean> IS_MODIFYING = ThreadLocal.withInitial(() -> false);

    @Override
    public void registerEvent(IEventBus eventBus, String skillId) {
        registerAction(
                eventBus,
                skillId,
                MobEffectEvent.Added.class,
                MobEffectEvent.Added::getEntity,
                this::onStartingEffect
        );
    }

    private void onStartingEffect(MobEffectEvent.Added event, ServerPlayer player, ISkill skill, int lvl) {

        if(IS_MODIFYING.get()) return;

        var effect = event.getEffectInstance();
        if(effect.isAmbient()) return;
        if(effect.getEffect().value().getCategory() != MobEffectCategory.HARMFUL) return;

        int originalDuration = effect.getDuration();
        if(originalDuration >= 1728000) return; // > 24h

        var values = skill.getValueSet("effect_time_reduction");
        if(values == null) return;;

        float reductionPercentage = values.getValue(lvl);
        if(reductionPercentage <= 0) return;

        int newDuration = (int)(originalDuration * (1- reductionPercentage/100f));
        if(newDuration < 20) return;

        final int capturedDuration = newDuration;
        player.getServer().execute(() -> {
            if(player.isRemoved()) return;
            IS_MODIFYING.set(true);

            try{
                player.removeEffect(effect.getEffect());
                var newEffect = new MobEffectInstance(
                        effect.getEffect(),
                        capturedDuration,
                        effect.getAmplifier(),
                        effect.isAmbient(),
                        effect.isVisible(),
                        effect.showIcon()
                );

                player.addEffect(newEffect);
                notifyClient(player, skill);
            }
            finally {
                IS_MODIFYING.set(false);
            }
        });
    }
}
