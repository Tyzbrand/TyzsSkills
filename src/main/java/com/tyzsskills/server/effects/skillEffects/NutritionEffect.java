package com.tyzsskills.server.effects.skillEffects;

import com.tyzsskills.server.model.Skill;
import com.tyzsskills.server.model.SkillBehaviour;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

public class NutritionEffect extends SkillBehaviour {

    @Override
    public void onPlayerFinishUsingItem(LivingEntityUseItemEvent.Finish event, ServerPlayer player, int lvl, Skill skill) {

        var item = event.getItem();
        var foodValue = item.getFoodProperties(player);
        if(foodValue == null) return;

        var values = skill.GetValues();
        if (values == null || values.isEmpty()) return;

        int index = Math.min(lvl - 1, values.size() - 1);
        float bonusPercentage = values.get(index) / 100f;

        int nutritionBonus = (int) Math.ceil(foodValue.nutrition() * bonusPercentage);

        if(nutritionBonus > 0){
            player.getFoodData().eat(nutritionBonus, foodValue.saturation());
            NotifyClient(player, skill);
        }
    }
}
