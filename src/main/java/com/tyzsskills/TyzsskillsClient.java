package com.tyzsskills;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.events.client.TyzsSkillsClientSetupEvent;
import com.tyzsskills.impl.client.active.SortingManager;
import com.tyzsskills.impl.client.cache.ClientCache;
import com.tyzsskills.impl.client.key.Keybinds;
import com.tyzsskills.impl.client.models.InventoryButton;
import com.tyzsskills.impl.client.screen.MainMenu;
import com.tyzsskills.impl.client.wrappers.TyzsSkillsClientRegistrationWrapper;
import com.tyzsskills.impl.server.active.ErrorManager;
import com.tyzsskills.impl.server.mob_effects.ReprieveMobEffect;
import com.tyzsskills.integration.ui.models.UIImage;
import com.tyzsskills.integration.ui.models.UIOverlay;
import com.tyzsskills.integration.ui.records.UITexture;
import com.tyzsskills.integration.ui.renderers.UIOverlayRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
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

        Settings.Client.init();
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        getModBus().post(new TyzsSkillsClientSetupEvent(new TyzsSkillsClientRegistrationWrapper()));
    }

    @SubscribeEvent
    static void onModSetup(TyzsSkillsClientSetupEvent event){
        Settings.Client.readSettings();
    }


    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event){
        event.registerAbove(VanillaGuiLayers.CROSSHAIR,
                ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "overlay_renderer"),
                new UIOverlayRenderer());
    }

    @SubscribeEvent
    public static void onClientLogIn(ClientPlayerNetworkEvent.LoggingIn event){
        registerStaticOverlays();
        ErrorManager.printErrors(event.getPlayer(), true);
    }


    @SubscribeEvent
    public static void onClientLogOut(ClientPlayerNetworkEvent.LoggingOut event){
        ClientCache.deleteCache();
        SortingManager.clearData();
        UIOverlayRenderer.clear();
        Settings.Server.resetSettings();
        Tyzsskills.LOGGER.info("CACHE CLEARED");
    }

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event){
        Keybinds.registerKeybinds(event);
    }


    @SubscribeEvent
    public static void onCrosshairRender(RenderGuiLayerEvent.Pre event){
        if (!event.getName().equals(VanillaGuiLayers.CROSSHAIR)) return;

        var player = Minecraft.getInstance().player;
        if(player == null) return;
        if(ReprieveMobEffect.isAffected(player)) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof InventoryScreen || event.getScreen() instanceof CreativeModeInventoryScreen) {

            if(!Settings.Client.INVENTORY_BUTTON.get()) return;

            AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) event.getScreen();

            int savedX = Settings.Client.INVENTORY_X.get();
            int savedY = Settings.Client.INVENTORY_Y.get();

            InventoryButton myButton = new InventoryButton(
                    savedX,
                    savedY,
                    () -> Minecraft.getInstance().setScreen(new MainMenu())

            );

            if (savedX == 10 && savedY == 10) {
                boolean overlap = event.getListenersList().stream()
                        .filter(l -> l instanceof AbstractWidget)
                        .map(l -> (AbstractWidget) l)
                        .anyMatch(w -> w.getX() < savedX + 20 && w.getX() + w.getWidth() > savedX
                                && w.getY() < savedY + 20 && w.getY() + w.getHeight() > savedY);

                if (overlap) {
                    myButton.setX(screen.getGuiLeft() + screen.getXSize() + 5);
                    myButton.setY(screen.getGuiTop() + 5);
                }
            }
            event.addListener(myButton);
        }
    }

    //UTILS
    private static void registerStaticOverlays(){
        var overlay = new UIOverlay(0, 0, -1f).withAnchor(Enums.OverlayAnchor.CENTER);
        overlay.withVisibility(() -> {
            var player = Minecraft.getInstance().player;
            return player != null && ReprieveMobEffect.isAffected(Minecraft.getInstance().player);
        });

        var image = new UITexture(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "textures/gui/icons/control_icon.png"), 16);
        overlay.addChild(new UIImage(0, 0, 16, 16, () -> image)
                .withScale(.85f, Enums.ScalePivot.CENTER)
                .withAlpha(() -> .5f));

        UIOverlayRenderer.add(overlay);
    }

}
