package com.tyzsskills.impl.server.effects.spellEffects;

import com.tyzsskills.api.TyzsSkillsAPI;
import com.tyzsskills.api.interfaces.ISpell;
import com.tyzsskills.api.model.SpellBehavior;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class DashEffect extends SpellBehavior {

    @Override
    public boolean onSpellCast(@NotNull ServerPlayer player, @NotNull ISpell spell) {
        var speedProp = spell.getProperty("dash_speed");
        if(speedProp == null) return false;

        var rawSpeed = speedProp.getValue(TyzsSkillsAPI.spells().getPropertyLevel(player, speedProp));

        var currentVelocity = player.getDeltaMovement().scale(0.33);
        var dashVelocity = player.getLookAngle().scale(rawSpeed);

        var finalVelocity = dashVelocity.add(currentVelocity);
        if (finalVelocity.length() > 3.85) finalVelocity = finalVelocity.normalize().scale(3.85);


        player.setDeltaMovement(finalVelocity);
        player.hurtMarked = true;

        return true;
    }
}
