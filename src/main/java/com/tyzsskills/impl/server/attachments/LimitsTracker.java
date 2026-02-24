package com.tyzsskills.impl.server.attachments;

import com.tyzsskills.Tyzsskills;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class LimitsTracker implements INBTSerializable<CompoundTag> {

    private int spLifetime = 0;
    private float xpInCycle = 0f;
    private long lastCycleTimestamp = 0L;

    public void incrSp(int amount){
        if(amount > 0) spLifetime += amount;
    }
    public int getSpLifetime(){return spLifetime;}

    public void incrXp(float amount){
        if(amount > 0f) xpInCycle += amount;
    }
    public float getXpInCycle(){return xpInCycle;}

    public void startNewCycle(long timestamp){
        if(timestamp >= 0L){
            lastCycleTimestamp = timestamp;
            xpInCycle = 0f;
        }
    }
    public long getLastCycleTimestamp(){return lastCycleTimestamp;}


    public void resetLimits(){
        spLifetime = 0;
        xpInCycle = 0f;
        lastCycleTimestamp = 0L;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        var tag = new CompoundTag();

        tag.putInt("sp_lifetime", spLifetime);
        tag.putFloat("xp_in_cycle", xpInCycle);
        tag.putLong("last_cycle_timestamp", lastCycleTimestamp);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {
        if(compoundTag.contains("sp_lifetime")) spLifetime = compoundTag.getInt("sp_lifetime");
        if(compoundTag.contains("xp_in_cycle")) xpInCycle = compoundTag.getFloat("xp_in_cycle");
        if(compoundTag.contains("last_cycle_timestamp")) lastCycleTimestamp = compoundTag.getLong("last_cycle_timestamp");

    }

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Tyzsskills.MODID);
    public static final Supplier<AttachmentType<LimitsTracker>> DATA = ATTACHMENT_TYPES.register(
            "player_limits", () -> AttachmentType.serializable(LimitsTracker::new).copyOnDeath().build());
}

