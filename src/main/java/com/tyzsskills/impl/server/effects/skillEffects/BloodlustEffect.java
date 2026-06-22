package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.SkillBehavior;
import com.tyzsskills.api.tools.TagMatchTool;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Enemy;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import org.jetbrains.annotations.UnknownNullability;

public class BloodlustEffect extends SkillBehavior {

    @Override
    public void onPlayerKill(LivingDeathEvent event, ServerPlayer player, int lvl, @UnknownNullability ISkill skill) {

        var values = skill.getValueSet("health_percentage");
        if (values == null) return;

        var target = event.getEntity();
        if(!(target instanceof Enemy)) return;
        if(TagMatchTool.isEntityInList(skill.getSpecificParameters(), "entity_blacklist", target.getType())) return;

        var targetHealth = target.getMaxHealth();

        float percentage = values.getValue(lvl) / 100f;
        float healthAmount = percentage * targetHealth;

        player.heal(healthAmount);
        notifyClient(player, skill);
    }
}
