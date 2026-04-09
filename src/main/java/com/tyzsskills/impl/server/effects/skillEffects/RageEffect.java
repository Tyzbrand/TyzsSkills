package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.impl.server.model.SkillBehavior;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.UnknownNullability;

public class RageEffect extends SkillBehavior {
    @Override
    public void onPlayerAttack(LivingIncomingDamageEvent event, ServerPlayer player, int lvl, @UnknownNullability ISkill skill) {
        var damageBuffs = skill.getValueSet("damage_buff");
        var healthThresholds = skill.getValueSet("health_threshold");
        if(damageBuffs == null || healthThresholds == null) return;

        float currentHp = player.getHealth();
        float hpThreshold = player.getMaxHealth() * healthThresholds.getValue(lvl) / 100f;
        if(currentHp > hpThreshold) return;


        float damageBonus = damageBuffs.getValue(lvl);
        float finalDamage = event.getAmount() * (1f + (damageBonus/100f));

        event.setAmount(finalDamage);
        notifyClient(player, skill);
    }
    }
