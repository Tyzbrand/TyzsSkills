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

public class DamageDealEffect extends SkillBehavior {

    public static final ThreadLocal<Boolean> IS_REFLECTING = ThreadLocal.withInitial(() -> false);

    @Override
    public void registerEvent(IEventBus eventBus, ISkill skill) {
        registerAction(
                eventBus,
                EventPriority.HIGHEST,
                skill,
                LivingIncomingDamageEvent.class,
                LivingIncomingDamageEvent::getEntity,
                this::onIncomingDamage
        );
    }


    private void onIncomingDamage(LivingIncomingDamageEvent event, ServerPlayer player, ISkill skill, int lvl) {
        if(IS_REFLECTING.get()) return;

        var values = skill.getValueSet("success_probability");
        if(values == null) return;

        float chancePercentage = values.getValue(lvl);

        if (player.getRandom().nextFloat() < (chancePercentage / 100f)) {
            if(!(event.getSource().getEntity() instanceof LivingEntity source)) return;
            if(TagMatchTool.isEntityInList(skill.getSpecificParameters(), "entity_blacklist", source.getType())) return;

            event.setCanceled(true);
            IS_REFLECTING.set(true);

            try{
                var damageSrc = player.damageSources().thorns(player);
                source.hurt(damageSrc, event.getAmount());
                notifyClient(player, skill);
            }
            finally {
                IS_REFLECTING.set(false);
            }
        }
    }
}
