package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.Config;
import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.impl.server.Level.LevelManager;
import com.tyzsskills.impl.server.model.SkillBehavior;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class CinderBloodEffect extends SkillBehavior {
    @Override
    public void onIncomingDamage(LivingIncomingDamageEvent event, ServerPlayer player, int lvl, ISkill skill) {
        if(event.getSource().is(DamageTypes.ON_FIRE) || event.getSource().is(DamageTypes.IN_FIRE) || event.getSource().is(DamageTypes.LAVA)){

            if(LevelManager.getLevel(player) < Config.TRAIT_UNLOCK_LEVEL.get()) return;

            if(!Config.PREVENT_LAVA_DAMAGE.get() && event.getSource().is(DamageTypes.LAVA)) return;

            event.setCanceled(true);
            player.clearFire();
            notifyClient(player, skill);
        }
    }
}
