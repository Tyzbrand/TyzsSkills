package com.tyzsskills.server.events;

import com.tyzsskills.server.active.*;
import com.tyzsskills.server.effects.GenericEffects;
import com.tyzsskills.server.model.Skill;
import com.tyzsskills.server.skills.SkillManager;
import com.tyzsskills.server.xp.XpManager;
import com.tyzsskills.server.xp.xpEvents.XpBlock;
import com.tyzsskills.server.xp.xpEvents.XpEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

public class RuntimeEvents {

    @SubscribeEvent
    public static void OnPlayerLogin(PlayerEvent.PlayerLoggedInEvent event){
        if(!(event.getEntity() instanceof ServerPlayer player)) return;

        AutoSyncClient.SyncMainData(player);
        AutoSyncClient.SyncSkillList(player);
        AutoSyncClient.SyncSkillLevels(player);
        AutoSyncClient.SyncConfig(player);
    }

    @SubscribeEvent
    public static void OnPlayerClone(PlayerEvent.Clone event){

        if(!(event.getOriginal() instanceof ServerPlayer oldPlayer) ||
                !(event.getEntity() instanceof ServerPlayer newPlayer)) return;

        if(event.isWasDeath()){
            XpManager.RestorePlayerXPData(oldPlayer, newPlayer);
            LevelManager.RestorePlayerLevelData(oldPlayer, newPlayer);
            SpManager.RestorePlayerSPData(oldPlayer, newPlayer);
            SkillManager.Get().RestaureSkillData(oldPlayer, newPlayer);

            GenericEffects.RestaureEffects(newPlayer);
        }
    }


    @SubscribeEvent
    public static void OnBlockBreak(BlockEvent.BreakEvent event){

        if(event.isCanceled()) return;

        if(event.getPlayer() instanceof ServerPlayer serverPlayer){
            XpBlock.BlockBreakProfit(event.getState(), serverPlayer);
        }
    }

    @SubscribeEvent
    public static void OnEntityDeath(LivingDeathEvent event){

        if(event.isCanceled()) return;

        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;
        XpEntity.EntityKillProfit(event.getEntity(), player);
    }

    @SubscribeEvent
    public static void OnPlayerAttack(LivingIncomingDamageEvent event){
        if(event.isCanceled()) return;
        var manager = SkillManager.Get();

        if (event.getSource().getEntity() instanceof ServerPlayer player){
            for (Skill skill : manager.GetAllSkills()){
                if(skill.HasBehaviour()){
                    var lvl = manager.GetPlayerSkillLevel(player, skill.GetID());
                    if( lvl<= 0) continue;

                    skill.GetBehaviour().onPlayerAttack(event, player, lvl, skill);
                }

            }
        }
    }

    @SubscribeEvent
    public static void OnIncomingDamage(LivingIncomingDamageEvent event){
        if(event.isCanceled()) return;
        var manager = SkillManager.Get();

        if (event.getEntity() instanceof ServerPlayer player){
            for (Skill skill : manager.GetAllSkills()){
                if(skill.HasBehaviour()){

                    var lvl = manager.GetPlayerSkillLevel(player, skill.GetID());
                    if( lvl<= 0) continue;

                    skill.GetBehaviour().onIncomingDamage(event, player, lvl, skill);
                }

            }
        }
    }

    @SubscribeEvent
    public static void OnPlayerKill(LivingDeathEvent event){
        if(event.isCanceled()) return;
        var manager = SkillManager.Get();

        if (event.getSource().getEntity() instanceof ServerPlayer player){
            for (Skill skill : manager.GetAllSkills()){
                if(skill.HasBehaviour()){

                    var lvl = manager.GetPlayerSkillLevel(player, skill.GetID());
                    if( lvl<= 0) continue;

                    skill.GetBehaviour().onPlayerKill(event, player, lvl, skill);
                }

            }
        }
    }

}
