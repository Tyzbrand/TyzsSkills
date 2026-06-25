package com.tyzsskills.impl.server.spells;

import com.tyzsskills.Config;
import com.tyzsskills.api.records.SpellProperty;
import com.tyzsskills.api.records.PropertyContext;
import com.tyzsskills.impl.server.active.BehaviorRegistries;
import com.tyzsskills.impl.server.attachments.PlayerData;
import com.tyzsskills.impl.server.sp.SpManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@ApiStatus.Internal
public class SpellManager {
    private static final Map<String, Spell> spellCollection = new HashMap<>();

    //CORE
    private static boolean setPropertyLevelInternal(@NotNull ServerPlayer player, @NotNull Spell spell, @NotNull SpellProperty property, int newLevel, boolean syncClient){
        if(newLevel < 0 || newLevel > spell.getMaximumLevelForProperty(property.key())) return false;
        int oldLvl = getPlayerPropertyLevel(player, spell.getID(), property.key());

        if(oldLvl == newLevel) return false;

        player.getData(PlayerData.DATA).setSpellPropertyLevel(spell.getID(), property.key(), newLevel);

        //if(syncClient) PacketDistributor.sendToPlayer(player, new UpdatePayloads.SkillLevelPayload(skill.getID(), newLevel));

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

        if(spell.canBuyProperty(getPropertyContext(player, spellId, propertyKey), Config.PURCHASE_SYSTEM.get())){
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
        spellId = spellId.toLowerCase();
        var spell = getSpell(spellId);
        if(spell == null) return false;

        if(isSpellPurchased(player, spellId)) return false;

        if(spell.canBuySpell(SpManager.getSP(player), Config.PURCHASE_SYSTEM.get())){
            var price = spell.getPrice();

            SpManager.tryRemoveSp(player, price);
            player.getData(PlayerData.DATA).purchaseSpell(spellId);
            return true;
        }
        return false;
    }

    public static void assignSpellToSlot(@NotNull ServerPlayer player, @Nullable String spellId, int slot){
        if(spellId == null || !isSpellPurchased(player, spellId)) return;
        player.getData(PlayerData.DATA).assignSpellToSlot(slot, spellId);
    }

    public static void unassignSpellInSlot(@NotNull ServerPlayer player, int slot){
        assignSpellToSlot(player, null, slot);
    }

    public static boolean castSpell(@NotNull ServerPlayer player, @NotNull String spellId){
        var spell = getSpell(spellId); if(spell == null) return false;
        if(!isSpellPurchased(player, spellId)) return false;
        var behavior = BehaviorRegistries.getSpellBehavior(spellId); if(behavior == null) return false;

        if(getCooldown(player, spellId) > 0) return false;

        if(behavior.onSpellCast(player, spell)){
            var cooldown = spell.getProperty("cooldown");
            if(cooldown == null) return false;

            var value = cooldown.getValue(getPlayerPropertyLevel(player, spellId,"cooldown"));
            if(value > 0){
                addCooldown(player, spellId, (int)(value * 20));
                return true;
            }
        }
        return false;
    }


    //COOLDOWNS
    public static boolean addCooldown(@NotNull ServerPlayer player, @NotNull String spellId, int startTick){
        return player.getData(PlayerData.DATA).setCooldown(spellId, startTick);
    }

    public static int getCooldown(@NotNull ServerPlayer player, @NotNull String spellId){
        return player.getData(PlayerData.DATA).getCooldown(spellId);
    }

    public static void updateCooldowns(@NotNull MinecraftServer server){
        for(var player: server.getPlayerList().getPlayers()){
            player.getData(PlayerData.DATA).updateCooldowns();
        }
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

    public static @NotNull PropertyContext getPropertyContext(@NotNull ServerPlayer player, @NotNull String spellId, @NotNull String propertyKey){
        return new PropertyContext(player, propertyKey, getPlayerPropertyLevel(player, spellId, propertyKey), SpManager.getSP(player));
    }

}
