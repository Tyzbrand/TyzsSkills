package com.tyzsskills;

import com.tyzsskills.client.ClientCache;
import com.tyzsskills.client.key.MainKeybind;
import com.tyzsskills.client.models.InventoryButton;
import com.tyzsskills.client.screen.LevelTriggerOverlay;
import com.tyzsskills.client.screen.MainGUI;
import com.tyzsskills.client.screen.SkillTriggerOverlay;
import com.tyzsskills.client.screen.XpTriggerOverlay;
import com.tyzsskills.server.model.Skill;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
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

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof InventoryScreen || event.getScreen() instanceof CreativeModeInventoryScreen) {

            if(!Config.SHOW_SKILL_BUTTON.get()) return;

            AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) event.getScreen();

            int savedX = Config.INVENTORY_BUTTON_X.get();
            int savedY = Config.INVENTORY_BUTTON_Y.get();

            InventoryButton myButton = new InventoryButton(
                    savedX,
                    savedY,
                    () -> {
                        Minecraft.getInstance().setScreen(new MainGUI());
                    }
            );

            if (savedX == 10 && savedY == 10) {
                boolean overlap = event.getListenersList().stream()
                        .filter(l -> l instanceof net.minecraft.client.gui.components.AbstractWidget)
                        .map(l -> (net.minecraft.client.gui.components.AbstractWidget) l)
                        .anyMatch(w -> w.getX() < savedX + 20 && w.getX() + w.getWidth() > savedX &&
                                w.getY() < savedY + 20 && w.getY() + w.getHeight() > savedY);

                if (overlap) {
                    myButton.setX(screen.getGuiLeft() + screen.getXSize() + 5);
                    myButton.setY(screen.getGuiTop() + 5);
                }
            }
            event.addListener(myButton);
        }
    }


}
