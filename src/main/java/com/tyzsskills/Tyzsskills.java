package com.tyzsskills;

import com.mojang.logging.LogUtils;
import com.tyzsskills.api.events.TyzsSkillsCommonSetupEvent;
import com.tyzsskills.impl.server.active.AttributeRegistry;
import com.tyzsskills.impl.server.active.EffectRegistry;
import com.tyzsskills.impl.server.active.SoundRegistry;
import com.tyzsskills.impl.server.attachments.*;
import com.tyzsskills.impl.server.events.RuntimeEvents;
import com.tyzsskills.impl.server.events.XpGainsEvents;
import com.tyzsskills.impl.server.payloads.*;
import com.tyzsskills.impl.server.skills.SkillPresets;
import com.tyzsskills.impl.server.spells.SpellPresets;
import com.tyzsskills.impl.server.wrappers.TyzsSkillsCommonRegistrationWrapper;
import com.tyzsskills.integration.kubejs.JsEventsDelegate;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

@Mod(Tyzsskills.MODID)
public class Tyzsskills {
    public static final String MODID = "tyzs_skills";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static net.neoforged.bus.api.IEventBus MOD_BUS;
    public static IEventBus getModBus(){
        if(MOD_BUS == null) throw  new IllegalStateException("Attempt to Access MOD_BUS (server) While it's not yet Initialized.");
        return MOD_BUS;
    }

    public Tyzsskills(IEventBus modEventBus, ModContainer modContainer) {
        MOD_BUS = modContainer.getEventBus();

        modEventBus.register(this);

        Settings.Server.init();

       //Register attributes
        AttributeRegistry.ATTRIBUTES.register(modEventBus);

        //Register mob effects
        EffectRegistry.MOB_EFFECTS.register(modEventBus);

        //Register Attachments
        BlockMarker.ATTACHMENT_TYPES.register(modEventBus);
        LegacyData.ATTACHMENT_TYPES.register(modEventBus);
        ExplorationProgression.ATTACHMENT_TYPES.register(modEventBus);
        StatsTracker.ATTACHMENT_TYPES.register(modEventBus);
        PlayerData.ATTACHMENT_TYPES.register(modEventBus);

        //Register Sounds
        SoundRegistry.register(modEventBus);

        NeoForge.EVENT_BUS.register(RuntimeEvents.class);
        NeoForge.EVENT_BUS.register(XpGainsEvents.class);
        NeoForge.EVENT_BUS.register(JsEventsDelegate.class);
    }

    //EVENTS
    @SubscribeEvent
    public void registerAttributes(EntityAttributeModificationEvent event) {
        if (!event.has(EntityType.PLAYER, AttributeRegistry.SKILL_XP_MULTIPLIER)) {
            event.add(EntityType.PLAYER, AttributeRegistry.SKILL_XP_MULTIPLIER);
        }

        if (!event.has(EntityType.PLAYER, AttributeRegistry.SP_MULTIPLIER)) {
            event.add(EntityType.PLAYER, AttributeRegistry.SP_MULTIPLIER);
        }

        if (!event.has(EntityType.PLAYER, AttributeRegistry.EXTRA_SPELL_SLOTS)) {
            event.add(EntityType.PLAYER, AttributeRegistry.EXTRA_SPELL_SLOTS);
        }
    }

    @SubscribeEvent
    public void registerPayloads(final RegisterPayloadHandlersEvent event){
        final PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(
                UpdatePayloads.InitPayload.TYPE,
                UpdatePayloads.InitPayload.STREAM_CODEC,
                UpdatePayloads.InitPayload::Handle
        );

        registrar.playToClient(
                UpdatePayloads.LevelPayload.TYPE,
                UpdatePayloads.LevelPayload.STREAM_CODEC,
                UpdatePayloads.LevelPayload::Handle
        );

        registrar.playToClient(
                UpdatePayloads.SpPayload.TYPE,
                UpdatePayloads.SpPayload.STREAM_CODEC,
                UpdatePayloads.SpPayload::Handle
        );

        registrar.playToClient(
                UpdatePayloads.XpPayload.TYPE,
                UpdatePayloads.XpPayload.STREAM_CODEC,
                UpdatePayloads.XpPayload::Handle
        );

        registrar.playToClient(
                UpdatePayloads.LevelDataPayload.TYPE,
                UpdatePayloads.LevelDataPayload.STREAM_CODEC,
                UpdatePayloads.LevelDataPayload::Handle
        );

        registrar.playToClient(
                UpdatePayloads.SkillLevelPayload.TYPE,
                UpdatePayloads.SkillLevelPayload.STREAM_CODEC,
                UpdatePayloads.SkillLevelPayload::Handle
        );

        registrar.playToClient(
                UpdatePayloads.BookmarksPayload.TYPE,
                UpdatePayloads.BookmarksPayload.STREAM_CODEC,
                UpdatePayloads.BookmarksPayload::Handle
        );

        registrar.playToClient(
                UpdatePayloads.DeactivationsPayload.TYPE,
                UpdatePayloads.DeactivationsPayload.STREAM_CODEC,
                UpdatePayloads.DeactivationsPayload::Handle
        );

        registrar.playToServer(
                CActionSkillPayload.TYPE,
                CActionSkillPayload.STREAM_CODEC,
                CActionSkillPayload::Handle
        );

        registrar.playToServer(
                CActionSpellPayload.TYPE,
                CActionSpellPayload.STREAM_CODEC,
                CActionSpellPayload::Handle
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
                ExportPayload.TYPE,
                ExportPayload.STREAM_CODEC,
                ExportPayload::Handle
        );

        registrar.playToClient(
                UpdatePayloads.SpellPurchasePayload.TYPE,
                UpdatePayloads.SpellPurchasePayload.STREAM_CODEC,
                UpdatePayloads.SpellPurchasePayload::Handle
        );

        registrar.playToClient(
                UpdatePayloads.PropertyLevelPayload.TYPE,
                UpdatePayloads.PropertyLevelPayload.STREAM_CODEC,
                UpdatePayloads.PropertyLevelPayload::Handle
        );

        registrar.playToClient(
                UpdatePayloads.SpellSlotChange.TYPE,
                UpdatePayloads.SpellSlotChange.STREAM_CODEC,
                UpdatePayloads.SpellSlotChange::Handle
        );

        registrar.playToClient(
                UpdatePayloads.SpellCooldownChange.TYPE,
                UpdatePayloads.SpellCooldownChange.STREAM_CODEC,
                UpdatePayloads.SpellCooldownChange::Handle
        );

        registrar.playToClient(
                ClientReloadPayload.TYPE,
                ClientReloadPayload.STREAM_CODEC,
                ClientReloadPayload::Handle
        );
    }

    @SubscribeEvent
    public void onServerSetup(FMLCommonSetupEvent event){
        getModBus().post(new TyzsSkillsCommonSetupEvent(new TyzsSkillsCommonRegistrationWrapper()));
    }

    @SubscribeEvent
    public void onTyzsSkillsCommonSetup(TyzsSkillsCommonSetupEvent event){
        for(var prefab : SkillPresets.getDefaultSkills()){
            event.wrapper().registerSkill(prefab);
        }

        for(var prefab : SpellPresets.getDefaultSpells()){
            event.wrapper().registerSpell(prefab);
        }
    }
}
