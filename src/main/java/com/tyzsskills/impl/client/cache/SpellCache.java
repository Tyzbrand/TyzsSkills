package com.tyzsskills.impl.client.cache;

import com.tyzsskills.Config;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.interfaces.ISpell;
import com.tyzsskills.api.model.Context;
import com.tyzsskills.api.records.Cooldown;
import com.tyzsskills.api.records.SpellProperty;
import com.tyzsskills.impl.server.active.AttributeRegistry;
import com.tyzsskills.impl.server.payloads.CActionSpellPayload;
import com.tyzsskills.impl.server.payloads.UpdatePayloads;
import com.tyzsskills.impl.server.spells.SpellRules;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

public class SpellCache {

    private final ClientCache CACHE;
    SpellCache(ClientCache cache){CACHE = cache;}

    public final Update UPDATE = new Update();

    public class Update{
        void init(UpdatePayloads.PlayerSyncSpellData playerSpellData, UpdatePayloads.ServerSyncData serverData){
            purchasedSpells.clear(); propertyLevels.clear(); spells.clear(); spellSlots.clear(); spellCooldowns.clear();

            for(var spell : serverData.spells()) spells.put(spell.getId(), spell);
            purchasedSpells.addAll(playerSpellData.purchasedSpells());
            propertyLevels.putAll(playerSpellData.playerPropertyLevels());
            spellSlots.putAll(playerSpellData.playerSlots());
            spellCooldowns.putAll(playerSpellData.playerCooldowns());
        }

        public void updateSpellPurchase(@NotNull UpdatePayloads.SpellPurchasePayload payload) {
            if(payload.state()){
                if(!isSpellPurchased(payload.id())) purchasedSpells.add(payload.id());
            }
            else purchasedSpells.remove(payload.id());
            CACHE.UPDATE.registerUpdate("Spell Purchase: New Purchased Spell [" + payload.id() + "]");
        }

        public void updatePropertyLevel(@NotNull UpdatePayloads.PropertyLevelPayload payload) {
            if (payload.level() <= 0) propertyLevels.remove(payload.id().toLowerCase());
            else propertyLevels.put(payload.id(), payload.level());
            CACHE.UPDATE.registerUpdate("Property Level Synced: New Cached Spell Property Level [" + payload.id() + ", " + payload.level() + "]");
        }

        public void updateSpellSlot(@NotNull UpdatePayloads.SpellSlotChange payload) {
            if (payload.id().isEmpty()) spellSlots.remove(payload.slot());
            else {
                spellSlots.values().remove(payload.id());
                spellSlots.put(payload.slot(), payload.id());
            }
            CACHE.UPDATE.registerUpdate("Slot Synced: New Cached Spell in Slot [" + payload.slot() + "]");
        }

        public void updateSpellCooldown(@NotNull UpdatePayloads.SpellCooldownChange payload) {
            var cooldown = payload.newCooldown();
            if(cooldown.duration() <= 0L) {
                spellCooldowns.remove(payload.id());
                CACHE.UPDATE.registerUpdate("Cooldown Removed: [" + payload.id() + "]");
            }
            else {
                spellCooldowns.put(payload.id(), payload.newCooldown());
                CACHE.UPDATE.registerUpdate("Cooldown Added: New Cached Cooldown for [" + payload.id() + "] for " + payload.newCooldown().duration()/20 + " sec");
            }
        }
    }


    //DATA
    //==============Properties==============
    private final Map<String, Integer> propertyLevels = new HashMap<>();
    private final Map<String, Integer> propertyLevelsView = Collections.unmodifiableMap(propertyLevels);

    public int getPropertyLevel(@NotNull String spellId, @NotNull String propertyKey) {
        return propertyLevels.getOrDefault(getPropertyId(spellId, propertyKey), 0);
    }

    private String getPropertyId(String spellId, String propertyKey){return spellId + ":" + propertyKey;}


    //==============Slots==============
    private final Map<Integer, String> spellSlots = new HashMap<>();
    private final Map<Integer, String> spellSlotsView = Collections.unmodifiableMap(spellSlots);

    public boolean isSpellAssigned(@NotNull String spellId){return  spellSlots.containsValue(spellId);}
    public boolean isSlotFree(int slot){
        return !spellSlots.containsKey(slot);
    }
    public @NotNull @UnmodifiableView Map<Integer, String> getSlots(){return spellSlotsView;}
    public @Nullable ISpell getSpellInSlot(int slot){return getSpell(spellSlots.getOrDefault(slot, ""));}
    public int getSpellSlot(@NotNull String spellId) {
        for (var entry : spellSlots.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(spellId)) return entry.getKey();
        }
        return -1;
    }

    //==============Cooldowns==============
    private final Map<String, Cooldown> spellCooldowns = new HashMap<>();
    private final Map<String, Cooldown> spellCooldownsView = Collections.unmodifiableMap(spellCooldowns);

    public @Nullable Cooldown getSpellCooldown(@NotNull String spellId){
        var cooldown = spellCooldowns.get(spellId);
        var level = Minecraft.getInstance().level;
        if(level == null) return null;

        if(cooldown != null && cooldown.isCooldownOver(level.getGameTime())) {
            spellCooldowns.remove(spellId);
            return null;
        }
        return cooldown;
    }
    public @NotNull @UnmodifiableView Map<String, Cooldown> getAllCooldowns(){return spellCooldownsView;}

    //==============Spells==============
    private final List<String> purchasedSpells = new ArrayList<>();
    private final List<String> purchasedSpellsView = Collections.unmodifiableList(purchasedSpells);
    private final Map<String, ISpell> spells = new HashMap<>();
    private final Map<String, ISpell> spellsView = Collections.unmodifiableMap(spells);

    public @Nullable ISpell getSpell(@NotNull String spellId) {return spells.getOrDefault(spellId.toLowerCase(), null);}
    public @NotNull @UnmodifiableView Map<String, ISpell> getAllSpells() {return spellsView;}
    public boolean isSpellPurchased(@NotNull String spellId) {
        return purchasedSpells.contains(spellId);
    }
    public @NotNull @UnmodifiableView List<String> getAllPurchasedSpells(){return purchasedSpellsView;}

    //==============Contexts==============
    private final Context.Spell spellContext = new Context.Spell();
    public @NotNull Context.Spell getSpellContext(@NotNull String spellId) {
        var spell = getSpell(spellId);
        Objects.requireNonNull(spell, "Attempt to access SpellContext with null spell.");
        return spellContext.updateContext(spell, isSpellPurchased(spellId));
    }
    private final Context.Property propertyContext = new Context.Property();
    public @NotNull Context.Property getPropertyContext(@NotNull SpellProperty property, @NotNull String spellId) {
        return propertyContext.updateContext(property, getPropertyLevel(spellId, property.key()));
    }

    //ACTIONS
    private boolean triggerAction(@NotNull ISpell spell, @NotNull String propertyKey, Enums.SpellAction actionType, int affectedSlot){
        if(CACHE.isSpamming()) return false;
        var success = false;

        switch(actionType){
            case PURCHASE -> success = predictPurchase(spell);
            case ASSIGNMENT -> success = predictAssignment(spell, affectedSlot);
            case UNASSIGNMENT -> success = predictUnassignment(affectedSlot);
            case CAST -> success = predictCast(spell);
            case PROPERTY_PURCHASE -> success = predictPropertyPurchase(spell, propertyKey);
        }

        if(success){
            CACHE.registerNewClick();
            PacketDistributor.sendToServer(new CActionSpellPayload(spell.getId(), propertyKey, actionType, affectedSlot));
        }
        return success;
    }

    public boolean triggerSpellAction(@NotNull ISpell spell, Enums.SpellAction actionType, int affectedSlot){
        return triggerAction(spell, "", actionType, affectedSlot);
    }

    public boolean triggerPropertyAction(@NotNull ISpell spell, @NotNull String propertyId, Enums.SpellAction actionType){
        return triggerAction(spell, propertyId, actionType, -1);
    }


    //PREDICTIONS
    private boolean predictPurchase(ISpell spell){
        var spellId = spell.getId();
        if(isSpellPurchased(spellId)) return false;

        if(SpellRules.canBuySpell(getSpellContext(spellId), CACHE.getPlayerContext(), CACHE.getConfigBool(Config.PURCHASE_SYSTEM_KEY, true))){
            var price = spell.getPrice();

            CACHE.METADATA.sp -= price;
            purchasedSpells.add(spellId);
            CACHE.incrVersion();
            return true;
        }
        return false;
    }

    private boolean predictPropertyPurchase(ISpell spell, String propertyId){
        var spellId = spell.getId();
        var property = spell.getProperty(propertyId);
        if(!isSpellPurchased(spellId) || property == null) return false;

        var propertyCtx = getPropertyContext(property, spellId);

        if(SpellRules.canBuyProperty(propertyCtx, CACHE.getPlayerContext(), CACHE.getConfigBool(Config.PURCHASE_SYSTEM_KEY, true))){
            var price = property.getPrice(getPropertyLevel(spellId, propertyId) + 1);

            CACHE.METADATA.sp -= price;
            propertyLevels.put(getPropertyId(spellId, propertyId), propertyCtx.propertyLevel() + 1);
            CACHE.incrVersion();
            return true;
        }
        return false;
    }

    private boolean predictAssignment(ISpell spell, int slot){
        var player = Minecraft.getInstance().player;
        if(player == null) return false;

        var maxSlots = (int)player.getAttributeValue(AttributeRegistry.MAX_SPELL_SLOTS);
        if (slot <= 0 || slot > maxSlots) return false;

        if(!isSpellPurchased(spell.getId())) return false;

        spellSlots.put(slot, spell.getId());
        CACHE.incrVersion();
        return true;
    }

    private boolean predictUnassignment(int slot){
        if(isSlotFree(slot)) return false;
        spellSlots.remove(slot);
        CACHE.incrVersion();
        return true;
    }

    private boolean predictCast(ISpell spell){
        var spellId = spell.getId();
        if(!isSpellPurchased(spellId) || !isSpellAssigned(spellId)) return false;

        return getSpellCooldown(spellId) == null;
    }
}
