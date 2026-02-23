package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.impl.server.model.SkillBehavior;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Enemy;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import org.jetbrains.annotations.UnknownNullability;

public class BloodlustEffect extends SkillBehavior {

    @Override
    public void onPlayerKill(LivingDeathEvent event, ServerPlayer player, int lvl, @UnknownNullability ISkill skill) {

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
