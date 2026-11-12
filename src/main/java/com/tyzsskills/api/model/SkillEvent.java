package com.tyzsskills.api.model;

import com.tyzsskills.api.interfaces.ISkill;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.NotNull;

public abstract class SkillEvent extends Event {
    private final ServerPlayer player;
    public @NotNull ServerPlayer getPlayer(){return player;}

    private final ISkill skill;
    public @NotNull ISkill getSkill(){return skill;}

    public SkillEvent(@NotNull ServerPlayer player, @NotNull ISkill skill){
        this.player = player;
        this.skill = skill;
    }


}
