package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.impl.server.model.SkillBehavior;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;

public class ExperienceBoostEffect extends SkillBehavior {

    @Override
    public void onPickupXp(PlayerXpEvent.PickupXp event, ServerPlayer player, int lvl, ISkill skill) {
        float orbValue = event.getOrb().getValue();
        if(orbValue <= 0)return;

        var values = skill.getValues();
        if (values == null || values.isEmpty()) return;

        int index = Math.min(lvl - 1, values.size() - 1);
        float bonusPercentage = values.get(index) / 100f;

        int bonusValue = (int) Math.ceil(orbValue * bonusPercentage);

        if(bonusValue > 0) {
            player.giveExperiencePoints(bonusValue);
            NotifyClient(player, skill);
        }

    }
}
