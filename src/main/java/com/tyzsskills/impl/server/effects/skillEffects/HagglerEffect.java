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
    public void registerEvent(IEventBus eventBus, String skillId) {
        registerAction(
                eventBus,
                skillId,
                PlayerTickEvent.Post.class,
                PlayerTickEvent.Post::getEntity,
                this::onPlayerTick
        );
    }

    private void onPlayerTick(PlayerTickEvent.Post event, ServerPlayer player, ISkill skill, int lvl) {
        if ((player.tickCount + player.getId()) % 20 != 0) return;

        var mainHand = player.getMainHandItem();
        var secondHand = player.getOffhandItem();
        boolean holdsEmerald = mainHand.is(Items.EMERALD) || mainHand.is(Items.EMERALD_BLOCK) ||
                secondHand.is(Items.EMERALD) || secondHand.is(Items.EMERALD_BLOCK);

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

            villager.getLookControl().setLookAt(player, 10f, (float) villager.getMaxHeadXRot());

            if (villager.distanceToSqr(player) > 9D) villager.getNavigation().moveTo(player, villagerSpeedFactor);
            else if (villager.getNavigation().isInProgress()) villager.getNavigation().stop();

        }
    }
}
