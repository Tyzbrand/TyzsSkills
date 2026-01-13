package com.tyzsskills.server.effects.skillEffects;

import com.tyzsskills.server.model.Skill;
import com.tyzsskills.server.model.SkillBehaviour;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class MagnetEffect extends SkillBehaviour {

    @Override
    public void onPlayerTick(ServerPlayer player, int lvl, Skill skill) {

        if(player.isDeadOrDying() || player.isSpectator() || player.isCrouching()) return;

        var values = skill.GetValues();
        if (values == null || values.isEmpty()) return;

        int index = Math.min(lvl - 1, values.size() - 1);
        float blockRadiusValue = values.get(index);
        int blockRadius = (int)blockRadiusValue;

        AABB searchBox = player.getBoundingBox().inflate(blockRadius);
        List<ItemEntity> items = player.level().getEntitiesOfClass(ItemEntity.class, searchBox);

        for(var item : items){
            if (item.hasPickUpDelay() || !item.isAlive()) continue;

            Vec3 targetPos = player.position().add(0, 0.5, 0);
            Vec3 itemPos = item.position();
            Vec3 direction = targetPos.subtract(itemPos);

            if (direction.lengthSqr() > 0.01) {
                Vec3 motion = direction.normalize().scale(0.2);

                item.setDeltaMovement(item.getDeltaMovement().scale(0.5).add(motion));
            }
        }
    }
}
