package com.tyzsskills.impl.server.attachments;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.api.model.Cooldown;
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
    private final Set<String> playerBookmarks = new HashSet<>();

    //SPELLS
    private final Map<String, Integer> playerSpells = new HashMap<>();
    private final String[] assignedSpells = new String[3];
    private final List<Cooldown> cooldowns = new ArrayList<>(3);

    //METADATA
    private int playerLevel = 1;
    private int playerSP = 0;
    private float playerXP = 0f;

    //COMPAT
    private final Set<String> compatibility_tags = new HashSet<>();


    //-----------------Skill level-----------------
    public void setSkillLevel(String id, int lvl){
        if(id == null || lvl < 0) return;

        if(lvl == 0 && playerSkills.containsKey(id)) playerSkills.remove(id);
        else playerSkills.put(id, lvl);
    }
    public int getSkillLevel(String id){return playerSkills.getOrDefault(id, 0);}
    public List<String> getOwnedSkillIds(){return List.copyOf(playerSkills.keySet());}


    //-----------------Spells-----------------
    private String getPropertyId(String spellId, String propertyKey){return spellId + ":" + propertyKey;}
    public void setSpellPropertyLevel(String spellId, String propertyKey, int lvl){
        if(spellId == null || propertyKey == null || lvl < 0) return;

        var finalId = getPropertyId(spellId, propertyKey);

        if(lvl == 0 && playerSpells.containsKey(finalId)) playerSpells.remove(finalId);
        else playerSpells.put(finalId, lvl);
    }

    public void resetPropertyLevels(){
        playerSpells.clear();
    }

    public int getSpellPropertyLevel(String spellId, String propertyKey){
        return playerSpells.getOrDefault(getPropertyId(spellId, propertyKey), 0);
    }

    public void assignSpellToSlot(int slot, @Nullable String spellId){
        if(slot <= 0 ||slot > 3) return;
        assignedSpells[slot - 1] = spellId;
    }

    public @NotNull @UnmodifiableView List<String> getAssignedSpells(){
        return Collections.unmodifiableList(Arrays.asList(assignedSpells));
    }

    public @Nullable String getSpellInSlot(int slot){
        if(slot <= 0 ||slot > 3) return null;
        return assignedSpells[slot - 1];
    }

    public boolean isSpellAssigned(@NotNull String  spellId){
        return getAssignedSpells().contains(spellId);
    }

    //-----------------Cooldowns-----------------
    public boolean setCooldown(@NotNull String spellId, int startTick){
        if(!isSpellAssigned(spellId)) return false;

        for(var cooldown : cooldowns)
            if(cooldown.spellId.equals(spellId)) return false;

        return cooldowns.add(new Cooldown(spellId, startTick));
    }

    public int getCooldown(@NotNull String spellId){
        if(spellId.isEmpty()) return 0;

        for(var cooldown : cooldowns){
            if(cooldown.spellId.equals(spellId)) return cooldown.ticks;
        }
        return 0;
    }

    public void updateCooldowns(){
        if(cooldowns.isEmpty()) return;

        var iterator = cooldowns.iterator();
        while (iterator.hasNext()){
            var cooldown = iterator.next();
            cooldown.ticks--;

            if(cooldown.ticks <= 0) iterator.remove();
        }
    }


    //-----------------Bookmarks-----------------
    public void triggerBookmark(String id){
        if(playerBookmarks.contains(id)) playerBookmarks.remove(id);
        else playerBookmarks.add(id);
    }
    public List<String> getBookmarks(){return List.copyOf(playerBookmarks);}
    public boolean isBookmarked(String id){return playerBookmarks.contains(id);}


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


    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        var tag = new CompoundTag();

        CompoundTag skillLevels = new CompoundTag();
        for(var entry : playerSkills.entrySet()) skillLevels.putInt(entry.getKey(), entry.getValue());
        tag.put("skill_levels", skillLevels);

        ListTag bookmarks = new ListTag();
        playerBookmarks.forEach(b -> bookmarks.add(StringTag.valueOf(b)));
        tag.put("skill_bookmarks", bookmarks);

        CompoundTag spellLevels = new CompoundTag();
        for(var entry : playerSpells.entrySet()) spellLevels.putInt(entry.getKey(), entry.getValue());
        tag.put("spell_levels", spellLevels);

        ListTag spellSlots = new ListTag();
        for (String s : assignedSpells) spellSlots.add(StringTag.valueOf(s == null ? "" : s));
        tag.put("spell_slots", spellSlots);

        CompoundTag cooldownsTag = new CompoundTag();
        for (var cd : cooldowns) {
            cooldownsTag.putInt(cd.spellId, cd.ticks);
        }
        tag.put("active_cooldowns", cooldownsTag);

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
        playerSkills.clear();
        playerSpells.clear();
        cooldowns.clear();
        Arrays.fill(assignedSpells, null);

        if(compoundTag.contains("skill_levels")) {
            CompoundTag skillsTag = compoundTag.getCompound("skill_levels");
            for(String key : skillsTag.getAllKeys()) playerSkills.put(key, skillsTag.getInt(key));
        }

        if(compoundTag.contains("skill_bookmarks")){
            for (var id : compoundTag.getList("skill_bookmarks", Tag.TAG_STRING)){
                playerBookmarks.add(id.getAsString());
            }
        }

        if(compoundTag.contains("spell_levels")) {
            CompoundTag spellsTag = compoundTag.getCompound("spell_levels");
            for(String key : spellsTag.getAllKeys()) playerSpells.put(key, spellsTag.getInt(key));
        }

        if(compoundTag.contains("spell_slots")){
            ListTag slotsTag = compoundTag.getList("spell_slots", Tag.TAG_STRING);
            for (int i = 0; i < Math.min(3, slotsTag.size()); i++) {
                var spellId = slotsTag.getString(i);
                assignedSpells[i] = spellId.isEmpty() ? null : spellId;
            }
        }

        if (compoundTag.contains("active_cooldowns")) {
            CompoundTag cooldownsTag = compoundTag.getCompound("active_cooldowns");
            for (String key : cooldownsTag.getAllKeys()) {
                cooldowns.add(new Cooldown(key, cooldownsTag.getInt(key)));
            }
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

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Tyzsskills.MODID);

    public static final Supplier<AttachmentType<PlayerData>> DATA = ATTACHMENT_TYPES.register(
            "player_skill_data",
            () -> AttachmentType.serializable(PlayerData::new).build());
}
