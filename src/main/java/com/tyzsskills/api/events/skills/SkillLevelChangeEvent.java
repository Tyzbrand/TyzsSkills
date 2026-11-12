package com.tyzsskills.api.events.skills;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.SkillEvent;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class SkillLevelChangeEvent extends SkillEvent {
    private final int oldLevel;
    public int getOldLevel() {return oldLevel;}

    private final int newLevel;
    public int getNewLevel() {return newLevel;}

    public SkillLevelChangeEvent(@NotNull ServerPlayer player, @NotNull ISkill skill, int oldLevel, int newLevel) {
        super(player, skill);

        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }
}
