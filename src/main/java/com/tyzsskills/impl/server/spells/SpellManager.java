package com.tyzsskills.impl.server.spells;


import com.tyzsskills.Config;
import com.tyzsskills.api.events.SkillActionEvent;
import com.tyzsskills.api.records.SpellProperty;
import com.tyzsskills.api.records.SpellPropertyContext;
import com.tyzsskills.impl.server.Level.LevelManager;
import com.tyzsskills.impl.server.attachments.PlayerData;
import com.tyzsskills.impl.server.sp.SpManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

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

    //PUBLIC
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
        for(var spell : spellCollection.values()) {
            for (var property : spell.getProperties())
                setPropertyLevelInternal(player, spell, property, 0, false);
        }
    }

    public static boolean tryBuyProperty(@NotNull ServerPlayer player, @NotNull String spellId, @NotNull String propertyKey)
    {
        spellId = spellId.toLowerCase();
        propertyKey = propertyKey.toLowerCase();
        var spell = getSpell(spellId);
        if(spell == null) return false;

        var property = spell.getProperty(propertyKey);
        if(property == null) return false;

        int currentLvl = getPlayerPropertyLevel(player, spellId, propertyKey);

        if(spell.canBuy(getPropertyContext(player, spellId, propertyKey), Config.PURCHASE_SYSTEM.get())){
            var price = property.getPrice(currentLvl + 1);

            if(setPropertyLevelInternal(player, spell, property, currentLvl + 1, true)) {
                SpManager.tryRemoveSp(player, price);
                return true;
            }
        }
        return false;
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
        return player.getData(PlayerData.DATA).getSpellPropertyLevel(spellId, propertyKey);}

    public static @NotNull SpellPropertyContext getPropertyContext(@NotNull ServerPlayer player, @NotNull String spellId, @NotNull String propertyKey){
        return new SpellPropertyContext(player, propertyKey, getPlayerPropertyLevel(player, spellId, propertyKey), SpManager.getSP(player));
    }
}
