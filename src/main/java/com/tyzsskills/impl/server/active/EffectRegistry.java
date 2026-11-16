package com.tyzsskills.impl.server.active;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.impl.server.mob_effects.ReprieveMobEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EffectRegistry {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, Tyzsskills.MODID);

    public static final DeferredHolder<MobEffect, MobEffect> REPRIEVE =
            MOB_EFFECTS.register("reprieve", ReprieveMobEffect::new);
}
