package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.SkillBehavior;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HagglerEffect extends SkillBehavior {

    @Override
    public void registerEvent(IEventBus eventBus, ISkill skill) {
        registerAction(
                eventBus,
                skill,
                PlayerTickEvent.Post.class,
                PlayerTickEvent.Post::getEntity,
                this::onPlayerTick
        );
    }

    private final Set<Item> TRIGGER_ITEMS = Set.of(Items.EMERALD, Items.EMERALD_BLOCK);
    private void onPlayerTick(PlayerTickEvent.Post event, ServerPlayer player, ISkill skill, int lvl) {
        if(player.tickCount % 20 != 0) return;

        boolean holdsEmerald = TRIGGER_ITEMS.contains(player.getMainHandItem().getItem()) || TRIGGER_ITEMS.contains(player.getOffhandItem().getItem());

        if(!holdsEmerald) return;

        var radiusValues = skill.getValueSet("block_radius");
        var speedValues = skill.getValueSet("villager_speed");
        if(radiusValues == null || speedValues == null) return;

        var blockRadius = radiusValues.getValue(lvl);
        if(blockRadius <= 0) return;

        var villagerSpeedFactor = 0.6f + speedValues.getValue(lvl) / 100f;


        AABB area = player.getBoundingBox().inflate(blockRadius);

        List<Villager> villagers = player.level().getEntitiesOfClass(Villager.class, area);

        for(var villager : villagers){
            if(villager.isSleeping() || villager.isTrading() || villager.isDeadOrDying()) continue;
            var nav = villager.getNavigation();
            nav.moveTo(player, villagerSpeedFactor);
            villager.getLookControl().setLookAt(player, 10f, (float)villager.getMaxHeadXRot());

        }
    }
}
