package com.tyzsskills.server.effects.skillEffects;

import com.tyzsskills.server.model.Skill;
import com.tyzsskills.server.model.SkillBehaviour;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class VenomousAttackEffect extends SkillBehaviour {

    @Override
    public void onPlayerAttack(LivingIncomingDamageEvent event, ServerPlayer player, int lvl, Skill skill) {
        var values = skill.GetValues();
        if(values == null || values.isEmpty()) return;

        LivingEntity target = event.getEntity();

        int index = Math.min(lvl - 1, values.size() - 1);
        float chancePercentage = values.get(index);

        if(player.getRandom().nextFloat() < (chancePercentage/100f)){
            target.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 1));
        }

    }
}
