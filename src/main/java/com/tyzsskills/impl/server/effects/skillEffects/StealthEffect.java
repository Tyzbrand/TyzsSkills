package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.impl.server.model.Skill;
import com.tyzsskills.impl.server.model.SkillBehaviour;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Enemy;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class StealthEffect extends SkillBehaviour {


    @Override
    public void onLivingVisibility(LivingEvent.LivingVisibilityEvent event, ServerPlayer player, int lvl, Skill skill) {

        Entity observer = event.getLookingEntity();
        if (!(observer instanceof Enemy)) return;

        var values = skill.getValues();
        if (values == null || values.isEmpty()) return;

        int index = Math.min(lvl - 1, values.size() - 1);
        float reductionPercent = values.get(index);
        double multiplier = 1.0 - (reductionPercent / 100.0);

        event.modifyVisibility(multiplier);
    }


}
