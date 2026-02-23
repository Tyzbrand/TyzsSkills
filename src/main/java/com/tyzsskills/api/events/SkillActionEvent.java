package com.tyzsskills.api.events;

import com.tyzsskills.api.interfaces.ISkill;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public abstract class SkillActionEvent extends Event {
    private final ISkill skill;
    private final ServerPlayer player;
    public SkillActionEvent(ISkill skill, ServerPlayer player){
        this.skill = skill;
        this.player = player;
    }

    public ISkill getSkill(){return skill;}
    public ServerPlayer getPlayer(){return player;}

    public static class LevelChange extends SkillActionEvent {
        private final int oldLevel;
        private final int newLevel;

        public LevelChange(ISkill skill, ServerPlayer player, int oldLevel, int newLevel){
            super(skill, player);
            this.oldLevel = oldLevel;
            this.newLevel = newLevel;
        }

        public int getOldLevel(){return oldLevel;}
        public int getNewLevel(){return newLevel;}
    }

    public static class PurchasePre extends SkillActionEvent implements ICancellableEvent {
        public PurchasePre(ISkill skill, ServerPlayer player){
            super(skill, player);
        }
    }

    public static class PurchasePost extends SkillActionEvent{
        public PurchasePost(ISkill skill, ServerPlayer player){
            super(skill, player);
        }
    }

    public static class RefundPre extends SkillActionEvent implements ICancellableEvent{
        public RefundPre(ISkill skill, ServerPlayer player){
            super(skill, player);
        }
    }

    public static class RefundPost extends SkillActionEvent{
        public RefundPost(ISkill skill, ServerPlayer player){
            super(skill, player);
        }
    }

    public static class Bookmark extends SkillActionEvent{
        public Bookmark(ISkill skill, ServerPlayer player) {
            super(skill, player);
        }
    }

}
