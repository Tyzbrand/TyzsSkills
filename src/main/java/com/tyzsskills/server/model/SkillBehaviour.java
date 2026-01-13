package com.tyzsskills.server.model;

import com.tyzsskills.server.payloads.SkillTriggerPayload;
import net.minecraft.server.level.ServerPlayer;

import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public abstract class SkillBehaviour {
    public void onPlayerTick(ServerPlayer player, int lvl, Skill skill){}
    public void onIncomingDamage(LivingIncomingDamageEvent event, ServerPlayer player, int lvl, Skill skill){}
    public void onPlayerAttack(LivingIncomingDamageEvent event, ServerPlayer player, int lvl, Skill skill){}
    public void onPlayerKill(LivingDeathEvent event, ServerPlayer player, int lvl, Skill skill){}
    public void onPlayerDeath(LivingDeathEvent event, ServerPlayer player, int lvl, Skill skill){}
    public void onStartingEffect(MobEffectEvent.Added event, ServerPlayer player, int lvl, Skill skill){}
    public void onPlayerBreakBlock(BlockEvent.BreakEvent event, ServerPlayer player, int lvl, Skill skill){}
    public void onPlayerFinishUsingItem(LivingEntityUseItemEvent.Finish event, ServerPlayer player, int lvl, Skill skill){}
    public void onPickupXp(PlayerXpEvent.PickupXp event, ServerPlayer player, int lvl, Skill skill){}



    protected void NotifyClient(ServerPlayer player, Skill skill ){
        PacketDistributor.sendToPlayer(player, new SkillTriggerPayload(skill.GetID()));
    }

}
