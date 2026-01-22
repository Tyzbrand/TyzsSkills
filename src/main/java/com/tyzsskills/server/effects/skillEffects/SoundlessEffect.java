package com.tyzsskills.server.effects.skillEffects;

import com.tyzsskills.server.model.Skill;
import com.tyzsskills.server.model.SkillBehaviour;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.PlayLevelSoundEvent;

public class SoundlessEffect extends SkillBehaviour {

    @Override
    public void OnNoiseAtPlayer(PlayLevelSoundEvent.AtEntity event, ServerPlayer player, int lvl, Skill skill) {
        event.setCanceled(true);
    }
}
