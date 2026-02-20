package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.impl.server.model.Skill;
import com.tyzsskills.impl.server.model.SkillBehaviour;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class BackstabEffect extends SkillBehaviour {

    @Override
    public void onPlayerAttack(LivingIncomingDamageEvent event, ServerPlayer player, int lvl, Skill skill) {
        var values = skill.GetValues();
        if(values == null || values.isEmpty()) return;

        if(event.getEntity() instanceof LivingEntity target){
            double dotAngle = player.getLookAngle().dot(target.getLookAngle());

            if(dotAngle > 0.5){
                int index = Math.min(lvl - 1, values.size() - 1);
                float bonusPercentage = 1 + (values.get(index) / 100f);
                event.setAmount(event.getAmount() * bonusPercentage);
                NotifyClient(player, skill);
            }
        }

    }
}
