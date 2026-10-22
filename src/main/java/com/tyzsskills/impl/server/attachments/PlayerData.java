package com.tyzsskills.impl.server.attachments;

import com.tyzsskills.Tyzsskills;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.Supplier;

public class PlayerData implements INBTSerializable<CompoundTag> {

    //SKILLS
    private final Map<String, Integer> playerSkills = new HashMap<>();
    private final Map<String, Integer> playerSkillsView = Collections.unmodifiableMap(playerSkills);
    private final List<String> playerBookmarks = new ArrayList<>();
    private final List<String> playerBookmarksView = Collections.unmodifiableList(playerBookmarks);
    private final List<String> playerDeactivations = new ArrayList<>();
    private final List<String> playerDeactivationsView = Collections.unmodifiableList(playerDeactivations);

    //SPELLS
    private final List<String> playerSpells = new ArrayList<>();
    private final List<String> playerSpellsView = Collections.unmodifiableList(playerSpells);;
    private final Map<String, Integer> playerSpellsProperties = new HashMap<>();
    private final Map<String, Integer> playerSpellsPropertiesView = Collections.unmodifiableMap(playerSpellsProperties);
    private final Map<Integer, String> playerSpellSlots = new HashMap<>();
    private final Map<Integer, String> playerSpellSlotsView = Collections.unmodifiableMap(playerSpellSlots);

    //METADATA
    private int playerLevel = 1;
    private int playerSP = 0;
    private float playerXP = 0f;

    //COMPAT
    private final Set<String> compatibility_tags = new HashSet<>();


    //region SKILLS
    //-----------------Skill level-----------------
    public void setSkillLevel(String id, int lvl){
        if(id == null || lvl < 0) return;

        if(lvl == 0 && playerSkills.containsKey(id)) playerSkills.remove(id);
        else playerSkills.put(id, lvl);
    }
    public int getSkillLevel(String id){return playerSkills.getOrDefault(id, 0);}
    public @NotNull @UnmodifiableView Map<String, Integer> getOwnedSkill(){return playerSkillsView;}

    //-----------------Bookmarks-----------------
    public void triggerBookmark(String id){
        if(playerBookmarks.contains(id)) playerBookmarks.remove(id);
        else playerBookmarks.add(id);
    }
    public @NotNull @UnmodifiableView List<String> getBookmarks(){return playerBookmarksView;}
    public boolean isBookmarked(String id){return playerBookmarks.contains(id);}

    //-----------------Deactivations-----------------
    public void triggerDeactivation(String id){
        if(playerDeactivations.contains(id)) playerDeactivations.remove(id);
        else playerDeactivations.add(id);
    }
    public @NotNull @UnmodifiableView List<String> getDeactivatedSkills(){return playerDeactivationsView;}
    public boolean isDeactivated(String id){return playerDeactivations.contains(id);}
    //endregion

    //region SPELLS
    //-----------------Properties-----------------
    private String getPropertyId(String spellId, String propertyKey){return spellId + ":" + propertyKey;}
    public void setSpellPropertyLevel(String spellId, String propertyKey, int lvl){
        if(spellId == null || propertyKey == null || lvl < 0) return;

        var finalId = getPropertyId(spellId, propertyKey);

        if(lvl == 0 && playerSpellsProperties.containsKey(finalId)) playerSpellsProperties.remove(finalId);
        else playerSpellsProperties.put(finalId, lvl);
    }

    public void resetPropertyLevels(){
        playerSpellsProperties.clear();
    }

    public int getPropertyLevel(String spellId, String propertyKey){
        if(isSpellPurchased(spellId)) return playerSpellsProperties.getOrDefault(getPropertyId(spellId, propertyKey), 0);
        else return 0;
    }

    public @NotNull @UnmodifiableView Map<String, Integer> getPlayerProperties(){return playerSpellsPropertiesView;}

    //-----------------Spells-----------------
    public void purchaseSpell(String spellId){
        if(spellId == null || isSpellPurchased(spellId)) return;
        playerSpells.add(spellId);
    }

    public void resetSpellLevels(){
        playerSpells.clear();
    }

    public boolean isSpellPurchased(String spellId){
        if(spellId == null) return false;
        return playerSpells.contains(spellId);
    }

    public @NotNull @UnmodifiableView List<String> getPlayerSpells(){return playerSpellsView;}

    //-----------------Slots-----------------
    public boolean tryAssignSpell(String spellId, int slot){
        if(spellId == null || !isSpellPurchased(spellId)) return false;

        playerSpellSlots.values().remove(spellId);
        playerSpellSlots.put(slot, spellId);

        return true;
    }
    public void unassignSpell(int slot){
        playerSpellSlots.remove(slot);
    }


    public boolean isSpellAssigned(String spellId){
        return spellId != null && playerSpellSlots.containsValue(spellId);
    }
    public boolean isSlotFree(int slot){
        return !playerSpellSlots.containsKey(slot);
    }

    public @NotNull @UnmodifiableView Map<Integer, String> getPlayerSlots(){return playerSpellSlotsView;}
    //endregion

    //region DATA
    //-----------------Level-----------------
    public void setLevel(int lvl){playerLevel = Math.max(1, lvl);}
    public int getLevel(){return playerLevel;}


    //-----------------SP-----------------
    public void setSP(int sp){playerSP = Math.max(0, sp);}
    public int getSP(){return playerSP;}


    //-----------------XP-----------------
    public void setXP(float xp){playerXP = Math.max(0f, xp);}
    public float getXP(){return playerXP;}


    //-----------------COMPATIBILITY-----------------
    public void putTag(String tag){if(tag != null) compatibility_tags.add(tag);}
    public boolean hasMigrated(String tag){return tag != null && compatibility_tags.contains(tag);}
    //endregion

    //region SERIALIZATION
    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        var tag = new CompoundTag();

        CompoundTag skillLevels = new CompoundTag();
        for(var entry : playerSkills.entrySet()) skillLevels.putInt(entry.getKey(), entry.getValue());
        tag.put("skill_levels", skillLevels);

        ListTag bookmarks = new ListTag();
        playerBookmarks.forEach(b -> bookmarks.add(StringTag.valueOf(b)));
        tag.put("skill_bookmarks", bookmarks);

        ListTag deactivations = new ListTag();
        playerDeactivations.forEach(b -> deactivations.add(StringTag.valueOf(b)));
        tag.put("skill_deactivations", deactivations);

        ListTag spells = new ListTag();
        playerSpells.forEach(b -> spells.add(StringTag.valueOf(b)));
        tag.put("spells", spells);

        CompoundTag propertyLevels = new CompoundTag();
        for(var entry : playerSpellsProperties.entrySet()) propertyLevels.putInt(entry.getKey(), entry.getValue());
        tag.put("property_levels", propertyLevels);

        CompoundTag spellSlots = new CompoundTag();
        for(var entry : playerSpellSlots.entrySet()) spellSlots.putString(String.valueOf(entry.getKey()), entry.getValue());
        tag.put("spell_slots", spellSlots);

        tag.putInt("skill_level", playerLevel);
        tag.putInt("skill_point", playerSP);
        tag.putFloat("skill_xp", playerXP);

        ListTag compatTags = new ListTag();
        compatibility_tags.forEach(b -> compatTags.add(StringTag.valueOf(b)));
        tag.put("compatibility_tags", compatTags);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag compoundTag) {
        playerBookmarks.clear();
        playerDeactivations.clear();
        playerSkills.clear();

        playerSpellsProperties.clear();
        playerSpells.clear();
        playerSpellSlots.clear();


        if(compoundTag.contains("skill_levels")) {
            CompoundTag skillsTag = compoundTag.getCompound("skill_levels");
            for(String key : skillsTag.getAllKeys()) playerSkills.put(key, skillsTag.getInt(key));
        }

        if(compoundTag.contains("skill_bookmarks")){
            for (var id : compoundTag.getList("skill_bookmarks", Tag.TAG_STRING)){
                playerBookmarks.add(id.getAsString());
            }
        }

        if(compoundTag.contains("skill_deactivations")){
            for (var id : compoundTag.getList("skill_deactivations", Tag.TAG_STRING)){
                playerDeactivations.add(id.getAsString());
            }
        }

        if(compoundTag.contains("spells")){
            for (var id : compoundTag.getList("spells", Tag.TAG_STRING)){
                playerSpells.add(id.getAsString());
            }
        }

        if(compoundTag.contains("property_levels")) {
            CompoundTag propTag = compoundTag.getCompound("property_levels");
            for(String key : propTag.getAllKeys()) playerSpellsProperties.put(key, propTag.getInt(key));
        }

        if(compoundTag.contains("spell_slots")) {
            CompoundTag propTag = compoundTag.getCompound("spell_slots");
            for(String key : propTag.getAllKeys()) playerSpellSlots.put(Integer.parseInt(key), propTag.getString(key));
        }

        if(compoundTag.contains("skill_level")) playerLevel = compoundTag.getInt("skill_level");
        if(compoundTag.contains("skill_point")) playerSP = compoundTag.getInt("skill_point");
        if(compoundTag.contains("skill_xp")) playerXP = compoundTag.getFloat("skill_xp");


        if(compoundTag.contains("compatibility_tags")){
            for (var id : compoundTag.getList("compatibility_tags", Tag.TAG_STRING)){
                compatibility_tags.add(id.getAsString());
            }
        }
    }
    //endregion

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Tyzsskills.MODID);

    public static final Supplier<AttachmentType<PlayerData>> DATA = ATTACHMENT_TYPES.register(
            "player_skill_data",
            () -> AttachmentType.serializable(PlayerData::new).build());
}
