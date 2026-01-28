package com.tyzsskills.client;

import com.tyzsskills.server.active.SoundRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;

import java.util.concurrent.ThreadLocalRandom;

public class SoundPlayer {
    public static void PlayUIClick(){
        SoundEvent[] uiSounds = {SoundRegistry.UI_CLICK_2.get(), SoundRegistry.UI_CLICK_3.get()};
        var soundToPlay = uiSounds[ThreadLocalRandom.current().nextInt(uiSounds.length)];

        float pitch = (.9f + (float)Math.random() * .2f);

        Minecraft.getInstance().getSoundManager().play(
                SimpleSoundInstance.forUI(soundToPlay, pitch, .7f)
        );
    }

    public static void PlayLevelUpSound(){
        Minecraft.getInstance().getSoundManager().play(
                SimpleSoundInstance.forUI(SoundRegistry.LEVEL_UP_SOUND.get(), 1f, .7f)
        );
    }
}
