package com.tyzsskills.server.effects.skillEffects;

import com.tyzsskills.server.model.Skill;
import com.tyzsskills.server.model.SkillBehaviour;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.LightLayer;

public class DeepSightEffect extends SkillBehaviour {

    @Override
    public void onPlayerTick(ServerPlayer player, int lvl, Skill skill) {
        if(player.tickCount % 20 != 0) return;

        var world = player.level();
        var pos = player.blockPosition();

        if(world.canSeeSky(pos)) return;
        if(world.getBrightness(LightLayer.SKY, pos) > 0) return;
        if(world.getBrightness(LightLayer.BLOCK, pos) >= 5) return;

        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0, false, false, false));

    }
}
