package com.tyzsskills.server.effects.skillEffects;

import com.tyzsskills.server.model.Skill;
import com.tyzsskills.server.model.SkillBehaviour;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class RageEffect extends SkillBehaviour {
    @Override
    public void onPlayerAttack(LivingIncomingDamageEvent event, ServerPlayer player, int lvl, Skill skill) {
        var values = skill.GetValues();
        if(values == null || values.isEmpty()) return;

        float maxHp = player.getMaxHealth();
        float currentHp = player.getHealth();

        if(currentHp >= maxHp) return;

        float missingPercentage = (maxHp - currentHp) / maxHp;

        int index = Math.min(lvl - 1, values.size() - 1);
        float maxBonus = values.get(index);

        float damageMultiplier = 1f + (missingPercentage * (maxBonus/100f));

        event.setAmount(event.getAmount() * damageMultiplier);
        NotifyClient(player, skill);
    }
    }
