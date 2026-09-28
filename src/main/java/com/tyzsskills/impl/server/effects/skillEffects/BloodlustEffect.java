package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.SkillBehavior;
import com.tyzsskills.impl.client.active.TagMatchManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Enemy;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

public class BloodlustEffect extends SkillBehavior {

    @Override
    public void registerEvent(IEventBus eventBus, String skillId) {
        registerAction(
                eventBus,
                skillId,
                LivingDeathEvent.class,
                event -> event.getSource().getEntity(),
                this::onPlayerKill
        );
    }


    private void onPlayerKill(LivingDeathEvent event, ServerPlayer player, ISkill skill, int lvl) {
        var values = skill.getValueSet("health_percentage");
        if (values == null) return;

        var target = event.getEntity();
        if(!(target instanceof Enemy)) return;
        if(TagMatchManager.isEntityInList(skill.getSpecificParameters(), "entity_blacklist", target.getType())) return;

        var targetHealth = target.getMaxHealth();

        float percentage = values.getValue(lvl) / 100f;
        float healthAmount = percentage * targetHealth;

        player.heal(healthAmount);
        notifyClient(player, skill);
    }
}
