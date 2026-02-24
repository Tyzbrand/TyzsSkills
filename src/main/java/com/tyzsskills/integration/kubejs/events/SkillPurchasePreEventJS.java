package com.tyzsskills.integration.kubejs.events;

import com.tyzsskills.api.interfaces.ISkill;
import dev.latvian.mods.kubejs.event.KubeEvent;
import net.minecraft.server.level.ServerPlayer;

public class SkillPurchasePreEventJS implements KubeEvent {
    private final ISkill skill;
    private final ServerPlayer player;

    private boolean canceled = false;

    public SkillPurchasePreEventJS(ISkill skill, ServerPlayer player){
        this.skill = skill;
        this.player = player;
    }

    public ISkill getSkill(){return skill;}
    public ServerPlayer getPlayer(){return player;}

    public void cancel(){this.canceled = true;}
    public boolean isCanceled(){return canceled;}
}
