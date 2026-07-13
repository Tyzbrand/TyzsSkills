package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.SkillBehavior;
import com.tyzsskills.api.tools.TagMatchTool;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.UnknownNullability;

public class VenomousAttackEffect extends SkillBehavior {

    @Override
    public void registerEvent(IEventBus eventBus, String skillId) {
        registerAction(
                eventBus,
                skillId,
                LivingIncomingDamageEvent.class,
                event -> event.getSource().getEntity(),
                this::onPlayerAttack
        );
    }

    private void onPlayerAttack(LivingIncomingDamageEvent event, ServerPlayer player, ISkill skill, int lvl) {
        var values = skill.getValueSet("success_probability");
        if(values == null) return;

        LivingEntity target = event.getEntity();

        float chancePercentage = values.getValue(lvl);

        if(player.getRandom().nextFloat() < (chancePercentage/100f)){
            if(TagMatchTool.isEntityInList(skill.getSpecificParameters(), "entity_blacklist", target.getType())) return;

            target.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 1));
            notifyClient(player, skill);
        }

    }
}
