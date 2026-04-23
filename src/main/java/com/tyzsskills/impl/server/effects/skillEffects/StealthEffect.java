package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.SkillBehavior;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Enemy;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class StealthEffect extends SkillBehavior {


    @Override
    public void onLivingVisibility(LivingEvent.LivingVisibilityEvent event, ServerPlayer player, int lvl, ISkill skill) {

        Entity observer = event.getLookingEntity();
        if (!(observer instanceof Enemy)) return;

        var values = skill.getValueSet("range_reduction");
        if(values == null) return;

        float reductionPercent = values.getValue(lvl);
        double multiplier = 1.0 - (reductionPercent / 100.0);

        event.modifyVisibility(multiplier);
    }


}
