package com.tyzsskills;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.TyzsSkillsAPI;
import com.tyzsskills.api.events.TyzsSkillsClientSetupEvent;
import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.records.SortType;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.client.events.ClientEvents;
import com.tyzsskills.impl.client.key.MainKeybind;
import com.tyzsskills.impl.client.models.InventoryButton;
import com.tyzsskills.impl.client.records.SkillTooltipData;
import com.tyzsskills.impl.client.screen.*;
import com.tyzsskills.impl.client.tools.SortingTools;
import com.tyzsskills.impl.client.wrappers.ClientCacheWrapper;
import com.tyzsskills.impl.client.wrappers.TyzsSkillsClientRegistrationWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Comparator;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = Tyzsskills.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = Tyzsskills.MODID, value = Dist.CLIENT)
public class TyzsskillsClient {

    public static net.neoforged.bus.api.IEventBus MOD_BUS;

    public TyzsskillsClient(ModContainer container) {
        MOD_BUS = container.getEventBus();

        NeoForge.EVENT_BUS.register(ClientEvents.class);

        //API client side
        TyzsSkillsAPI.registerPlayerCacheManager(new ClientCacheWrapper());

        //Config screen
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        MOD_BUS.post(new TyzsSkillsClientSetupEvent(new TyzsSkillsClientRegistrationWrapper()));
    }

    @SubscribeEvent
    static void onTyzsSkillsClientSetup(TyzsSkillsClientSetupEvent event){
        event.wrapper().registerSortingType(new SortType(
                "gui.tyzs_skills.sorting.name",
                Comparator.comparing(
                        (ISkill skill) -> Component.translatable(skill.getDisplayName()).getString(),
                        String.CASE_INSENSITIVE_ORDER
                ).thenComparing(ISkill::getID),
                94, 151,
                126, 151
        ));

        event.wrapper().registerSortingType(new SortType(
                "gui.tyzs_skills.sorting.price",
                Comparator.comparingInt((ISkill skill) -> skill.getPrice(ClientCache.getSkillLevel(skill.getID()) + 1)).thenComparing(ISkill::getID),
                78, 151,
                110, 151
        ));

        event.wrapper().registerSortingType(new SortType(
                "gui.tyzs_skills.sorting.level",
                Comparator.comparingInt((ISkill skill) -> ClientCache.getSkillLevel(skill.getID())).thenComparing(ISkill::getID),
                126, 167,
                142, 167
        ));

        event.wrapper().registerSortingType(new SortType(
                "gui.tyzs_skills.sorting.random",
                null,
                95, 167,
                111, 167
        ));
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
    public static void registerCustomTooltip(RegisterClientTooltipComponentFactoriesEvent event){
        event.register(SkillTooltipData.class, SkillTooltip::new);
    }

    @SubscribeEvent
    public static void onClientLogOut(ClientPlayerNetworkEvent.LoggingOut event){
        ClientCache.clearCache(Enums.ResetType.SHUTDOWN);
        SortingTools.clearData();
        SkillTriggerOverlay.Clear();
        XpTriggerOverlay.Clear();
        Tyzsskills.LOGGER.info("CACHE CLEARED");
    }

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event){
        event.register(MainKeybind.OPEN_SKILL_KEY);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event){
        while (MainKeybind.OPEN_SKILL_KEY.consumeClick()){
            if (Minecraft.getInstance().screen == null) {
                Minecraft.getInstance().setScreen(new MainGUI());
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
