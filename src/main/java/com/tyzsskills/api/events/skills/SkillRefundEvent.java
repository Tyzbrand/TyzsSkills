package com.tyzsskills.api.events.skills;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.SkillEvent;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.NotNull;

public abstract class SkillRefundEvent extends SkillEvent {
    private final int oldLevel;
    public int getOldLevel() {return oldLevel;}

    private final int newLevel;
    public int getNewLevel() {return newLevel;}

    public SkillRefundEvent(@NotNull ServerPlayer player, @NotNull ISkill skill, int oldLevel, int newLevel) {
        super(player, skill);

        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }

    public static class Pre extends SkillRefundEvent implements ICancellableEvent{
        public Pre(@NotNull ServerPlayer player, @NotNull ISkill skill, int oldLevel, int newLevel) {
            super(player, skill, oldLevel, newLevel);
        }
    }

    public static class Post extends SkillRefundEvent{
        public Post(@NotNull ServerPlayer player, @NotNull ISkill skill, int oldLevel, int newLevel) {
            super(player, skill, oldLevel, newLevel);
        }
    }
}
