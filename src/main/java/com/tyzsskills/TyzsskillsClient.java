package com.tyzsskills;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.events.TyzsSkillsClientSetupEvent;
import com.tyzsskills.impl.client.active.SortingManager;
import com.tyzsskills.impl.client.cache.ClientCache;
import com.tyzsskills.impl.client.key.MainKeybind;
import com.tyzsskills.impl.client.models.InventoryButton;
import com.tyzsskills.impl.client.overlay.LevelTriggerOverlay;
import com.tyzsskills.impl.client.overlay.SkillTriggerOverlay;
import com.tyzsskills.impl.client.overlay.XpTriggerOverlay;
import com.tyzsskills.impl.client.screen.MainMenu;
import com.tyzsskills.impl.client.screen.SpellWheel;
import com.tyzsskills.impl.client.wrappers.TyzsSkillsClientRegistrationWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = Tyzsskills.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = Tyzsskills.MODID, value = Dist.CLIENT)
public class TyzsskillsClient {

    private static net.neoforged.bus.api.IEventBus MOD_BUS;
    public static IEventBus getModBus(){
        if(MOD_BUS == null) throw  new IllegalStateException("Attempt to Access MOD_BUS (client) While it's not yet Initialized.");
        return MOD_BUS;
    }

    public TyzsskillsClient(ModContainer container) {
        MOD_BUS = container.getEventBus();

        //Config screen
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        getModBus().post(new TyzsSkillsClientSetupEvent(new TyzsSkillsClientRegistrationWrapper()));
    }

    @SubscribeEvent
    static void onTyzsSkillsClientSetup(TyzsSkillsClientSetupEvent event){

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
        ClientCache.deleteCache();
        SortingManager.clearData();
        SkillTriggerOverlay.Clear();
        XpTriggerOverlay.Clear();
        Tyzsskills.LOGGER.info("CACHE CLEARED");
    }

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event){
        event.register(MainKeybind.OPEN_SKILL_MENU);
        event.register(MainKeybind.OPEN_SPELL_WHEEL);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event){
        while (MainKeybind.OPEN_SKILL_MENU.consumeClick()){
            if (Minecraft.getInstance().screen == null) {
                Minecraft.getInstance().setScreen(new MainMenu());
            }
        }

        while (MainKeybind.OPEN_SPELL_WHEEL.consumeClick()){
            if (Minecraft.getInstance().screen == null) {
                Minecraft.getInstance().setScreen(new SpellWheel(Enums.WheelMode.CAST, null));
            }
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
                        Minecraft.getInstance().setScreen(new MainMenu());
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
