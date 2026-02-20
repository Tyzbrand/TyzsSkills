package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.impl.server.model.Skill;
import com.tyzsskills.impl.server.model.SkillBehaviour;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class RageEffect extends SkillBehaviour {
    @Override
    public void onPlayerAttack(LivingIncomingDamageEvent event, ServerPlayer player, int lvl, Skill skill) {
        var values = skill.GetValues();
        if(values == null || values.isEmpty()) return;

        float currentHp = player.getHealth();

        if(currentHp >= 6f) return;


        int index = Math.min(lvl - 1, values.size() - 1);
        float damageBonus = values.get(index);

        float finalDamage = event.getAmount() * (1f + (damageBonus/100f));

        event.setAmount(finalDamage);
        NotifyClient(player, skill);
    }
    }
