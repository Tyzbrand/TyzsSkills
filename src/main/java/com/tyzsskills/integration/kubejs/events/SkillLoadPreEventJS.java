package com.tyzsskills.integration.kubejs.events;

import com.tyzsskills.api.interfaces.ISkill;
import dev.latvian.mods.kubejs.event.KubeEvent;

public class SkillLoadPreEventJS implements KubeEvent {
    private final ISkill skill;
    public SkillLoadPreEventJS(ISkill skill){
        this.skill = skill;
    }

    private boolean canceled = false;

    public ISkill getSkill(){return skill;}

    public void cancel(){this.canceled = true;}
    public boolean isCanceled(){return canceled;}
}
