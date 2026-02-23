package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.impl.server.model.SkillBehavior;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;

import java.util.Optional;
import java.util.UUID;

public class GildedAuraEffect extends SkillBehavior {

    @Override
    public void onTargetChange(LivingChangeTargetEvent event, ServerPlayer player, int lvl, ISkill skill) {
        if (!(event.getEntity() instanceof Piglin piglin)) return;
        if (piglin.getLastHurtByMob() == player) return;

        var brain = piglin.getBrain();
        if(brain.hasMemoryValue(MemoryModuleType.ANGRY_AT)){
            Optional<UUID> angryAt = brain.getMemory(MemoryModuleType.ANGRY_AT);
            if (angryAt.isPresent() && angryAt.get().equals(player.getUUID())) return;
        }

        event.setCanceled(true);
        NotifyClient(player, skill);
    }
}
