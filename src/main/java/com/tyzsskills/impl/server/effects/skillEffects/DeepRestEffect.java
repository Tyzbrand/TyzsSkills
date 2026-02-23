package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.Config;
import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.impl.server.model.SkillBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;

public class DeepRestEffect extends SkillBehavior {

    @Override
    public void OnPlayerWakeUp(PlayerWakeUpEvent event, ServerPlayer player, int lvl, ISkill skill) {
        if(!(player.level() instanceof ServerLevel level)) return;

        long timeOfDay = level.getDayTime() % 24000L;
        if (timeOfDay > 1000L) return;


        if(Config.RESTORES_NUTRITION.get()) player.getFoodData().setFoodLevel(20);
        if(Config.RESTORES_HEALTH.get()) player.setHealth(player.getMaxHealth());
        if(Config.RESTORES_SATURATION.get())player.getFoodData().setSaturation(20f);

        NotifyClient(player, skill);
    }
}
