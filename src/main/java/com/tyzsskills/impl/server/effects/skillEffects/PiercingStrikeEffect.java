package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.impl.server.model.SkillBehavior;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class PiercingStrikeEffect extends SkillBehavior {

    public static final ThreadLocal<Boolean> IS_PENETRATING= ThreadLocal.withInitial(() -> false);

    @Override
    public void onPlayerAttack(LivingIncomingDamageEvent event, ServerPlayer player, int lvl, ISkill skill) {
        if(IS_PENETRATING.get()) return;

        var values = skill.getValueSet("success_probability");
        if(values == null) return;

        float chancePercentage = values.getValue(lvl);

        if (player.getRandom().nextFloat() < (chancePercentage / 100f)) {
            if(event.getEntity() instanceof LivingEntity target){
                var rawDamage = event.getAmount();

                event.setCanceled(true);
                IS_PENETRATING.set(true);

                try{
                    target.hurt(target.damageSources().indirectMagic(player, player), rawDamage);
                    notifyClient(player, skill);
                }
                finally {
                    IS_PENETRATING.set(false);
                }

            }
        }
    }

    @Override
    public int getPriority(){return -10;}
}
