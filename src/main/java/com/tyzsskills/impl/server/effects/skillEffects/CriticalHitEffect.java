package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.SkillBehavior;
import com.tyzsskills.api.tools.TagMatchTool;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.UnknownNullability;

public class CriticalHitEffect extends SkillBehavior {

    @Override
    public void registerEvent(IEventBus eventBus, ISkill skill) {
        registerAction(
                eventBus,
                EventPriority.HIGH,
                skill,
                LivingIncomingDamageEvent.class,
                event -> event.getSource().getEntity(),
                this::onPlayerAttack
        );
    }


    private void onPlayerAttack(LivingIncomingDamageEvent event, ServerPlayer player, ISkill skill,  int lvl){
        var values = skill.getValueSet("success_probability");
        if(values == null) return;

        float chancePercentage = values.getValue(lvl);

        if(player.getRandom().nextFloat() < (chancePercentage/100f)){

            if(TagMatchTool.isEntityInList(skill.getSpecificParameters(), "entity_blacklist", event.getEntity().getType())) return;

            event.setAmount(event.getAmount() * 2f);
            notifyClient(player, skill);

            if (player.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.CRIT,
                        event.getEntity().getX(),
                        event.getEntity().getY() + 1.5,
                        event.getEntity().getZ(),
                        15,
                        0.5, 0.5, 0.5, 0.1);
            }

            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.PLAYER_ATTACK_CRIT, player.getSoundSource(), 1.0f, 1.0f);
        }
    }

}
