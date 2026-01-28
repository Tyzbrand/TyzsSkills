package com.tyzsskills.server.effects.skillEffects;

import com.tyzsskills.Config;
import com.tyzsskills.server.model.Skill;
import com.tyzsskills.server.model.SkillBehaviour;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class CinderBloodEffect extends SkillBehaviour {
    @Override
    public void onIncomingDamage(LivingIncomingDamageEvent event, ServerPlayer player, int lvl, Skill skill) {
        if(event.getSource().is(DamageTypes.ON_FIRE) || event.getSource().is(DamageTypes.IN_FIRE) || event.getSource().is(DamageTypes.LAVA)){

            if(!Config.PREVENT_LAVA_DAMAGE.get() && event.getSource().is(DamageTypes.LAVA)) return;

            event.setCanceled(true);
            player.clearFire();
            NotifyClient(player, skill);
        }
    }
}
