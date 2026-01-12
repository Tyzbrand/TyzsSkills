package com.tyzsskills.server.effects.skillEffects;

import com.tyzsskills.server.model.Skill;
import com.tyzsskills.server.model.SkillBehaviour;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

public class BloodlustEffect extends SkillBehaviour {

    @Override
    public void onPlayerKill(LivingDeathEvent event, ServerPlayer player, int lvl, Skill skill) {

        var values = skill.GetValues();
        if (values == null || values.isEmpty()) return;

        int index = Math.min(lvl - 1, values.size() - 1);
        float healthAmount = values.get(index);

        if (event.getSource().getEntity() instanceof ServerPlayer) {
            player.heal(healthAmount);
        }

    }
}
