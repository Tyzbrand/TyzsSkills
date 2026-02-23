package com.tyzsskills.api.events;

import com.tyzsskills.api.interfaces.ISkill;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public abstract class SkillLoadEvent extends Event {
    private final ISkill skill;
    public SkillLoadEvent(ISkill skill){
        this.skill = skill;
    }

    public ISkill getSkill(){return skill;}

    public static class Pre extends SkillLoadEvent implements ICancellableEvent{
        public Pre(ISkill skill){
            super(skill);
        }
    }

    public static class Post extends SkillLoadEvent{
        public Post(ISkill skill){
            super(skill);
        }
    }

}


