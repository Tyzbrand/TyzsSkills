package com.tyzsskills.server.attachments;

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

public class ExplorationProgression implements INBTSerializable<CompoundTag> {

    private final Set<String> visitedBiomes = new HashSet<>();
    private final Set<String> visitedDimensions = new HashSet<>();
    private final Set<String> visitedStructures = new HashSet<>();
    private long lastSleepDay = -1L;

    public void addBiome(String biomeID){
        visitedBiomes.add(biomeID.toLowerCase());
    }

    public boolean hasDiscoveredBiome(String biomeID){
        return visitedBiomes.contains(biomeID.toLowerCase());
    }

    public void addDimension(String dimensionID){
        if(!hasDiscoveredDimension(dimensionID)) visitedDimensions.add(dimensionID.toLowerCase());
        else visitedDimensions.remove(dimensionID.toLowerCase());
    }
    public boolean hasDiscoveredDimension(String dimensionID){
        return visitedDimensions.contains(dimensionID.toLowerCase());
    }

    public void addStructure(String structureID){
        if(!hasDiscoveredStructure(structureID)) visitedStructures.add(structureID.toLowerCase());
        else visitedStructures.remove(structureID.toLowerCase());
    }
    public boolean hasDiscoveredStructure(String structureID){
        return visitedStructures.contains(structureID.toLowerCase());
    }

    public boolean hasAlreadySlept(long currentDay){
        return currentDay <= lastSleepDay;
    }

    public void setSleepDay(long currentDay){
        if(currentDay >= 0L) lastSleepDay = currentDay;
    }


    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();

        ListTag biomeList = new ListTag();
        visitedBiomes.forEach(b -> biomeList.add(StringTag.valueOf(b)));
        tag.put("biomes", biomeList);

        ListTag dimensionList = new ListTag();
        visitedDimensions.forEach(b -> dimensionList.add(StringTag.valueOf(b)));
        tag.put("dimensions", dimensionList);

        ListTag structureList = new ListTag();
        visitedStructures.forEach(b -> structureList.add(StringTag.valueOf(b)));
        tag.put("structures", structureList);

        tag.putLong("last_sleep_day", lastSleepDay);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {
        visitedBiomes.clear();
        visitedDimensions.clear();
        visitedStructures.clear();

        if(compoundTag.contains("biomes")){
            for(var biome : compoundTag.getList("biomes", Tag.TAG_STRING)){
                visitedBiomes.add(biome.getAsString());
            }
        }

        if(compoundTag.contains("dimensions")){
            for(var dimension : compoundTag.getList("dimensions", Tag.TAG_STRING)){
                visitedDimensions.add(dimension.getAsString());
            }
        }

        if(compoundTag.contains("structures")){
            for(var structure : compoundTag.getList("structures", Tag.TAG_STRING)){
                visitedStructures.add(structure.getAsString());
            }
        }

        if(compoundTag.contains("last_sleep_day")) lastSleepDay = compoundTag.getLong("last_sleep_day");
    }

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Tyzsskills.MODID);

    public static final Supplier<AttachmentType<ExplorationProgression>> DATA = ATTACHMENT_TYPES.register(
            "exploration_xp_data",
            () -> AttachmentType.serializable(ExplorationProgression::new)
                    .copyOnDeath()
                    .build()
    );
}
