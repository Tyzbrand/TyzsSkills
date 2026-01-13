package com.tyzsskills.server.model;

import net.minecraft.server.level.ServerPlayer;

import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

public abstract class SkillBehaviour {
    public void onPlayerTick(ServerPlayer player, int lvl, Skill skill){}
    public void onIncomingDamage(LivingIncomingDamageEvent event, ServerPlayer player, int lvl, Skill skill){}
    public void onPlayerAttack(LivingIncomingDamageEvent event, ServerPlayer player, int lvl, Skill skill){}
    public void onPlayerKill(LivingDeathEvent event, ServerPlayer player, int lvl, Skill skill){}
    public void onPlayerDeath(LivingDeathEvent event, ServerPlayer player, int lvl, Skill skill){}
    public void onStartingEffect(MobEffectEvent.Added event, ServerPlayer player, int lvl, Skill skill){}

}
