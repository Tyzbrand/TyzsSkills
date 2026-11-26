package com.tyzsskills.impl.server.mob_effects;

import com.tyzsskills.api.events.spells.SpellCastEvent;
import com.tyzsskills.impl.server.registries.EffectRegistry;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

public class ReprieveMobEffect extends MobEffect {
    public ReprieveMobEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF000000);
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onIncomingDamage(LivingIncomingDamageEvent event){
        if(event.isCanceled() || event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;
        if(isAffected(event.getEntity())) event.setCanceled(true);
    }

    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if(event.isCanceled()) return;
        if(isAffected(event.getEntity())) event.setCanceled(true);
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if(event.isCanceled()) return;
        if (isAffected(event.getEntity())) event.setUseItem(net.neoforged.neoforge.common.util.TriState.FALSE);
    }

    @SubscribeEvent
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if(event.isCanceled()) return;
        if(isAffected(event.getEntity())) event.setCanceled(true);
    }


    @SubscribeEvent
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event){
        if(event.isCanceled()) return;
        if (event.getEntity() instanceof LivingEntity living && isAffected(living)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event){
        if(event.isCanceled()) return;
        if(isAffected(event.getPlayer())) event.setCanceled(true);
    }

    @SubscribeEvent
    public void onSpellCast(SpellCastEvent.Pre event){
        if(event.isCanceled()) return;
        if(isAffected(event.getPlayer())) event.setCanceled(true);
    }

    public static boolean isAffected(LivingEntity entity){return entity.hasEffect(EffectRegistry.REPRIEVE);}
}
