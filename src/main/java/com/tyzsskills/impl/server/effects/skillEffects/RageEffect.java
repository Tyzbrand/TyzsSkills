package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.impl.server.model.SkillBehavior;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.UnknownNullability;

public class RageEffect extends SkillBehavior {
    @Override
    public void onPlayerAttack(LivingIncomingDamageEvent event, ServerPlayer player, int lvl, @UnknownNullability ISkill skill) {
        var values = skill.getValues();
        if(values == null || values.isEmpty()) return;

        float currentHp = player.getHealth();

        if(currentHp >= 6f) return;


        int index = Math.min(lvl - 1, values.size() - 1);
        float damageBonus = values.get(index);

        float finalDamage = event.getAmount() * (1f + (damageBonus/100f));

        event.setAmount(finalDamage);
        notifyClient(player, skill);
    }
    }
