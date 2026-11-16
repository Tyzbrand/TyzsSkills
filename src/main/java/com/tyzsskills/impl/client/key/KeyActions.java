package com.tyzsskills.impl.client.key;

import com.tyzsskills.Tyzsskills;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = Tyzsskills.MODID, value = Dist.CLIENT)
public class KeyActions {

    private static final Map<KeyMapping, Runnable> registeredInputs = new HashMap<>();
    public static void clearInputs(){registeredInputs.clear();}

    public static KeyMapping registerInput(@NotNull RegisterKeyMappingsEvent event, @NotNull KeyMapping key, @NotNull Runnable onPress) {
        event.register(key);
        registeredInputs.put(key, onPress);
        return key;
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event){
        var mc = Minecraft.getInstance();
        if(mc.screen != null || mc.player == null) return;

        for(var kvp : registeredInputs.entrySet()){
            onPress(kvp.getKey(), kvp.getValue());
        }
    }

    public static void onPress(KeyMapping key, Runnable onPress){
        var pressed = false;
        while(key.consumeClick()) pressed = true;
        if(pressed) onPress.run();
    }
}
