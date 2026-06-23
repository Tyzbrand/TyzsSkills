package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.SkillBehavior;
import com.tyzsskills.api.tools.TagMatchTool;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class TrueStrikeEffect extends SkillBehavior {

    public static final ThreadLocal<Boolean> IS_PENETRATING= ThreadLocal.withInitial(() -> false);

    @Override
    public void registerEvent(IEventBus eventBus, ISkill skill) {
        registerAction(
                eventBus,
                EventPriority.HIGHEST,
                skill,
                LivingIncomingDamageEvent.class,
                event -> event.getSource().getEntity(),
                this::onPlayerAttack
        );
    }

    private void onPlayerAttack(LivingIncomingDamageEvent event, ServerPlayer player, ISkill skill, int lvl) {
        if(IS_PENETRATING.get()) return;

        var values = skill.getValueSet("success_probability");
        if(values == null) return;

        float chancePercentage = values.getValue(lvl);

        if (player.getRandom().nextFloat() < (chancePercentage / 100f)) {
            var rawDamage = event.getAmount();
            var target = event.getEntity();

            if(TagMatchTool.isEntityInList(skill.getSpecificParameters(), "entity_blacklist", target.getType())) return;

            event.setCanceled(true);
            IS_PENETRATING.set(true);

            try{
                    target.hurt(target.damageSources().indirectMagic(player, player), rawDamage);
                    notifyClient(player, skill);
            }
            finally {
                IS_PENETRATING.set(false);
            }
        }
    }
}
