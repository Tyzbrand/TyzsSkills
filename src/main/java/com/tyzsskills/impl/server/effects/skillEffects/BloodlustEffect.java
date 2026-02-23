package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.impl.server.model.Skill;
import com.tyzsskills.impl.server.model.SkillBehaviour;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Enemy;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

public class BloodlustEffect extends SkillBehaviour {

    @Override
    public void onPlayerKill(LivingDeathEvent event, ServerPlayer player, int lvl, Skill skill) {

        var values = skill.getValues();
        if (values == null || values.isEmpty()) return;

        var target = event.getEntity();
        if(!(target instanceof Enemy)) return;
        var targetHealth = target.getMaxHealth();

        int index = Math.min(lvl - 1, values.size() - 1);
        float percentage = values.get(index) / 100f;
        float healthAmount = Math.max(percentage * targetHealth, 1f);

        if (event.getSource().getEntity() instanceof ServerPlayer) {
            player.heal(healthAmount);
            NotifyClient(player, skill);
        }

    }
}
