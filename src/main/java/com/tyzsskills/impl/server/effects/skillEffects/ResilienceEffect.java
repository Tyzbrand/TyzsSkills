package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.impl.server.model.SkillBehavior;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

public class ResilienceEffect extends SkillBehavior {

    public static final ThreadLocal<Boolean> IS_MODIFYING = ThreadLocal.withInitial(() ->false);

    @Override
    public void onStartingEffect(MobEffectEvent.Added event, ServerPlayer player, int lvl, ISkill skill) {

        if(IS_MODIFYING.get()) return;

        var effect = event.getEffectInstance();
        if(effect.isAmbient()) return;
        if(effect.getEffect().value().getCategory() != MobEffectCategory.HARMFUL) return;

        int originalDuration = effect.getDuration();
        if(originalDuration >= 1728000) return; // > 24h


        var values = skill.getValues();
        if (values == null || values.isEmpty()) return;

        int index = Math.min(lvl - 1, values.size() - 1);
        float reductionPercentage = values.get(index);
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
                NotifyClient(player, skill);
            }
            finally {
                IS_MODIFYING.set(false);
            }

        });





    }
}
