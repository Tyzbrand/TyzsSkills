package com.tyzsskills.impl.server.effects.skillEffects;

import com.tyzsskills.Config;
import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.impl.server.model.SkillBehavior;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.LightLayer;

public class DeepSightEffect extends SkillBehavior {

    @Override
    public void onPlayerTick(ServerPlayer player, int lvl, ISkill skill) {
        if(player.tickCount % 20 != 0) return;

        var world = player.level();
        var pos = player.blockPosition();

        if(world.canSeeSky(pos)) return;
        if(world.getBrightness(LightLayer.SKY, pos) > Config.SKY_BRIGHTNESS_THRESHOLD.get()) return;
        if(world.getBrightness(LightLayer.BLOCK, pos) >= Config.AMBIENT_BRIGHTNESS_THRESHOLD.get()) return;

        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0, false, false, false));

    }
}
