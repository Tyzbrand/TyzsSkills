package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.impl.server.model.SkillBehavior;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

public class IronGutEffect extends SkillBehavior {

    @Override
    public void onEffectApplicable(MobEffectEvent.Applicable event, ServerPlayer player, int lvl, ISkill skill) {
        if (event.getEffectInstance().getEffect() != MobEffects.HUNGER) {
            return;
        }

        if (player.isUsingItem() && player.getUseItem().is(Items.ROTTEN_FLESH)) {
            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
            NotifyClient(player, skill);
        }
    }
}
