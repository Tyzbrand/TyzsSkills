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

import java.util.*;
import java.util.function.Supplier;

public class PlayerData implements INBTSerializable<CompoundTag> {

    private final Map<String, Integer> playerSkills = new HashMap<>();
    private final Set<String> playerBookmarks = new HashSet<>();
    private int playerLevel = 1;
    private int playerSP = 0;
    private float playerXP = 0f;
    private int playerPower = 0;


    //-----------------Skill level-----------------
    public void setSkillLevel(String id, int lvl){
        if(id == null || lvl < 0) return;

        if(lvl == 0 && playerSkills.containsKey(id)) playerSkills.remove(id);
        else playerSkills.put(id, lvl);
    }
    public int getSkillLevel(String id){return playerSkills.getOrDefault(id, 0);}


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


    //-----------------Power-----------------
    public void setPower(int power){playerPower = Math.max(0, power);}
    public int getPower(){return playerPower;}


    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        var tag = new CompoundTag();

        CompoundTag skillLevels = new CompoundTag();
        for(var entry : playerSkills.entrySet()) skillLevels.putInt(entry.getKey(), entry.getValue());
        tag.put("skill_levels", skillLevels);

        ListTag bookmarks = new ListTag();
        playerBookmarks.forEach(b -> bookmarks.add(StringTag.valueOf(b)));
        tag.put("skill_bookmarks", bookmarks);

        tag.putInt("skill_level", playerLevel);
        tag.putInt("skill_point", playerSP);
        tag.putFloat("skill_xp", playerXP);
        tag.putInt("trait_power", playerPower);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {
        playerBookmarks.clear();
        playerSkills.clear();

        if(compoundTag.contains("skill_levels")) {
            CompoundTag skillsTag = compoundTag.getCompound("skill_levels");
            for(String key : skillsTag.getAllKeys()) playerSkills.put(key, skillsTag.getInt(key));
        }

        if(compoundTag.contains("skill_bookmarks")){
            for (var id : compoundTag.getList("skill_bookmarks", Tag.TAG_STRING)){
                playerBookmarks.add(id.getAsString());
            }
        }

        if(compoundTag.contains("skill_level")) playerLevel = compoundTag.getInt("skill_level");
        if(compoundTag.contains("skill_point")) playerSP = compoundTag.getInt("skill_point");
        if(compoundTag.contains("skill_xp")) playerXP = compoundTag.getFloat("skill_xp");
        if(compoundTag.contains("trait_power")) playerPower = compoundTag.getInt("trait_power");
    }

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Tyzsskills.MODID);

    public static final Supplier<AttachmentType<PlayerData>> DATA = ATTACHMENT_TYPES.register(
            "player_skill_data",
            () -> AttachmentType.serializable(PlayerData::new)
                    .copyOnDeath()
                    .build()
    );
}
