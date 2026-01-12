package com.tyzsskills.server.effects.skillEffects;

import com.tyzsskills.server.model.Skill;
import com.tyzsskills.server.model.SkillBehaviour;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class ResistanceEffect  extends SkillBehaviour {

    @Override
    public void onIncomingDamage(LivingIncomingDamageEvent event, ServerPlayer player, int lvl, Skill skill) {

        var values = skill.GetValues();
        if (values == null || values.isEmpty()) return;

        int index = Math.min(lvl - 1, values.size() - 1);

        float value = values.get(index);
        if(value >= 100f){
            event.setCanceled(true);
            return;
        }

        float damageAbsorption = 1f - (values.get(index) / 100f);
        float newDmg = event.getAmount() * damageAbsorption;

        event.setAmount(newDmg);
    }
}
