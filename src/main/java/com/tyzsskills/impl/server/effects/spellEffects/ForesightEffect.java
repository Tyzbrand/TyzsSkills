package com.tyzsskills.impl.server.effects.spellEffects;

import com.tyzsskills.api.TyzsSkillsAPI;
import com.tyzsskills.api.interfaces.ISpell;
import com.tyzsskills.api.model.SpellBehavior;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class ForesightEffect extends SpellBehavior {

    @Override
    public boolean onSpellCast(@NotNull ServerPlayer player, @NotNull ISpell spell) {
        var radiusProp = spell.getProperty("block_radius");
        var durationProp = spell.getProperty("effect_duration");
        if(radiusProp == null ||durationProp == null) return false;

        var radius = radiusProp.getValue(TyzsSkillsAPI.spells().getPropertyLevel(player, radiusProp));
        var duration = durationProp.getValue(TyzsSkillsAPI.spells().getPropertyLevel(player, durationProp));

        var searchBox = player.getBoundingBox().inflate(radius);
        var entities = player.level().getEntitiesOfClass(LivingEntity.class, searchBox);

        for(var entity : entities){
            if(entity == player) continue;
            entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, Math.round(duration * 20), 0));
        }

        return true;
    }
}
