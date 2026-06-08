package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.SkillBehavior;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class MagnetEffect extends SkillBehavior {

    @Override
    public void onPlayerTick(ServerPlayer player, int lvl, ISkill skill) {

        if(player.isDeadOrDying() || player.isSpectator() || player.isCrouching()) return;

        var values = skill.getValueSet("block_radius");
        if(values == null) return;

        float blockRadiusValue = values.getValue(lvl);
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

    @Override
    public boolean isTickEvent() {
        return true;
    }
}
