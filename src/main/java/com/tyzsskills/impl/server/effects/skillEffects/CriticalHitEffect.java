package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.impl.server.model.SkillBehavior;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.UnknownNullability;

public class CriticalHitEffect extends SkillBehavior {

    @Override
    public void onPlayerAttack(LivingIncomingDamageEvent event, ServerPlayer player, int lvl, @UnknownNullability ISkill skill){
        var values = skill.getValues();
        if(values == null || values.isEmpty()) return;

        int index = Math.min(lvl - 1, values.size() - 1);
        float chancePercentage = values.get(index);


        if(player.getRandom().nextFloat() < (chancePercentage/100f)){

            event.setAmount(event.getAmount() * 2f);
            NotifyClient(player, skill);

            if (player.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.CRIT,
                        event.getEntity().getX(),
                        event.getEntity().getY() + 1.5,
                        event.getEntity().getZ(),
                        15, // Nombre de particules
                        0.5, 0.5, 0.5, 0.1);
            }

            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.PLAYER_ATTACK_CRIT, player.getSoundSource(), 1.0f, 1.0f);
        }
    }
}
