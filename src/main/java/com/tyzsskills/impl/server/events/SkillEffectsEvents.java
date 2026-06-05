package com.tyzsskills.impl.server.events;

import com.tyzsskills.Config;
import com.tyzsskills.impl.server.Level.LevelManager;
import com.tyzsskills.impl.server.model.Skill;
import com.tyzsskills.impl.server.skills.SkillManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.PlayLevelSoundEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class SkillEffectsEvents {

    @SubscribeEvent
    public static void onPlayerAttack(LivingIncomingDamageEvent event){
        if(event.isCanceled()) return;
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;

        for (Skill skill : SkillManager.getSortedBehaviors()){
            var lvl = SkillManager.getPlayerSkillLevel(player, skill.getID());
            if( lvl<= 0) continue;

            skill.getBehavior().onPlayerAttack(event, player, lvl, skill);

        }
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event){
        if(event.isCanceled()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        for (Skill skill : SkillManager.getSortedBehaviors()){
            var lvl = SkillManager.getPlayerSkillLevel(player, skill.getID());
            if( lvl<= 0) continue;

            skill.getBehavior().onIncomingDamage(event, player, lvl, skill);
        }
    }

    @SubscribeEvent
    public static void onPlayerKill(LivingDeathEvent event){
        if(event.isCanceled()) return;
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;

        for (Skill skill : SkillManager.getSortedBehaviors()){
            var lvl = SkillManager.getPlayerSkillLevel(player, skill.getID());
            if( lvl<= 0) continue;

            skill.getBehavior().onPlayerKill(event, player, lvl, skill);
        }
    }

    @SubscribeEvent
    public static void onStartingEffect(MobEffectEvent.Added event){
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        for (Skill skill : SkillManager.getSortedBehaviors()){
            var lvl = SkillManager.getPlayerSkillLevel(player, skill.getID());
            if( lvl<= 0) continue;

            skill.getBehavior().onStartingEffect(event, player, lvl, skill);
        }
    }

    @SubscribeEvent
    public static void onPlayerBreakBlock(BlockEvent.BreakEvent event){
        if(event.isCanceled()) return;
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;

        for (Skill skill : SkillManager.getSortedBehaviors()){
            var lvl = SkillManager.getPlayerSkillLevel(player, skill.getID());
            if( lvl<= 0) continue;

            skill.getBehavior().onPlayerBreakBlock(event, player, lvl, skill);

        }
    }

    @SubscribeEvent
    public static void onPlayerFinishUsingItem(LivingEntityUseItemEvent.Finish event){
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        for (Skill skill : SkillManager.getSortedBehaviors()){
            var lvl = SkillManager.getPlayerSkillLevel(player, skill.getID());
            if( lvl<= 0) continue;

            skill.getBehavior().onPlayerFinishUsingItem(event, player, lvl, skill);
        }
    }

    @SubscribeEvent
    public static void onPickupXp(PlayerXpEvent.PickupXp event){
        if(event.isCanceled()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        for (Skill skill : SkillManager.getSortedBehaviors()){
            var lvl = SkillManager.getPlayerSkillLevel(player, skill.getID());
            if( lvl<= 0) continue;

            skill.getBehavior().onPickupXp(event, player, lvl, skill);
        }
    }

    @SubscribeEvent
    public static void onEffectApplicable(MobEffectEvent.Applicable event){
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        for (Skill skill : SkillManager.getSortedBehaviors()){
            var lvl = SkillManager.getPlayerSkillLevel(player, skill.getID());
            if( lvl<= 0) continue;

            skill.getBehavior().onEffectApplicable(event, player, lvl, skill);
        }
    }

    @SubscribeEvent
    public static void onTargetChange(LivingChangeTargetEvent event){
        if(event.isCanceled()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        for (Skill skill : SkillManager.getSortedBehaviors()){
            var lvl = SkillManager.getPlayerSkillLevel(player, skill.getID());
            if( lvl<= 0) continue;

            skill.getBehavior().onTargetChange(event, player, lvl, skill);
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event){
        if(event.isCanceled()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        
        for (Skill skill : SkillManager.getSortedBehaviors()){
            var lvl = SkillManager.getPlayerSkillLevel(player, skill.getID());
            if( lvl<= 0) continue;

            skill.getBehavior().onPlayerDeath(event, player, lvl, skill);
        }
    }

    public static void onPlayerClone(PlayerEvent.Clone event){  //DEFERRED ABOVE
        
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        for (Skill skill : SkillManager.getSortedBehaviors()){
            var lvl = SkillManager.getPlayerSkillLevel(player, skill.getID());
            if( lvl<= 0) continue;

            skill.getBehavior().onPlayerClone(event, player, lvl, skill);
        }
    }

    @SubscribeEvent
    public static void onNoiseAtPlayer(PlayLevelSoundEvent.AtEntity event){
        if(event.isCanceled()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        

        for (Skill skill : SkillManager.getSortedBehaviors()){
            var lvl = SkillManager.getPlayerSkillLevel(player, skill.getID());
            if( lvl<= 0) continue;
            skill.getBehavior().OnNoiseAtPlayer(event, player, lvl, skill);
        }
    }

    @SubscribeEvent
    public static void onPlayerWakeUp(PlayerWakeUpEvent event){
        
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        for (Skill skill : SkillManager.getSortedBehaviors()){
            var lvl = SkillManager.getPlayerSkillLevel(player, skill.getID());
            if( lvl<= 0) continue;

            skill.getBehavior().OnPlayerWakeUp(event, player, lvl, skill);
        }
    }
    @SubscribeEvent
    public static void onRightClickBlock(net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock event){
        if (event.isCanceled() || !(event.getEntity() instanceof ServerPlayer player)) return;
        

        for (Skill skill : SkillManager.getSortedBehaviors()){
            var lvl = SkillManager.getPlayerSkillLevel(player, skill.getID());
            if(lvl <= 0) continue;

            skill.getBehavior().onRightClickBlock(event, player, lvl, skill);
        }
    }


    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event){
        
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        for (Skill skill : SkillManager.getSortedBehaviors()){
            var lvl = SkillManager.getPlayerSkillLevel(player, skill.getID());
            if( lvl<= 0) continue;

            skill.getBehavior().onItemCrafted(event, player, lvl, skill);
        }
    }

    @SubscribeEvent
    public static void onBabySpawn(BabyEntitySpawnEvent event){
        if(event.isCanceled()) return;

        
        if (!(event.getCausedByPlayer() instanceof ServerPlayer player)) return;

        for (Skill skill : SkillManager.getSortedBehaviors()){
            var lvl = SkillManager.getPlayerSkillLevel(player, skill.getID());
            if( lvl<= 0) continue;

            skill.getBehavior().OnBabySpawn(event, player, lvl, skill);
        }
    }


    private final static String[] playerTickSkills = {"magnet", "haggler"};
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event){
        
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        for(var id : playerTickSkills){
            var skill = SkillManager.getSkill(id.toLowerCase());
            if(skill == null || !skill.hasBehaviour()) continue;

            var lvl = SkillManager.getPlayerSkillLevel(player, skill.getID());
            if( lvl<= 0) continue;

            skill.getBehavior().onPlayerTick(player, lvl, skill);
        }
    }

    private final static String[] entityVisibility = {"stealth"};
    @SubscribeEvent
    public static void onLivingVisibility(LivingEvent.LivingVisibilityEvent event){
        
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        for(var id : entityVisibility){
            var skill = SkillManager.getSkill(id.toLowerCase());
            if(skill == null || !skill.hasBehaviour()) continue;

            var lvl = SkillManager.getPlayerSkillLevel(player, skill.getID());
            if( lvl<= 0) continue;

            skill.getBehavior().onLivingVisibility(event, player, lvl, skill);
        }
    }
}

