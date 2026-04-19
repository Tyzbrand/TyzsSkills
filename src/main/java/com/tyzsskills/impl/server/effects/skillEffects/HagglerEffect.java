package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.Config;
import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.impl.server.Level.LevelManager;
import com.tyzsskills.impl.server.model.SkillBehavior;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class HagglerEffect extends SkillBehavior {

    @Override
    public void onPlayerTick(ServerPlayer player, int lvl, ISkill skill) {
        if(player.tickCount % 20 != 0) return;

        boolean holdsEmerald = player.getMainHandItem().is(Items.EMERALD) ||
                player.getMainHandItem().is(Items.EMERALD_BLOCK) ||
                player.getOffhandItem().is(Items.EMERALD) ||
                player.getOffhandItem().is(Items.EMERALD_BLOCK);

        if(!holdsEmerald) return;

        var radiusValues = skill.getValueSet("block_radius");
        var speedValues = skill.getValueSet("villager_speed");
        if(radiusValues == null || speedValues == null) return;

        var blockRadius = radiusValues.getValue(lvl);
        if(blockRadius <= 0) return;

        var villagerSpeedFactor = 1f + speedValues.getValue(lvl) / 100f;


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
