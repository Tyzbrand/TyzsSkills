package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.model.SkillBehavior;
import com.tyzsskills.api.tools.TagMatchTool;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;

public class MagnetEffect extends SkillBehavior {

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

    private void onPlayerTick(PlayerTickEvent.Post event, ServerPlayer player, ISkill skill, int lvl) {
        if ((player.tickCount + player.getId()) % 2 != 0) return;

        if(player.isDeadOrDying() || player.isSpectator() || player.isCrouching() || player.isCreative()) return;

        var values = skill.getValueSet("block_radius");
        if(values == null) return;

        float blockRadiusValue = values.getValue(lvl);
        int blockRadius = (int)blockRadiusValue;

        AABB searchBox = player.getBoundingBox().inflate(blockRadius);
        List<ItemEntity> items = player.level().getEntitiesOfClass(ItemEntity.class, searchBox);

        for(var item : items){
            if (item.hasPickUpDelay() || !item.isAlive()) continue;
            if(TagMatchTool.isItemInList(skill.getSpecificParameters(), "item_blacklist", item.getItem())) continue;

            Vec3 targetPos = player.position().add(0, 0.5, 0);
            Vec3 itemPos = item.position();
            Vec3 direction = targetPos.subtract(itemPos);

            if (direction.lengthSqr() > 0.01) {
                Vec3 motion = direction.normalize().scale(0.35);

                item.setDeltaMovement(item.getDeltaMovement().scale(0.5).add(motion));
            }
        }
    }

}
