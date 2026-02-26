package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.impl.server.model.SkillBehavior;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.UnknownNullability;

public class VenomousAttackEffect extends SkillBehavior {

    @Override
    public void onPlayerAttack(LivingIncomingDamageEvent event, ServerPlayer player, int lvl, @UnknownNullability ISkill skill) {
        var values = skill.getValues();
        if(values == null || values.isEmpty()) return;

        LivingEntity target = event.getEntity();

        int index = Math.min(lvl - 1, values.size() - 1);
        float chancePercentage = values.get(index);

        if(player.getRandom().nextFloat() < (chancePercentage/100f)){
            target.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 1));
            notifyClient(player, skill);
        }

    }
}
