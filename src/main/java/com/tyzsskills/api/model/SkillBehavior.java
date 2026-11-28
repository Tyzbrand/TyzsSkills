package com.tyzsskills.api.model;

import com.tyzsskills.Settings;
import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.impl.server.core.ErrorManager;
import com.tyzsskills.impl.server.interfaces.ISkillBehavior;
import com.tyzsskills.impl.server.payloads.SkillTriggerPayload;
import com.tyzsskills.impl.server.skills.SkillManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public abstract class SkillBehavior implements ISkillBehavior {

    public abstract void registerEvent(IEventBus eventBus, ISkill skill);

    protected final <T extends Event> void registerAction(IEventBus eventBus, EventPriority priority, ISkill skill,
                                                          Class<T> eventClass, Function<T, Entity> entityExtractor, SkillAction<T> skillAction){
        eventBus.addListener(priority, eventClass, event -> {
            if(event instanceof ICancellableEvent cancellable && cancellable.isCanceled()) return;

            var entity = entityExtractor.apply(event);
            if(!(entity instanceof ServerPlayer player)) return;

            var currentSkill = SkillManager.getSkill(skill.getId());
            if(currentSkill == null || SkillManager.isSkillDeactivated(player, currentSkill)) return;

            var lvl = SkillManager.getSkillLevel(player, currentSkill);
            if(lvl <= 0) return;

            skillAction.execute(event, player, currentSkill, lvl);
        });
    }

    protected final <T extends Event> void registerAction(IEventBus eventBus, ISkill skill,
                                                          Class<T> eventClass, Function<T, Entity> entityExtractor, SkillAction<T> skillAction){
        this.registerAction(eventBus, EventPriority.NORMAL, skill, eventClass, entityExtractor, skillAction);
    }


    //Tools
    protected final void notifyClient(@NotNull ServerPlayer player, @NotNull ISkill skill){
        if(!Settings.Server.TRIGGER_SKILL_OVERLAY.get()) return;
        PacketDistributor.sendToPlayer(player, new SkillTriggerPayload(skill.getId()));
    }

    protected final void registerMissingKey(@NotNull ServerPlayer player, @NotNull ISkill skill, @NotNull String valueKey){
        var error = Component.literal("Missing '"+ valueKey + "' value set for skill : [" + skill.getId() + "]");
        ErrorManager.printDynamicError(player, error);
    }

    //Utils
    @FunctionalInterface
    protected interface SkillAction<T extends Event>{
        void execute(T event, ServerPlayer player, ISkill skill, int lvl);
    }
}
