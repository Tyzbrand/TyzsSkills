package com.tyzsskills.api.model;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.impl.server.payloads.SkillTriggerPayload;
import net.minecraft.server.level.ServerPlayer;

import net.neoforged.neoforge.event.PlayLevelSoundEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.*;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public abstract class SkillBehavior {
    public void onIncomingDamage(LivingIncomingDamageEvent event, ServerPlayer player, int lvl, ISkill skill){}
    public void onPlayerAttack(LivingIncomingDamageEvent event, ServerPlayer player, int lvl, ISkill skill){}
    public void onPlayerKill(LivingDeathEvent event, ServerPlayer player, int lvl, ISkill skill){}
    public void onPlayerDeath(LivingDeathEvent event, ServerPlayer player, int lvl, ISkill skill){}
    public void onStartingEffect(MobEffectEvent.Added event, ServerPlayer player, int lvl, ISkill skill){}
    public void onPlayerBreakBlock(BlockEvent.BreakEvent event, ServerPlayer player, int lvl, ISkill skill){}
    public void onPlayerFinishUsingItem(LivingEntityUseItemEvent.Finish event, ServerPlayer player, int lvl, ISkill skill){}
    public void onPickupXp(PlayerXpEvent.PickupXp event, ServerPlayer player, int lvl, ISkill skill){}
    public void onEffectApplicable(MobEffectEvent.Applicable event, ServerPlayer player, int lvl, ISkill skill){}
    public void onTargetChange(LivingChangeTargetEvent event, ServerPlayer player, int lvl, ISkill skill){}
    public void onPlayerClone(PlayerEvent.Clone event, ServerPlayer player, int lvl, ISkill skill){}
    public void OnNoiseAtPlayer(PlayLevelSoundEvent.AtEntity event, ServerPlayer player, int lvl, ISkill skill){}
    public void OnPlayerWakeUp(PlayerWakeUpEvent event, ServerPlayer player, int lvl, ISkill skill){}
    public void OnBabySpawn(BabyEntitySpawnEvent event, ServerPlayer player, int lvl, ISkill skill){}
    public void onItemCrafted(PlayerEvent.ItemCraftedEvent event, ServerPlayer player, int lvl, ISkill skill){}
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event, ServerPlayer player, int lvl, ISkill skill){}




    //Tracked events to prevent TPS spikes
    public void onPlayerTick(ServerPlayer player, int lvl, ISkill skill){}
    public void onLivingVisibility(LivingEvent.LivingVisibilityEvent event, ServerPlayer player, int lvl, ISkill skill){}



    protected void notifyClient(ServerPlayer player, ISkill skill ){
        PacketDistributor.sendToPlayer(player, new SkillTriggerPayload(skill.getID()));
    }

    public int getPriority(){return 0;}

}
