package com.tyzsskills.server.effects.skillEffects;

import com.tyzsskills.server.model.Skill;
import com.tyzsskills.server.model.SkillBehaviour;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;

public class DeepRestEffect extends SkillBehaviour {

    @Override
    public void OnPlayerWakeUp(PlayerWakeUpEvent event, ServerPlayer player, int lvl, Skill skill) {
        if(!(player.level() instanceof ServerLevel level)) return;

        long timeOfDay = level.getDayTime() % 24000L;
        if (timeOfDay > 1000L) return;


        player.setHealth(player.getMaxHealth());
        player.getFoodData().setFoodLevel(20);
        player.getFoodData().setSaturation(20f);

        NotifyClient(player, skill);
    }
}
