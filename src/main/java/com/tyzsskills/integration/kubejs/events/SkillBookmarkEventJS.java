package com.tyzsskills.integration.kubejs.events;

import com.tyzsskills.api.interfaces.ISkill;
import dev.latvian.mods.kubejs.event.KubeEvent;
import net.minecraft.server.level.ServerPlayer;

public class SkillBookmarkEventJS implements KubeEvent {
    private final ISkill skill;
    private final ServerPlayer player;

    public SkillBookmarkEventJS(ISkill skill, ServerPlayer player){
        this.skill = skill;
        this.player = player;
    }

    public ISkill getSkill(){return skill;}
    public ServerPlayer getPlayer(){return player;}
}
