package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.impl.server.model.SkillBehavior;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

public class NutritionEffect extends SkillBehavior {

    @Override
    public void onPlayerFinishUsingItem(LivingEntityUseItemEvent.Finish event, ServerPlayer player, int lvl, ISkill skill) {

        var item = event.getItem();
        var foodValue = item.getFoodProperties(player);
        if(foodValue == null) return;

        var values = skill.getValues();
        if (values == null || values.isEmpty()) return;

        int index = Math.min(lvl - 1, values.size() - 1);
        float bonusPercentage = values.get(index) / 100f;

        int nutritionBonus = Math.round(foodValue.nutrition() * bonusPercentage);

        if(nutritionBonus > 0){
            player.getFoodData().eat(nutritionBonus, foodValue.saturation() * 0.5f);
            NotifyClient(player, skill);
        }
    }
}
