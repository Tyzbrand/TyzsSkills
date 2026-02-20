package com.tyzsskills.server.events;

import com.tyzsskills.server.model.Skill;
import com.tyzsskills.server.skills.SkillManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.PlayLevelSoundEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class SkillEffectsEvents {

    @SubscribeEvent
    public static void OnPlayerAttack(LivingIncomingDamageEvent event){
        if(event.isCanceled()) return;
        var manager = SkillManager.Get();

        if (event.getSource().getEntity() instanceof ServerPlayer player){
            for (Skill skill : manager.getAllSkills()){
                if(skill.HasBehaviour()){
                    var lvl = manager.getPlayerSkillLevel(player, skill.GetID());
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
            for (Skill skill : manager.getAllSkills()){
                if(skill.HasBehaviour()){

                    var lvl = manager.getPlayerSkillLevel(player, skill.GetID());
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
            for (Skill skill : manager.getAllSkills()){
                if(skill.HasBehaviour()){

                    var lvl = manager.getPlayerSkillLevel(player, skill.GetID());
                    if( lvl<= 0) continue;

                    skill.GetBehaviour().onPlayerKill(event, player, lvl, skill);
                }

            }
        }
    }

    @SubscribeEvent
    public static void OnStartingEffect(MobEffectEvent.Added event){
        var manager = SkillManager.Get();

        if (event.getEntity() instanceof ServerPlayer player){
            for (Skill skill : manager.getAllSkills()){
                if(skill.HasBehaviour()){

                    var lvl = manager.getPlayerSkillLevel(player, skill.GetID());
                    if( lvl<= 0) continue;

                    skill.GetBehaviour().onStartingEffect(event, player, lvl, skill);
                }

            }
        }
    }

    @SubscribeEvent
    public static void OnPlayerBreakBlock(BlockEvent.BreakEvent event){
        if(event.isCanceled()) return;
        var manager = SkillManager.Get();

        if (event.getPlayer() instanceof ServerPlayer player){
            for (Skill skill : manager.getAllSkills()){
                if(skill.HasBehaviour()){

                    var lvl = manager.getPlayerSkillLevel(player, skill.GetID());
                    if( lvl<= 0) continue;

                    skill.GetBehaviour().onPlayerBreakBlock(event, player, lvl, skill);
                }

            }
        }
    }

    @SubscribeEvent
    public static void OnPlayerFinishUsingItem(LivingEntityUseItemEvent.Finish event){
        var manager = SkillManager.Get();

        if (event.getEntity() instanceof ServerPlayer player){
            for (Skill skill : manager.getAllSkills()){
                if(skill.HasBehaviour()){

                    var lvl = manager.getPlayerSkillLevel(player, skill.GetID());
                    if( lvl<= 0) continue;

                    skill.GetBehaviour().onPlayerFinishUsingItem(event, player, lvl, skill);
                }

            }
        }
    }

    @SubscribeEvent
    public static void OnPickupXp(PlayerXpEvent.PickupXp event){
        if(event.isCanceled()) return;
        var manager = SkillManager.Get();

        if (event.getEntity() instanceof ServerPlayer player){
            for (Skill skill : manager.getAllSkills()){
                if(skill.HasBehaviour()){

                    var lvl = manager.getPlayerSkillLevel(player, skill.GetID());
                    if( lvl<= 0) continue;

                    skill.GetBehaviour().onPickupXp(event, player, lvl, skill);
                }

            }
        }
    }

    @SubscribeEvent
    public static void OnEffectApplicable(MobEffectEvent.Applicable event){
        var manager = SkillManager.Get();

        if (event.getEntity() instanceof ServerPlayer player){
            for (Skill skill : manager.getAllSkills()){
                if(skill.HasBehaviour()){

                    var lvl = manager.getPlayerSkillLevel(player, skill.GetID());
                    if( lvl<= 0) continue;

                    skill.GetBehaviour().onEffectApplicable(event, player, lvl, skill);
                }

            }
        }
    }

    @SubscribeEvent
    public static void OnTargetChange(LivingChangeTargetEvent event){
        if(event.isCanceled()) return;
        var manager = SkillManager.Get();

        if (event.getNewAboutToBeSetTarget() instanceof ServerPlayer player){
            for (Skill skill : manager.getAllSkills()){
                if(skill.HasBehaviour()){

                    var lvl = manager.getPlayerSkillLevel(player, skill.GetID());
                    if( lvl<= 0) continue;

                    skill.GetBehaviour().onTargetChange(event, player, lvl, skill);
                }

            }
        }
    }

    @SubscribeEvent
    public static void OnPlayerDeath(LivingDeathEvent event){
        if(event.isCanceled()) return;
        var manager = SkillManager.Get();

        if (event.getEntity() instanceof ServerPlayer player){
            for (Skill skill : manager.getAllSkills()){
                if(skill.HasBehaviour()){

                    var lvl = manager.getPlayerSkillLevel(player, skill.GetID());
                    if( lvl<= 0) continue;

                    skill.GetBehaviour().onPlayerDeath(event, player, lvl, skill);
                }

            }
        }
    }

    public static void OnPlayerClone(PlayerEvent.Clone event){  //DEFERRED ABOVE
        var manager = SkillManager.Get();

        if (event.getEntity() instanceof ServerPlayer player){
            for (Skill skill : manager.getAllSkills()){
                if(skill.HasBehaviour()){

                    var lvl = manager.getPlayerSkillLevel(player, skill.GetID());
                    if( lvl<= 0) continue;

                    skill.GetBehaviour().onPlayerClone(event, player, lvl, skill);
                }

            }
        }
    }

    @SubscribeEvent
    public static void OnNoiseAtPlayer(PlayLevelSoundEvent.AtEntity event){
        if(event.isCanceled()) return;
        var manager = SkillManager.Get();

        if (event.getEntity() instanceof ServerPlayer player){
            for (Skill skill : manager.getAllSkills()){
                if(skill.HasBehaviour()){

                    var lvl = manager.getPlayerSkillLevel(player, skill.GetID());
                    if( lvl<= 0) continue;

                    skill.GetBehaviour().OnNoiseAtPlayer(event, player, lvl, skill);
                }

            }
        }
    }

    @SubscribeEvent
    public static void OnPlayerWakeUp(PlayerWakeUpEvent event){
        var manager = SkillManager.Get();

        if (event.getEntity() instanceof ServerPlayer player){
            for (Skill skill : manager.getAllSkills()){
                if(skill.HasBehaviour()){

                    var lvl = manager.getPlayerSkillLevel(player, skill.GetID());
                    if( lvl<= 0) continue;

                    skill.GetBehaviour().OnPlayerWakeUp(event, player, lvl, skill);
                }

            }
        }
    }


    private final static String[] playerTickSkills = {"magnet", "silver_tongue", "deep_sight"};
    @SubscribeEvent
    public static void OnPlayerTick(PlayerTickEvent.Post event){
        var manager = SkillManager.Get();

        if (event.getEntity() instanceof ServerPlayer player){

            for(var id : playerTickSkills){
                var skill = SkillManager.Get().getSkill(id.toLowerCase());

                if(skill != null && skill.HasBehaviour()){
                    var lvl = manager.getPlayerSkillLevel(player, skill.GetID());
                    if( lvl<= 0) continue;
                    skill.GetBehaviour().onPlayerTick(player, lvl, skill);
                }
            }
        }
    }

    private final static String[] entityVisibility = {"stealth"};
    @SubscribeEvent
    public static void OnLivingVisibility(LivingEvent.LivingVisibilityEvent event){
        var manager = SkillManager.Get();

        if (event.getEntity() instanceof ServerPlayer player){

            for(var id : entityVisibility){
                var skill = SkillManager.Get().getSkill(id.toLowerCase());

                if(skill != null && skill.HasBehaviour()){
                    var lvl = manager.getPlayerSkillLevel(player, skill.GetID());
                    if( lvl<= 0) continue;
                    skill.GetBehaviour().onLivingVisibility(event, player, lvl, skill);
                }
            }
        }
    }
}
