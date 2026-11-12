package com.tyzsskills.api.events.skills;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.SkillEvent;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.NotNull;

public abstract class SkillDeactivationEvent extends SkillEvent {
    public SkillDeactivationEvent(@NotNull ServerPlayer player, @NotNull ISkill skill) {
        super(player, skill);
    }

    public static class Pre extends SkillDeactivationEvent implements ICancellableEvent{
        public Pre(@NotNull ServerPlayer player, @NotNull ISkill skill) {
            super(player, skill);
        }
    }

    public static class Post extends SkillDeactivationEvent{
        public Post(@NotNull ServerPlayer player, @NotNull ISkill skill) {
            super(player, skill);
        }
    }
}
