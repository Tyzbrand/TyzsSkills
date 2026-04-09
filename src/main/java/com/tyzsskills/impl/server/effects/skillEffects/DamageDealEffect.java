package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.impl.server.model.SkillBehavior;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class DamageDealEffect extends SkillBehavior {

    public static final ThreadLocal<Boolean> IS_REFLECTING = ThreadLocal.withInitial(() ->false);

    @Override
    public void onIncomingDamage(LivingIncomingDamageEvent event, ServerPlayer player, int lvl, ISkill skill) {

        if(IS_REFLECTING.get()) return;

        var values = skill.getValueSet("success_probability");
        if(values == null) return;

        float chancePercentage = values.getValue(lvl);



        if (player.getRandom().nextFloat() < (chancePercentage / 100f)) {
            if(event.getSource().getEntity() instanceof LivingEntity source){
                event.setCanceled(true);

                IS_REFLECTING.set(true);

                try{
                    var damageSrc = player.damageSources().playerAttack(player);
                    source.hurt(damageSrc, event.getAmount());
                    notifyClient(player, skill);
                }
                finally {
                    IS_REFLECTING.set(false);
                }

            }
        }
    }
}
