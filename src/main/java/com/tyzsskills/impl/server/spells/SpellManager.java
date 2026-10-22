package com.tyzsskills.impl.server.spells;

import com.tyzsskills.Config;
import com.tyzsskills.api.model.Context;
import com.tyzsskills.api.records.SpellProperty;
import com.tyzsskills.impl.server.active.AttributeRegistry;
import com.tyzsskills.impl.server.active.BehaviorRegistry;
import com.tyzsskills.impl.server.attachments.PlayerData;
import com.tyzsskills.impl.server.payloads.UpdatePayloads;
import com.tyzsskills.impl.server.skills.SkillManager;
import com.tyzsskills.impl.server.sp.SpManager;
import net.minecraft.server.level.ServerPlayer;
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
    private static boolean setPropertyLevelInternal(@NotNull ServerPlayer player, @NotNull Spell spell, @NotNull SpellProperty property, int newLevel, boolean syncClient){
        if(!isSpellPurchased(player, spell.getID())) return false;
        if(newLevel < 0 || newLevel > spell.getMaximumLevelForProperty(property.key())) return false;
        int oldLvl = getPlayerPropertyLevel(player, spell.getID(), property.key());

        if(oldLvl == newLevel) return false;

        player.getData(PlayerData.DATA).setSpellPropertyLevel(spell.getID(), property.key(), newLevel);

        if(syncClient) PacketDistributor.sendToPlayer(player, new UpdatePayloads.PropertyLevelPayload(spell.getID() + ":" + property.key(), newLevel));

        return true;
    }

    //PROPERTIES
    public static boolean tryAddPropertyLevel(@NotNull ServerPlayer player, @NotNull String spellId, @NotNull String propertyKey, int amount){
        var spell = getSpell(spellId.toLowerCase()); if(spell == null) return false;
        var property = spell.getProperty(propertyKey); if(property == null) return false;
        return setPropertyLevelInternal(player, spell, property, getPlayerPropertyLevel(player, spellId, propertyKey) + amount, true);
    }

    public static boolean tryRemovePropertyLevel(@NotNull ServerPlayer player, @NotNull String spellId, @NotNull String propertyKey, int amount){
        var spell = getSpell(spellId.toLowerCase()); if(spell == null) return false;
        var property = spell.getProperty(propertyKey); if(property == null) return false;
        return setPropertyLevelInternal(player, spell, property, getPlayerPropertyLevel(player, spellId, propertyKey) - amount, true);
    }

    public static void setPropertiesLevel(@NotNull ServerPlayer player, @NotNull String spellId, @NotNull String propertyKey, int newLevel){
        var spell = getSpell(spellId.toLowerCase()); if(spell == null) return;
        var property = spell.getProperty(propertyKey); if(property == null) return;
        setPropertyLevelInternal(player, spell, property, newLevel, true);
    }

    public static void resetPropertyLevels(@NotNull ServerPlayer player){
        player.getData(PlayerData.DATA).resetPropertyLevels();
    }

    public static boolean tryBuyProperty(@NotNull ServerPlayer player, @NotNull String spellId, @NotNull String propertyKey)
    {
        spellId = spellId.toLowerCase();
        propertyKey = propertyKey.toLowerCase();

        if(!isSpellPurchased(player, spellId)) return false;

        var spell = getSpell(spellId);
        if(spell == null) return false;


        var property = spell.getProperty(propertyKey);
        if(property == null) return false;

        int currentLvl = getPlayerPropertyLevel(player, spellId, propertyKey);

        if(SpellRules.canBuyProperty(getPropertyContext(player, spellId, propertyKey), SkillManager.getPlayerContext(player), Config.PURCHASE_SYSTEM.get())){
            var price = property.getPrice(currentLvl + 1);

            if(setPropertyLevelInternal(player, spell, property, currentLvl + 1, true)) {
                SpManager.tryRemoveSp(player, price);
                return true;
            }
        }
        return false;
    }

    //SPELLS
    public static boolean tryBuySpell(@NotNull ServerPlayer player, @NotNull String spellId){
        var spell = getSpell(spellId);
        if(spell == null) return false;

        if(isSpellPurchased(player, spellId)) return false;

        if(SpellRules.canBuySpell(getSpellContext(player, spellId), SkillManager.getPlayerContext(player), Config.PURCHASE_SYSTEM.get())){
            var price = spell.getPrice();

            SpManager.tryRemoveSp(player, price);
            player.getData(PlayerData.DATA).purchaseSpell(spellId);
            PacketDistributor.sendToPlayer(player, new UpdatePayloads.SpellPurchasePayload(spellId));
            return true;
        }
        return false;
    }

    public static boolean tryCastSpell(@NotNull ServerPlayer player, @NotNull String spellId){
        var behavior = BehaviorRegistry.getSpellBehavior(spellId);
        if(behavior == null) return false;

        var spell = getSpell(spellId);
        if(spell == null) return false;

        if(!isSpellPurchased(player, spellId) || !isSpellAssigned(player, spellId)) return false;

        return behavior.onSpellCast(player, spell);
    }

    public static boolean tryAssignSpell(@NotNull ServerPlayer player, @NotNull String spellId, int slot){
        var spell = getSpell(spellId);
        if(spell == null) return false;

        var maxSlots = (int)player.getAttributeValue(AttributeRegistry.MAX_SPELL_SLOTS);
        if (slot <= 0 || slot > maxSlots) return false;


        if(player.getData(PlayerData.DATA).tryAssignSpell(spellId, slot)){
            PacketDistributor.sendToPlayer(player, new UpdatePayloads.SpellSlotChange(slot, spellId));
            return true;
        }

        return false;
    }

    public static void unassignSpell(@NotNull ServerPlayer player, int slot){
        player.getData(PlayerData.DATA).unassignSpell(slot);
        PacketDistributor.sendToPlayer(player, new UpdatePayloads.SpellSlotChange(slot, ""));
    }


    //API
    public static void registerSpell(@NotNull Spell spell) {
        spellCollection.put(spell.getID().toLowerCase(), spell);
    }
    public static void clearSpells() {spellCollection.clear();}

    //Getters
    public static @Nullable Spell getSpell(@NotNull String spellId){return spellCollection.getOrDefault(spellId.toLowerCase(), null);}
    public static boolean isSpellLoaded(@NotNull String spellId){return spellCollection.containsKey(spellId);}

    public static int getPlayerPropertyLevel(@NotNull ServerPlayer player, @NotNull String spellId, @NotNull String propertyKey) {
        return player.getData(PlayerData.DATA).getPropertyLevel(spellId, propertyKey);}

    public static boolean isSpellPurchased(@NotNull ServerPlayer player, @NotNull String spellId){
        return player.getData(PlayerData.DATA).isSpellPurchased(spellId);
    }
    public static boolean isSpellAssigned(@NotNull ServerPlayer player, @NotNull String spellId){
        return player.getData(PlayerData.DATA).isSpellAssigned(spellId);
    }
    public static boolean isSlotFree(@NotNull ServerPlayer player, int slot){
        var maxSlots =(int) player.getAttributeValue(AttributeRegistry.MAX_SPELL_SLOTS);
        if (slot <= 0 || slot > maxSlots) return false;
        return player.getData(PlayerData.DATA).isSlotFree(slot);
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

    public static @NotNull Context.Spell getSpellContext(@NotNull ServerPlayer player, @NotNull String spellId){
        var spell = getSpell(spellId);
        Objects.requireNonNull(spell, "Attempt to access SpellContext with null spell (server side).");
        return SPELL_CONTEXT.updateContext(spell, isSpellPurchased(player, spellId));
    }
    public static @NotNull Context.Property getPropertyContext(@NotNull ServerPlayer player, @NotNull String spellId, @NotNull String propertyKey){
        var spell = getSpell(spellId);
        Objects.requireNonNull(spell, "Attempt to access Property with null spell (server side).");
        var property = spell.getProperty(propertyKey);
        Objects.requireNonNull(property, "Attempt to access PropertyContext with null property (server side).");
        return PROPERTY_CONTEXT.updateContext(property, getPlayerPropertyLevel(player, spellId, propertyKey));
    }

    //CORE
    @ApiStatus.Internal public static @NotNull Collection<Spell> getAllSpells() {return spellCollectionView.values();}
}
