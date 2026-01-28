package com.tyzsskills.server.active;

import com.tyzsskills.Tyzsskills;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


public class SoundRegistry {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, Tyzsskills.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> UI_CLICK_2 = SOUND_EVENTS.register("ui-click_2",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "ui-click_2")));

    public static final DeferredHolder<SoundEvent, SoundEvent> UI_CLICK_3 = SOUND_EVENTS.register("ui-click_3",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "ui-click_3")));

    public static final DeferredHolder<SoundEvent, SoundEvent> LEVEL_UP_SOUND = SOUND_EVENTS.register("level_up_sound",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "level_up_sound")));


    public static void register(IEventBus eventBus){
        SOUND_EVENTS.register(eventBus);
    }
}
