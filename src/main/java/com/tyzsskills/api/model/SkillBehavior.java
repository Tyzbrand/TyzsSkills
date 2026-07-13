package com.tyzsskills.api.model;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.impl.server.interfaces.ISkillBehavior;
import com.tyzsskills.impl.server.payloads.SkillTriggerPayload;
import com.tyzsskills.impl.server.skills.SkillManager;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.PlayLevelSoundEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.*;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public abstract class SkillBehavior implements ISkillBehavior {

    public abstract void registerEvent(IEventBus eventBus, String skillId);

    protected final <T extends Event> void registerAction(IEventBus eventBus, EventPriority priority, String skillId,
                                                          Class<T> eventClass, Function<T, Entity> entityExtractor, SkillAction<T> skillAction){
        eventBus.addListener(priority, eventClass, event -> {
            if(event instanceof ICancellableEvent cancellable && cancellable.isCanceled()) return;

            var entity = entityExtractor.apply(event);
            if(!(entity instanceof ServerPlayer player)) return;

            var skill = SkillManager.getSkill(skillId);
            if(skill == null) return;

            var lvl = SkillManager.getPlayerSkillLevel(player, skillId);
            if(lvl <= 0) return;

            skillAction.execute(event, player, skill, lvl);
        });
    }

    protected final <T extends Event> void registerAction(IEventBus eventBus, String skillId,
                                                          Class<T> eventClass, Function<T, Entity> entityExtractor, SkillAction<T> skillAction){
        this.registerAction(eventBus, EventPriority.NORMAL, skillId, eventClass, entityExtractor, skillAction);
    }


    //Tools
    protected void notifyClient(@NotNull ServerPlayer player, @NotNull ISkill skill ){
        PacketDistributor.sendToPlayer(player, new SkillTriggerPayload(skill.getID()));
    }

    //Utils
    @FunctionalInterface
    protected interface SkillAction<T extends Event>{
        void execute(T event, ServerPlayer player, ISkill skill, int lvl);
    }
}
