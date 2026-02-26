package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.Config;
import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.impl.server.Level.LevelManager;
import com.tyzsskills.impl.server.model.SkillBehavior;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.PlayLevelSoundEvent;

public class SoundlessEffect extends SkillBehavior {

    @Override
    public void OnNoiseAtPlayer(PlayLevelSoundEvent.AtEntity event, ServerPlayer player, int lvl, ISkill skill) {
        if(LevelManager.getLevel(player) < Config.TRAIT_UNLOCK_LEVEL.get()) return;
        event.setCanceled(true);
    }
}
