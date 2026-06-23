package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.SkillBehavior;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;

public class ExperienceBoostEffect extends SkillBehavior {

    @Override
    public void registerEvent(IEventBus eventBus, ISkill skill) {
        registerAction(
                eventBus,
                skill,
                PlayerXpEvent.PickupXp.class,
                PlayerXpEvent.PickupXp::getEntity,
                this::onPickupXp
        );
    }

    private void onPickupXp(PlayerXpEvent.PickupXp event, ServerPlayer player, ISkill skill, int lvl) {
        float orbValue = event.getOrb().getValue();
        if(orbValue <= 0)return;

        var values = skill.getValueSet("bonus_percentage");
        if(values == null) return;

        float bonusPercentage = values.getValue(lvl) / 100f;

        var exactBonus = orbValue * bonusPercentage;
        int guaranteedGain = (int)exactBonus;
        float bonusValue = exactBonus - guaranteedGain;

        if(player.getRandom().nextFloat() < bonusValue) guaranteedGain++;

        if(guaranteedGain > 0) player.giveExperiencePoints(guaranteedGain);

    }
}
