package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.SkillBehavior;
import com.tyzsskills.api.tools.TagMatchTool;
import net.minecraft.nbt.Tag;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.AgeableMob;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class ShepherdsBlessingEffect extends SkillBehavior {

    @Override
    public void registerEvent(IEventBus eventBus, ISkill skill) {
        registerAction(
                eventBus,
                skill,
                BabyEntitySpawnEvent.class,
                BabyEntitySpawnEvent::getCausedByPlayer,
                this::onBabySpawn
        );
    }

    private void onBabySpawn(BabyEntitySpawnEvent event, ServerPlayer player, ISkill skill, int lvl) {
        if(!(event.getChild() instanceof AgeableMob baby)) return;

        var values = skill.getValueSet("growth_speed");
        if(values == null) return;

        var speedBonus = values.getValue(lvl);
        var factor = 1f + (speedBonus / 100f);
        var server = player.getServer();

        if(TagMatchTool.isEntityInList(skill.getSpecificParameters(), "entity_blacklist", event.getChild().getType())) return;

        if (server != null) {
            server.tell(new TickTask(server.getTickCount() + 1, () -> {
                var newAge = (int) (-24000 / factor);
                baby.setAge(newAge);
                notifyClient(player, skill);
            }));
        }
    }

}
