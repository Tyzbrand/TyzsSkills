package com.tyzsskills.integration.kubejs.events;

import com.tyzsskills.api.interfaces.ISkill;
import dev.latvian.mods.kubejs.event.KubeEvent;

public class SkillLoadPostEventJS implements KubeEvent {
    private final ISkill skill;
    public SkillLoadPostEventJS(ISkill skill){
        this.skill = skill;
    }

    public ISkill getSkill(){return skill;}
}
