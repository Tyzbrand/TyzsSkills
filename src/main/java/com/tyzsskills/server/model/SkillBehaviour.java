package com.tyzsskills.server.model;

import net.minecraft.server.level.ServerPlayer;

import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public abstract class SkillBehaviour {
    public void onPlayerTick(ServerPlayer player, int lvl, Skill skill){}
    public void onIncomingDamage(LivingIncomingDamageEvent event, ServerPlayer player, int lvl, Skill skill){}
    public void onPlayerAttack(LivingIncomingDamageEvent event, ServerPlayer player, int lvl, Skill skill){}

}
