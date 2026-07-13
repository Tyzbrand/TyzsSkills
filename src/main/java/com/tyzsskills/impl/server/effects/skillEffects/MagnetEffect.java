package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.SkillBehavior;
import com.tyzsskills.api.tools.TagMatchTool;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;

public class MagnetEffect extends SkillBehavior {

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

    private static final double ITEM_SPEED = .35D;
    private void onPlayerTick(PlayerTickEvent.Post event, ServerPlayer player, ISkill skill, int lvl) {
        if ((player.tickCount + player.getId()) % 2 != 0) return;

        if(player.isDeadOrDying() || player.isSpectator() || player.isCrouching() || player.isCreative()) return;

        var values = skill.getValueSet("block_radius");
        if(values == null) return;

        float blockRadiusValue = values.getValue(lvl);
        int blockRadius = (int)blockRadiusValue;

        AABB searchBox = player.getBoundingBox().inflate(blockRadius);
        List<ItemEntity> items = player.level().getEntitiesOfClass(ItemEntity.class, searchBox);

        if(items.isEmpty()) return;

        var targetX = player.getX();
        var targetY = player.getY() + .5D;
        var targetZ = player.getZ();

        for(var item : items){
            if (item.hasPickUpDelay() || !item.isAlive()) continue;
            if(TagMatchTool.isItemInList(skill.getSpecificParameters(), "item_blacklist", item.getItem())) continue;

            var dx = targetX - item.getX();
            var dy = targetY - item.getY();
            var dz = targetZ - item.getZ();

            var distSqr = dx * dx + dy * dy + dz * dz;

            if (distSqr > .01D) {
                var invDist = 1D / Math.sqrt(distSqr);

                var motionX = dx * invDist * ITEM_SPEED;
                var motionY = dy * invDist * ITEM_SPEED;
                var motionZ = dz * invDist * ITEM_SPEED;

                var currentMotion = item.getDeltaMovement();
                item.setDeltaMovement(
                        currentMotion.x * .5D + motionX,
                        currentMotion.y * .5D + motionY,
                        currentMotion.z * .5D + motionZ
                );
            }
        }
    }

}
