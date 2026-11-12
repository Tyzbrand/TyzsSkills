package com.tyzsskills.api.events.skills;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.SkillEvent;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class SkillUnbookmarkEvent extends SkillEvent {
    public SkillUnbookmarkEvent(@NotNull ServerPlayer player, @NotNull ISkill skill) {
        super(player, skill);
    }
}
