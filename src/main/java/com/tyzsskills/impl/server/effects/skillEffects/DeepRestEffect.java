package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.SkillBehavior;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;

public class DeepRestEffect extends SkillBehavior {
    @Override
    public void registerEvent(IEventBus eventBus, ISkill skill) {
        registerAction(
                eventBus,
                skill,
                PlayerWakeUpEvent.class,
                PlayerWakeUpEvent::getEntity,
                this::onPlayerWakeUp
        );
    }


    private void onPlayerWakeUp(PlayerWakeUpEvent event, ServerPlayer player, ISkill skill, int lvl){
        if(event.wakeImmediately()) return;
        if (player.getSleepTimer() < 100) return;

        var values = skill.getValueSet("food_recovery");
        if(values == null) return;

        var ratio = values.getValue(lvl) / 100f;
        if (ratio <= 0f) return;

        var foodData = player.getFoodData();
        var previousFood = foodData.getFoodLevel();
        var previousSat = foodData.getSaturationLevel();

        var foodToAdd = Math.round(20f * ratio);
        var satToAdd = 20f * ratio;

        foodData.eat(foodToAdd, satToAdd);
        notifyClient(player, skill);

        if (foodData.getFoodLevel() > previousFood || foodData.getSaturationLevel() > previousSat) {
            notifyClient(player, skill);
        }
    }
}
