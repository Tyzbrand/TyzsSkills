package com.tyzsskills.impl.server.active;

import com.tyzsskills.Tyzsskills;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AttributeRegistry {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, Tyzsskills.MODID);

    public static final DeferredHolder<Attribute, Attribute> SKILL_XP_MULTIPLIER = ATTRIBUTES.register("skill_xp_multiplier",
            () -> new RangedAttribute("attribute.name.tyzs_skills.skill_xp_multiplier", 1.0D, 0.0D, 1024.0D).setSyncable(true));

    public static final DeferredHolder<Attribute, Attribute> SP_MULTIPLIER = ATTRIBUTES.register("sp_multiplier",
            () -> new RangedAttribute("attribute.name.tyzs_skills.sp_multiplier", 1.0D, 0.0D, 1024.0D).setSyncable(true));

    public static final DeferredHolder<Attribute, Attribute> MAX_SPELL_SLOTS = ATTRIBUTES.register("max_spell_slots",
            () -> new RangedAttribute("attribute.name.tyzs_skills.max_spell_slots", 3, 1, 5).setSyncable(true));

}
