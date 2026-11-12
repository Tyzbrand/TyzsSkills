package com.tyzsskills.api.events.skills;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.SkillEvent;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.NotNull;

public abstract class SkillActivationEvent extends SkillEvent {
    public SkillActivationEvent(@NotNull ServerPlayer player, @NotNull ISkill skill) {
        super(player, skill);
    }

    public static class Pre extends SkillActivationEvent implements ICancellableEvent{
        public Pre(@NotNull ServerPlayer player, @NotNull ISkill skill) {
            super(player, skill);
        }
    }

    public static class Post extends SkillActivationEvent {
        public Post(@NotNull ServerPlayer player, @NotNull ISkill skill) {
            super(player, skill);
        }
    }
}
