package com.tyzsskills;

import com.tyzsskills.client.ClientCache;
import com.tyzsskills.client.key.MainKeybind;
import com.tyzsskills.client.screen.LevelTriggerOverlay;
import com.tyzsskills.client.screen.MainGUI;
import com.tyzsskills.client.screen.SkillTriggerOverlay;
import com.tyzsskills.client.screen.XpTriggerOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;
import net.neoforged.neoforge.client.event.sound.SoundEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = Tyzsskills.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = Tyzsskills.MODID, value = Dist.CLIENT)
public class TyzsskillsClient {
    public TyzsskillsClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.


        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        Tyzsskills.LOGGER.info("HELLO FROM CLIENT SETUP");
        Tyzsskills.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event){
        event.registerAbove(VanillaGuiLayers.CROSSHAIR,
                ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "skill_notification"),
                new SkillTriggerOverlay());

        event.registerAbove(VanillaGuiLayers.CROSSHAIR,
                ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "xp_notification"),
                new XpTriggerOverlay());

        event.registerAbove(VanillaGuiLayers.CROSSHAIR,
                ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "level_notification"),
                new LevelTriggerOverlay());
    }

    @SubscribeEvent
    public static void onClientLogOut(ClientPlayerNetworkEvent.LoggingOut event){
        ClientCache.ClearCache();
        SkillTriggerOverlay.Clear();
        XpTriggerOverlay.Clear();
        Tyzsskills.LOGGER.info("CACHE CLEARED");
    }

    @SubscribeEvent
    public static void RegisterKeys(RegisterKeyMappingsEvent event){
        event.register(MainKeybind.OPEN_SKILL_KEY);
    }

    @SubscribeEvent
    public static void OnClientTick(ClientTickEvent.Post event){
        while (MainKeybind.OPEN_SKILL_KEY.consumeClick()){
            Minecraft.getInstance().setScreen(new MainGUI());
        }
    }


}
