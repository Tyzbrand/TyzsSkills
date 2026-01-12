package com.tyzsskills.server.effects.skillEffects;

import com.tyzsskills.server.model.Skill;
import com.tyzsskills.server.model.SkillBehaviour;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class DamageDealEffect extends SkillBehaviour {

    public static final ThreadLocal<Boolean> IS_REFLECTING = ThreadLocal.withInitial(() ->false);

    @Override
    public void onIncomingDamage(LivingIncomingDamageEvent event, ServerPlayer player, int lvl, Skill skill) {

        if(IS_REFLECTING.get()) return;

        var values = skill.GetValues();
        if (values == null || values.isEmpty()) return;

        int index = Math.min(lvl - 1, values.size() - 1);
        float chancePercentage = values.get(index);



        if (player.getRandom().nextFloat() < (chancePercentage / 100f)) {
            if(event.getSource().getEntity() instanceof LivingEntity source){
                event.setCanceled(true);

                IS_REFLECTING.set(true);

                try{
                    var damageSrc = player.damageSources().playerAttack(player);
                    source.hurt(damageSrc, event.getAmount());
                }
                finally {
                    IS_REFLECTING.set(false);
                }

            }
        }
    }
}
