package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.SkillBehavior;
import com.tyzsskills.impl.client.active.TagMatchManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

public class NutritionEffect extends SkillBehavior {

    @Override
    public void registerEvent(IEventBus eventBus, ISkill skill) {
        registerAction(
                eventBus,
                skill,
                LivingEntityUseItemEvent.Finish.class,
                LivingEntityUseItemEvent.Finish::getEntity,
                this::onPlayerFinishUsingItem
        );
    }

    private void onPlayerFinishUsingItem(LivingEntityUseItemEvent.Finish event, ServerPlayer player, ISkill skill, int lvl) {
        var item = event.getItem();
        var foodValue = item.getFoodProperties(player);
        if(foodValue == null) return;

        var values = skill.getValueSet("nutrition_bonus");
        if(values == null) return;

        var bonusPercentage = values.getValue(lvl) / 100f;
        var nutritionBonus = Math.round(foodValue.nutrition() * bonusPercentage);
        var saturationBonus = foodValue.saturation() * bonusPercentage;

        if(nutritionBonus > 0 || saturationBonus > 0f){
            if(TagMatchManager.isItemInList(skill.getSpecificParameters(), "food_blacklist", item)) return;

            var foodData = player.getFoodData();
            var newFood = Mth.clamp(foodData.getFoodLevel() + nutritionBonus, 0, 20);
            var newSat = Mth.clamp(foodData.getSaturationLevel() + saturationBonus, 0f, (float) newFood);

            foodData.setFoodLevel(newFood);
            foodData.setSaturation(newSat);

            notifyClient(player, skill);
        }
    }
}
