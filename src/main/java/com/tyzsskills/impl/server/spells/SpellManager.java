package com.tyzsskills.impl.server.spells;

import com.tyzsskills.Config;
import com.tyzsskills.api.events.spells.*;
import com.tyzsskills.api.interfaces.ISpell;
import com.tyzsskills.api.model.Context;
import com.tyzsskills.api.records.Cooldown;
import com.tyzsskills.api.records.SpellProperty;
import com.tyzsskills.impl.server.active.AttributeRegistry;
import com.tyzsskills.impl.server.active.BehaviorRegistry;
import com.tyzsskills.impl.server.attachments.PlayerData;
import com.tyzsskills.impl.server.payloads.UpdatePayloads;
import com.tyzsskills.impl.server.skills.SkillManager;
import com.tyzsskills.impl.server.sp.SpManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

@ApiStatus.Internal
public class SpellManager {
    private static final Map<String, Spell> spellCollection = new HashMap<>();
    private static final Map<String, Spell> spellCollectionView = Collections.unmodifiableMap(spellCollection);

    private static final Context.Spell SPELL_CONTEXT = new Context.Spell();
    private static final Context.Property PROPERTY_CONTEXT = new Context.Property();

    //CORE
    private static boolean setPropertyLevelInternal(@NotNull ServerPlayer player, @NotNull SpellProperty property, int newLevel, boolean syncClient){
        var spell = getSpell(property.spellId());
        if(spell == null || !isSpellPurchased(player, spell)) return false;

        if(newLevel < 0 || newLevel > property.getMaximumLevel()) return false;
        int oldLvl = getPropertyLevel(player, property);

        if(oldLvl == newLevel) return false;

        player.getData(PlayerData.DATA).setSpellPropertyLevel(property.getFullId(), newLevel);
        NeoForge.EVENT_BUS.post(new SpellPropertyLevelChangeEvent(player, spell, property, oldLvl, newLevel));

        if(syncClient) PacketDistributor.sendToPlayer(player, new UpdatePayloads.PropertyLevelPayload(property.getFullId(), newLevel));

        return true;
    }

    //PROPERTIES
    public static boolean tryAddPropertyLevel(@NotNull ServerPlayer player, @NotNull SpellProperty property, int amount){
        return setPropertyLevelInternal(player, property, getPropertyLevel(player, property) + amount, true);
    }
    public static boolean tryRemovePropertyLevel(@NotNull ServerPlayer player, @NotNull SpellProperty property, int amount){
        return setPropertyLevelInternal(player, property, getPropertyLevel(player, property) - amount, true);
    }
    public static void setPropertiesLevel(@NotNull ServerPlayer player, @NotNull SpellProperty property, int newLevel){
        setPropertyLevelInternal(player, property, newLevel, true);
    }
    public static boolean tryBuyProperty(@NotNull ServerPlayer player, @NotNull SpellProperty property) {
        var spell = getSpell(property.spellId());
        if(spell == null || !isSpellPurchased(player, spell)) return false;

        int currentLvl = getPropertyLevel(player, property);

        if(SpellRules.canBuyProperty(getPropertyContext(player, property), SkillManager.getPlayerContext(player), Config.PURCHASE_SYSTEM.get())){
            var price = property.getPrice(currentLvl + 1);

            var preEvent = new SpellPropertyPurchaseEvent.Pre(player, spell, property, currentLvl, currentLvl+1);
            NeoForge.EVENT_BUS.post(preEvent);
            if(preEvent.isCanceled()) return false;

            if(setPropertyLevelInternal(player, property, currentLvl + 1, true)) {
                SpManager.tryRemoveSp(player, price);
                NeoForge.EVENT_BUS.post(new SpellPropertyPurchaseEvent.Post(player, spell, property, currentLvl, currentLvl+1));
                return true;
            }
        }
        return false;
    }

    //SPELLS
    public static boolean tryBuySpell(@NotNull ServerPlayer player, @NotNull ISpell spell){
        if(isSpellPurchased(player, spell)) return false;

        if(SpellRules.canBuySpell(getSpellContext(player, spell), SkillManager.getPlayerContext(player), Config.PURCHASE_SYSTEM.get())){
            var price = spell.getPrice();

            var preEvent = new SpellPurchaseEvent.Pre(player, spell);
            NeoForge.EVENT_BUS.post(preEvent);
            if(preEvent.isCanceled()) return false;

            SpManager.tryRemoveSp(player, price);

            player.getData(PlayerData.DATA).purchaseSpell(spell.getId());
            PacketDistributor.sendToPlayer(player, new UpdatePayloads.SpellPurchasePayload(spell.getId(), true));

            NeoForge.EVENT_BUS.post(new SpellPurchaseEvent.Post(player, spell));
            return true;
        }
        return false;
    }
    public static boolean tryCastSpell(@NotNull ServerPlayer player, @NotNull ISpell spell){
        var behavior = BehaviorRegistry.getSpellBehavior(spell);

        if(behavior == null) return false;
        if(!isSpellPurchased(player, spell) || !isSpellAssigned(player, spell)) return false;
        if(getSpellCooldown(player, spell) != null) return false;

        var preEvent = new SpellCastEvent.Pre(player, spell);
        NeoForge.EVENT_BUS.post(preEvent);
        if(preEvent.isCanceled()) return false;

        if(behavior.onSpellCast(player, spell)){
            initSpellCooldown(player, spell);
            NeoForge.EVENT_BUS.post(new SpellCastEvent.Post(player, spell));
            return true;
        }
        return false;
    }
    public static boolean tryAssignSpell(@NotNull ServerPlayer player, @NotNull ISpell spell, int slot){
        var maxSlots = (int)player.getAttributeValue(AttributeRegistry.MAX_SPELL_SLOTS);
        if (slot <= 0 || slot > maxSlots) return false;

        var preEvent = new SpellAssignEvent.Pre(player, spell, slot);
        NeoForge.EVENT_BUS.post(preEvent);
        if(preEvent.isCanceled()) return false;

        if(player.getData(PlayerData.DATA).tryAssignSpell(spell.getId(), slot)){
            PacketDistributor.sendToPlayer(player, new UpdatePayloads.SpellSlotChange(slot, spell.getId()));
            NeoForge.EVENT_BUS.post(new SpellAssignEvent.Post(player, spell, slot));
            return true;
        }

        return false;
    }
    public static boolean tryUnassignSpell(@NotNull ServerPlayer player, int slot){
        var spell = getSpell(getSpellInSlot(player, slot));
        if(spell != null) {
            var preEvent = new SpellUnassignEvent.Pre(player, spell, slot);
            NeoForge.EVENT_BUS.post(preEvent);
            if(preEvent.isCanceled()) return false;
        }

        player.getData(PlayerData.DATA).unassignSpell(slot);
        PacketDistributor.sendToPlayer(player, new UpdatePayloads.SpellSlotChange(slot, ""));

        if(spell != null) NeoForge.EVENT_BUS.post(new SpellUnassignEvent.Post(player, spell, slot));
        return true;
    }
    public static void resetSpells(@NotNull ServerPlayer player){
        var data = player.getData(PlayerData.DATA);

        data.resetPropertyLevels();
        data.resetSpellLevels();
        data.resetSlots();
        data.resetCooldowns();
    }

    //COOLDOWNS
    public static void initSpellCooldown(@NotNull ServerPlayer player, @NotNull ISpell spell){
        var cooldownProp = spell.getProperty("cooldown");
        if(cooldownProp == null) return;

        var propertyLevel = getPropertyLevel(player, cooldownProp);
        var cooldownValue = (long)(cooldownProp.getValue(propertyLevel) * 20f);
        if(cooldownValue <= 0) return;

        var gameTime = player.server.overworld().getGameTime();
        var endTick = gameTime + cooldownValue;

        var preEvent = new SpellCooldownStartEvent.Pre(player, spell, new Cooldown(endTick, cooldownValue));
        NeoForge.EVENT_BUS.post(preEvent);
        if(preEvent.isCanceled()) return;

        var finalCooldown = preEvent.getCooldown();
        if(finalCooldown.isCooldownOver(gameTime)) return;

        player.getData(PlayerData.DATA).addCooldown(spell.getId(), finalCooldown);
        PacketDistributor.sendToPlayer(player, new UpdatePayloads.SpellCooldownChange(spell.getId(), finalCooldown));

        NeoForge.EVENT_BUS.post(new SpellCooldownStartEvent.Post(player, spell, finalCooldown));
    }
    public static void removeCooldown(@NotNull ServerPlayer player, @NotNull ISpell spell){
        player.getData(PlayerData.DATA).removeCooldown(spell.getId());
        PacketDistributor.sendToPlayer(player, new UpdatePayloads.SpellCooldownChange(spell.getId(), Cooldown.EMPTY));
    }

    //API
    public static void registerSpell(@NotNull Spell spell) {
        var preEvent = new SpellLoadEvent.Pre(spell);
        NeoForge.EVENT_BUS.post(preEvent);
        if(preEvent.isCanceled()) return;

        spellCollection.put(spell.getId().toLowerCase(), spell);

        NeoForge.EVENT_BUS.post(new SpellLoadEvent.Post(spell));
    }
    public static void clearSpells() {spellCollection.clear();}

    //Getters
    public static @Nullable Spell getSpell(@NotNull String spellId){return spellCollection.get(spellId.toLowerCase());}
    public static boolean isSpellLoaded(@NotNull String spellId){return getSpell(spellId) != null;}
    public static boolean isSpellPurchased(@NotNull ServerPlayer player, @NotNull ISpell spell){
        return player.getData(PlayerData.DATA).isSpellPurchased(spell.getId());
    }
    public static boolean isSpellAssigned(@NotNull ServerPlayer player, @NotNull ISpell spell){
        return player.getData(PlayerData.DATA).isSpellAssigned(spell.getId());
    }
    public static boolean isSlotFree(@NotNull ServerPlayer player, int slot){
        var maxSlots =(int) player.getAttributeValue(AttributeRegistry.MAX_SPELL_SLOTS);
        if (slot <= 0 || slot > maxSlots) return false;
        return player.getData(PlayerData.DATA).isSlotFree(slot);
    }
    public static @NotNull String getSpellInSlot(@NotNull ServerPlayer player, int slot){
        return getPlayerSlots(player).getOrDefault(slot, "");
    }

    public static @NotNull @UnmodifiableView Map<String, Integer> getPlayerProperties(@NotNull ServerPlayer player){
        return player.getData(PlayerData.DATA).getPlayerProperties();
    }
    public static @NotNull @UnmodifiableView List<String> getPlayerSpells(@NotNull ServerPlayer player){
        return player.getData(PlayerData.DATA).getPlayerSpells();
    }
    public static @NotNull @UnmodifiableView Map<Integer, String> getPlayerSlots(@NotNull ServerPlayer player){
        return player.getData(PlayerData.DATA).getPlayerSlots();
    }
    public static int getPropertyLevel(@NotNull ServerPlayer player, @NotNull SpellProperty property) {
        var spell = getSpell(property.spellId());
        if(spell == null || !isSpellPurchased(player, spell)) return 0;
        return player.getData(PlayerData.DATA).getPropertyLevel(property.getFullId());}

    public static @Nullable Cooldown getSpellCooldown(@NotNull ServerPlayer player, @NotNull ISpell spell){
        var data = player.getData(PlayerData.DATA);
        var cooldown = data.getCooldown(spell.getId());

        if(cooldown != null && cooldown.isCooldownOver(player.server.overworld().getGameTime())) {
            data.removeCooldown(spell.getId());
            return null;
        }
        return cooldown;
    }
    public static @NotNull @UnmodifiableView Map<String, Cooldown> getPlayerCooldowns(@NotNull ServerPlayer player){
        return player.getData(PlayerData.DATA).getPlayerSpellCooldowns();
    }

    public static @NotNull Context.Spell getSpellContext(@NotNull ServerPlayer player, @NotNull ISpell spell){
        return SPELL_CONTEXT.updateContext(spell, isSpellPurchased(player, spell));
    }
    public static @NotNull Context.Property getPropertyContext(@NotNull ServerPlayer player, @NotNull SpellProperty property){
        return PROPERTY_CONTEXT.updateContext(property, getPropertyLevel(player, property));
    }

    //CORE
    @ApiStatus.Internal public static @NotNull Collection<Spell> getAllSpells() {return spellCollectionView.values();}
}
