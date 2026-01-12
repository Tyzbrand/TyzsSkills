package com.tyzsskills.server.effects.skillEffects;

import com.tyzsskills.server.model.Skill;
import com.tyzsskills.server.model.SkillBehaviour;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class AdrenalineEffect extends SkillBehaviour {

    @Override
    public void onIncomingDamage(LivingIncomingDamageEvent event, ServerPlayer player, int lvl, Skill skill) {

        var values = skill.GetValues();
        if(values == null || values.isEmpty()) return;

        float currentHealth = player.getHealth();
        float maxHealth = player.getMaxHealth();

        int index = Math.min(lvl - 1, values.size() - 1);
        float value = values.get(index);
        float healthFlag = (value / 100f) * maxHealth;

        if(currentHealth <= healthFlag && !player.hasEffect(MobEffects.MOVEMENT_SPEED)){
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 2));

        }
    }

}
