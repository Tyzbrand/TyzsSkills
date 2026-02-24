package com.tyzsskills;

import com.tyzsskills.api.TyzsSkillsAPI;
import com.tyzsskills.impl.server.active.AttributeRegistry;
import com.tyzsskills.impl.server.active.ErrorManager;
import com.tyzsskills.impl.server.active.SoundRegistry;
import com.tyzsskills.impl.server.attachments.*;
import com.tyzsskills.impl.server.active.FileManager;
import com.tyzsskills.impl.server.events.SkillEffectsEvents;
import com.tyzsskills.impl.server.events.XpGainsEvents;
import com.tyzsskills.impl.server.skills.SkillBehaviorRegistry;
import com.tyzsskills.impl.server.skills.SkillManager;
import com.tyzsskills.impl.server.commands.MainCommand;
import com.tyzsskills.impl.server.events.RuntimeEvents;
import com.tyzsskills.impl.server.payloads.*;
import com.tyzsskills.impl.server.wrappers.*;
import com.tyzsskills.impl.server.xp.XpManager;
import com.tyzsskills.impl.server.xp.xpEvents.XpBlock;
import com.tyzsskills.impl.server.xp.xpEvents.XpEntity;
import com.tyzsskills.integration.kubejs.JsEventsDelegate;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
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


    public Tyzsskills(IEventBus modEventBus, ModContainer modContainer) {

        //API
        RegisterWrappers();

       //Register attributes
        AttributeRegistry.ATTRIBUTES.register(modEventBus);
        modEventBus.addListener(this::RegisterAttributes);

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
        modEventBus.addListener(this::RegisterPayloads);

        //Register commands
        NeoForge.EVENT_BUS.addListener(RegisterCommandsEvent.class, this::RegisterCommands);

        // Register config (Gameplay)
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);

        // Register config (Visual)
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);

        //Initialize skill behaviours
        SkillBehaviorRegistry.Init();
    }


    private void RegisterCommands(RegisterCommandsEvent event){
        event.getDispatcher().register(MainCommand.register());
    }

    private void RegisterWrappers(){
        TyzsSkillsAPI.registerSpManager(new SpWrapper());
        TyzsSkillsAPI.registerLevelManager(new LevelWrapper());
        TyzsSkillsAPI.registerXpManager(new XpWrapper());
        TyzsSkillsAPI.registerSkillManager(new SkillWrapper());
        TyzsSkillsAPI.registerPowerManager(new PowerWrapper());
    }

    private void RegisterAttributes(EntityAttributeModificationEvent event) {
        if (!event.has(EntityType.PLAYER, AttributeRegistry.SKILL_XP_MULTIPLIER)) {
            event.add(EntityType.PLAYER, AttributeRegistry.SKILL_XP_MULTIPLIER);
        }

        if (!event.has(EntityType.PLAYER, AttributeRegistry.SP_MULTIPLIER)) {
            event.add(EntityType.PLAYER, AttributeRegistry.SP_MULTIPLIER);
        }

        if (!event.has(EntityType.PLAYER, AttributeRegistry.TRAIT_POWER)) {
            event.add(EntityType.PLAYER, AttributeRegistry.TRAIT_POWER);
        }
    }

    private void RegisterPayloads(final RegisterPayloadHandlersEvent event){
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
                PowerUpdatePayload.TYPE,
                PowerUpdatePayload.STREAM_CODEC,
                PowerUpdatePayload::Handle
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

    }

    @SubscribeEvent
    public void OnServerBeforeStart(ServerAboutToStartEvent event) throws IOException {
        var fileManager = FileManager.Get();
        var server = event.getServer();

        fileManager.InitPath(server);
        fileManager.LoadDefaultJson(server);
        fileManager.LoadDefaultXpValues(server);
        fileManager.LoadDefaulltLevelPool(server);

        fileManager.ReadJsons(server);
        fileManager.ReadXpValues(server);
        fileManager.ReadLevelPool(server);
        fileManager.ReadCustomSkills(server);
    }

    @SubscribeEvent
    public void OnServerStop(ServerStoppingEvent event){
        SkillManager.Get().clearSkills();
        XpBlock.ClearValues();
        XpEntity.ClearValues();
        XpManager.clearPool();

        ErrorManager.ClearErrors();
    }






}
