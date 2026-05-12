package com.tyzsskills;

import com.tyzsskills.api.TyzsSkillsAPI;
import com.tyzsskills.api.events.TyzsSkillsCommonSetupEvent;
import com.tyzsskills.impl.server.active.*;
import com.tyzsskills.impl.server.attachments.*;
import com.tyzsskills.impl.server.categories.CategoryLoader;
import com.tyzsskills.impl.server.events.SkillEffectsEvents;
import com.tyzsskills.impl.server.events.XpGainsEvents;
import com.tyzsskills.impl.server.skills.SkillPresets;
import com.tyzsskills.impl.server.skills.SkillManager;
import com.tyzsskills.impl.server.commands.MainCommand;
import com.tyzsskills.impl.server.events.RuntimeEvents;
import com.tyzsskills.impl.server.payloads.*;
import com.tyzsskills.impl.server.wrappers.*;
import com.tyzsskills.impl.server.xp.XpGainRegistry;
import com.tyzsskills.impl.server.xp.XpManager;
import com.tyzsskills.integration.kubejs.JsEventsDelegate;
import net.minecraft.world.entity.EntityType;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;

import java.io.IOException;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Tyzsskills.MODID)
public class Tyzsskills {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "tyzs_skills";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    public static net.neoforged.bus.api.IEventBus MOD_BUS;


    public Tyzsskills(IEventBus modEventBus, ModContainer modContainer) {
        MOD_BUS = modContainer.getEventBus();

        //API
        registerWrappers();

       //Register attributes
        AttributeRegistry.ATTRIBUTES.register(modEventBus);


        modEventBus.addListener(this::registerAttributes);
        modEventBus.addListener(this::reloadConfig);
        modEventBus.addListener(this::onServerSetup);
        modEventBus.addListener(this::onTyzsSkillsCommonSetup);

        //Register Attachments
        BlockMarker.ATTACHMENT_TYPES.register(modEventBus);
        LegacyData.ATTACHMENT_TYPES.register(modEventBus);
        ExplorationProgression.ATTACHMENT_TYPES.register(modEventBus);
        StatsTracker.ATTACHMENT_TYPES.register(modEventBus);
        PlayerData.ATTACHMENT_TYPES.register(modEventBus);
        LimitsTracker.ATTACHMENT_TYPES.register(modEventBus);

        //Register Sounds
        SoundRegistry.register(modEventBus);



        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (Tyzsskills) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(RuntimeEvents.class);
        NeoForge.EVENT_BUS.register(XpGainsEvents.class);
        NeoForge.EVENT_BUS.register(SkillEffectsEvents.class);
        NeoForge.EVENT_BUS.register(JsEventsDelegate.class);

        //Register network
        modEventBus.addListener(this::registerPayloads);

        //Register commands
        NeoForge.EVENT_BUS.addListener(RegisterCommandsEvent.class, this::registerCommands);

        // Register config (Gameplay)
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);

        // Register config (Visual)
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);
    }


    private void registerCommands(RegisterCommandsEvent event){
        event.getDispatcher().register(MainCommand.register());
    }

    private void registerWrappers(){
        TyzsSkillsAPI.registerSpManager(new SpWrapper());
        TyzsSkillsAPI.registerLevelManager(new LevelWrapper());
        TyzsSkillsAPI.registerXpManager(new XpWrapper());
        TyzsSkillsAPI.registerSkillManager(new SkillWrapper());
    }

    private void registerAttributes(EntityAttributeModificationEvent event) {
        if (!event.has(EntityType.PLAYER, AttributeRegistry.SKILL_XP_MULTIPLIER)) {
            event.add(EntityType.PLAYER, AttributeRegistry.SKILL_XP_MULTIPLIER);
        }

        if (!event.has(EntityType.PLAYER, AttributeRegistry.SP_MULTIPLIER)) {
            event.add(EntityType.PLAYER, AttributeRegistry.SP_MULTIPLIER);
        }
    }

    private void reloadConfig(ModConfigEvent.Reloading event){
        if(!event.getConfig().getModId().equals(MODID)) return;

        if(event.getConfig().getType() != ModConfig.Type.COMMON) return;

        var server = ServerLifecycleHooks.getCurrentServer();
        if(server == null) return;

        for(var player : server.getPlayerList().getPlayers()) ClientSynchronizer.syncConfig(player);
    }

    private void registerPayloads(final RegisterPayloadHandlersEvent event){
        final PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(
                LevelUpdatePayload.TYPE,
                LevelUpdatePayload.STREAM_CODEC,
                LevelUpdatePayload::Handle
        );

        registrar.playToClient(
                SpUpdatePayload.TYPE,
                SpUpdatePayload.STREAM_CODEC,
                SpUpdatePayload::Handle
        );

        registrar.playToClient(
                XpUpdatePayload.TYPE,
                XpUpdatePayload.STREAM_CODEC,
                XpUpdatePayload::Handle
        );

        registrar.playToClient(
                LevelDataUpdatePayload.TYPE,
                LevelDataUpdatePayload.STREAM_CODEC,
                LevelDataUpdatePayload::Handle
        );

        registrar.playToClient(
                SkillSyncPayload.TYPE,
                SkillSyncPayload.STREAM_CODEC,
                SkillSyncPayload::Handle
        );

        registrar.playToClient(
                SkillLevelSyncPayload.TYPE,
                SkillLevelSyncPayload.STREAM_CODEC,
                SkillLevelSyncPayload::Handle
        );

        registrar.playToClient(
                ConfigSyncPayload.TYPE,
                ConfigSyncPayload.STREAM_CODEC,
                ConfigSyncPayload::Handle
        );

        registrar.playToServer(
                CActionSkillPayload.TYPE,
                CActionSkillPayload.STREAM_CODEC,
                CActionSkillPayload::Handle
        );

        registrar.playToClient(
                SkillTriggerPayload.TYPE,
                SkillTriggerPayload.STREAM_CODEC,
                SkillTriggerPayload::Handle
        );

        registrar.playToClient(
                LevelToastPayload.TYPE,
                LevelToastPayload.STREAM_CODEC,
                LevelToastPayload::Handle
        );

        registrar.playToClient(
                SkillBookmarksPayload.TYPE,
                SkillBookmarksPayload.STREAM_CODEC,
                SkillBookmarksPayload::Handle
        );


        registrar.playToClient(
                StatsXpPayload.TYPE,
                StatsXpPayload.STREAM_CODEC,
                StatsXpPayload::Handle
        );

        registrar.playToClient(
                StatsSpEarnedPayload.TYPE,
                StatsSpEarnedPayload.STREAM_CODEC,
                StatsSpEarnedPayload::Handle
        );

        registrar.playToClient(
                StatsSpSpentPayload.TYPE,
                StatsSpSpentPayload.STREAM_CODEC,
                StatsSpSpentPayload::Handle
        );

        registrar.playToClient(
                ResetPayload.TYPE,
                ResetPayload.STREAM_CODEC,
                ResetPayload::Handle
        );

        registrar.playToClient(
                ExportPayload.TYPE,
                ExportPayload.STREAM_CODEC,
                ExportPayload::Handle
        );

        registrar.playToClient(
                CategoriesPayload.TYPE,
                CategoriesPayload.STREAM_CODEC,
                CategoriesPayload::Handle
        );

    }



    private void onServerSetup(FMLCommonSetupEvent event){
        MOD_BUS.post(new TyzsSkillsCommonSetupEvent(new TyzsSkillsCommonRegistrationWrapper()));
    }

    private void onTyzsSkillsCommonSetup(TyzsSkillsCommonSetupEvent event){
        for(var prefab : SkillPresets.getDefaultSkills()){
            event.wrapper().registerSkill(prefab);
        }
    }

    @SubscribeEvent
    public void onServerBeforeStart(ServerAboutToStartEvent event) {
        var server = event.getServer();

        try{
            FileManager.init(server);

            FileManager.writeDefaultSkills(server);
            FileManager.writeDefaultData(server);

            FileManager.readSkills(server);
            FileManager.readData(server);
        }
        catch (IOException e) {
        ErrorManager.registerLoadError("Loading json files", "Check the logs for more details");
        System.err.println("[Tyz's Skills] CRITICAL ERROR: Unable to load files during server start");
        e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onServerStop(ServerStoppingEvent event){
        SkillManager.get().clearSkills();

        XpGainRegistry.clearAll();
        XpManager.clearPool();
        CategoryLoader.clearCategories();

        FileManager.clearPrefab();

        ErrorManager.clearErrors();
    }








}
