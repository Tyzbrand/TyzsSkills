package com.tyzsskills.server.effects.skillEffects;

import com.tyzsskills.server.model.Skill;
import com.tyzsskills.server.model.SkillBehaviour;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class VillagerLureEffect extends SkillBehaviour {

    @Override
    public void onPlayerTick(ServerPlayer player, int lvl, Skill skill) {
        if(player.tickCount % 20 != 0) return;

        boolean holdsEmerald = player.getMainHandItem().is(Items.EMERALD) ||
                player.getMainHandItem().is(Items.EMERALD_BLOCK) ||
                player.getOffhandItem().is(Items.EMERALD) ||
                player.getOffhandItem().is(Items.EMERALD_BLOCK);

        if(!holdsEmerald) return;

        AABB area = player.getBoundingBox().inflate(10D);

        List<Villager> villagers = player.level().getEntitiesOfClass(Villager.class, area);

        for(var villager : villagers){
            if(villager.isSleeping() || villager.isTrading() || villager.isDeadOrDying()) continue;
            var nav = villager.getNavigation();
            nav.moveTo(player, 0.6D);
            villager.getLookControl().setLookAt(player, 10f, (float)villager.getMaxHeadXRot());

        }
    }
}
