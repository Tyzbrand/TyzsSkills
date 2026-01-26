package com.tyzsskills.server.attachments;

import com.tyzsskills.Tyzsskills;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class StatsTracker implements INBTSerializable<CompoundTag> {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Tyzsskills.MODID);
    public static final Supplier<AttachmentType<StatsTracker>> DATA = ATTACHMENT_TYPES.register(
            "player_stats", () -> AttachmentType.serializable(StatsTracker::new).copyOnDeath().build());


    private float allTimeXp = 0f;
    private int totalSpEarned = 0;
    private int totalSpSpent = 0;
    private int skillsUnlocked = 0;

    public StatsTracker(){}

    public void addXp(float amount){
        if(amount > 0) this.allTimeXp += amount;
    }

    public void addSpEarned(int amount){
        if(amount > 0) this.totalSpEarned += amount;
    }

    public void addSpSpent(int amount){
        if(amount > 0) this.totalSpSpent+= amount;
    }

    public void addSkillUnlocked(int amount){
        this.skillsUnlocked += amount;
    }

    public void resetStats(){
        this.allTimeXp = 0f;
        this.totalSpEarned = 0;
        this.totalSpSpent = 0;
        this.skillsUnlocked = 0;
    }

    public void copyFrom(StatsTracker old){
        this.allTimeXp = old.allTimeXp;
        this.totalSpEarned = old.totalSpEarned;
        this.totalSpSpent = old.totalSpSpent;
        this.skillsUnlocked = old.skillsUnlocked;
    }

    //Getters
    public float getAllTimeXp() {return this.allTimeXp;}
    public int getTotalSpEarned() {return this.totalSpEarned;}
    public int getTotalSpSpent() {return this.totalSpSpent;}
    public int getSkillsUnlocked() {return this.skillsUnlocked;}

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("allTimeXp", allTimeXp);
        tag.putInt("totalSpEarned", totalSpEarned);
        tag.putInt("totalSpSpent", totalSpSpent);
        tag.putInt("skillUnlocked", skillsUnlocked);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {
        if(compoundTag.contains("allTimeXp")) allTimeXp = compoundTag.getFloat("allTimeXp");
        if(compoundTag.contains("totalSpEarned")) totalSpEarned = compoundTag.getInt("totalSpEarned");
        if(compoundTag.contains("totalSpSpent")) totalSpSpent = compoundTag.getInt("totalSpSpent");
        if(compoundTag.contains("skillUnlocked")) skillsUnlocked = compoundTag.getInt("skillUnlocked");
    }
}
