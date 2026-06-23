package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.SkillBehavior;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.UnknownNullability;

public class RageEffect extends SkillBehavior {
    @Override
    public void registerEvent(IEventBus eventBus, ISkill skill) {
        registerAction(
                eventBus,
                EventPriority.HIGH,
                skill,
                LivingIncomingDamageEvent.class,
                event -> event.getSource().getEntity(),
                this::onPlayerAttack
        );
    }

    private void onPlayerAttack(LivingIncomingDamageEvent event, ServerPlayer player, ISkill skill, int lvl) {
        var damageBuffs = skill.getValueSet("damage_buff");
        var healthThresholds = skill.getValueSet("health_threshold");
        if(damageBuffs == null || healthThresholds == null) return;

        float currentHp = player.getHealth();
        float hpThreshold = player.getMaxHealth() * healthThresholds.getValue(lvl) / 100f;
        if(currentHp > hpThreshold) return;


        float damageBonus = damageBuffs.getValue(lvl);
        float finalDamage = event.getAmount() * (1f + (damageBonus/100f));

        event.setAmount(finalDamage);
        notifyClient(player, skill);
    }
}
