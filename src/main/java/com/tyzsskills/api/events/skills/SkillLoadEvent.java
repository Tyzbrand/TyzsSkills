package com.tyzsskills.api.events.skills;

import com.tyzsskills.api.interfaces.ISkill;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.NotNull;

public abstract class SkillLoadEvent extends Event {
    private final ISkill skill;
    public @NotNull ISkill getSkill(){return skill;}

    public SkillLoadEvent(@NotNull ISkill skill){
        this.skill = skill;
    }

    public static class Pre extends SkillLoadEvent implements ICancellableEvent{
        public Pre(@NotNull ISkill skill){
            super(skill);
        }
    }

    public static class Post extends SkillLoadEvent{
        public Post(@NotNull ISkill skill){
            super(skill);
        }
    }

}


