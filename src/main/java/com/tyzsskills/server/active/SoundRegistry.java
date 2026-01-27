package com.tyzsskills.server.active;

import com.tyzsskills.Tyzsskills;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.concurrent.ThreadLocalRandom;

public class SoundRegistry {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, Tyzsskills.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> UI_CLICK_2 = SOUND_EVENTS.register("ui-click_2",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "ui-click_2")));

    public static final DeferredHolder<SoundEvent, SoundEvent> UI_CLICK_3 = SOUND_EVENTS.register("ui-click_3",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "ui-click_3")));

    public static void PlayUIClick(){
        SoundEvent[] uiSounds = {UI_CLICK_2.get(), UI_CLICK_3.get()};
        var soundToPlay = uiSounds[ThreadLocalRandom.current().nextInt(uiSounds.length)];

        float pitch = (.9f + (float)Math.random() * .2f);

        Minecraft.getInstance().getSoundManager().play(
                SimpleSoundInstance.forUI(soundToPlay, pitch, .7f)
        );
    }

    public static void register(IEventBus eventBus){
        SOUND_EVENTS.register(eventBus);
    }
}
