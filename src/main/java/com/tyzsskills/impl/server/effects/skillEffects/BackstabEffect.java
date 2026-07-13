package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.SkillBehavior;
import com.tyzsskills.api.tools.TagMatchTool;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.UnknownNullability;

public class BackstabEffect extends SkillBehavior {

    @Override
    public void registerEvent(IEventBus eventBus, String skillId) {
        registerAction(
                eventBus,
                EventPriority.HIGH,
                skillId,
                LivingIncomingDamageEvent.class,
                event -> event.getSource().getEntity(),
                this::onPlayerAttack
        );
    }

    private void onPlayerAttack(LivingIncomingDamageEvent event, ServerPlayer player, ISkill skill, int lvl) {
        var values = skill.getValueSet("damage_buff");
        if(values == null) return;

        var target = event.getEntity();
        var dotAngle = player.getLookAngle().dot(target.getLookAngle());

        if(dotAngle > 0.5){
            if(TagMatchTool.isEntityInList(skill.getSpecificParameters(), "entity_blacklist", target.getType())) return;

            var bonusPercentage = 1 + (values.getValue(lvl) / 100f);
            event.setAmount(event.getAmount() * bonusPercentage);
            notifyClient(player, skill);
        }
    }
}
