package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.SkillBehavior;
import com.tyzsskills.api.tools.TagMatchTool;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

public class NutritionEffect extends SkillBehavior {

    @Override
    public void registerEvent(IEventBus eventBus, String skillId) {
        registerAction(
                eventBus,
                skillId,
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

        float bonusPercentage = values.getValue(lvl) / 100f;

        int nutritionBonus = Math.round(foodValue.nutrition() * bonusPercentage);

        if(nutritionBonus > 0){
            if(TagMatchTool.isItemInList(skill.getSpecificParameters(), "food_blacklist", item)) return;

            player.getFoodData().eat(nutritionBonus, foodValue.saturation() * 0.5f);
            notifyClient(player, skill);
        }
    }
}
