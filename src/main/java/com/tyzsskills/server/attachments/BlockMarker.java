package com.tyzsskills.server.attachments;

import com.tyzsskills.Tyzsskills;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class BlockMarker implements INBTSerializable<ListTag> {

    private final Set<Long> placedBLocks = new HashSet<>();

    public void addBlock(BlockPos pos){placedBLocks.add(pos.asLong());}
    public void removeBlock(BlockPos pos){placedBLocks.remove(pos.asLong());}

    public boolean isPlacedByPlayer(BlockPos pos){return placedBLocks.contains(pos.asLong());}

    @Override
    public ListTag serializeNBT(HolderLookup.Provider provider) {
        var list = new ListTag();
        for(var pos : placedBLocks){list.add(LongTag.valueOf(pos));}
        return list;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, ListTag tags) {
        placedBLocks.clear();
        for(var tag : tags){
            if(tag instanceof LongTag longTag){placedBLocks.add(longTag.getAsLong());}
        }
    }

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Tyzsskills.MODID);

    public static final Supplier<AttachmentType<BlockMarker>> PLACED_BLOCKS = ATTACHMENT_TYPES.register(
            "placed_blocks", () -> AttachmentType.serializable(BlockMarker::new).build()
    );

    public static void MarkBlock(Level level, BlockPos pos){
        if(level.isClientSide) return;
        LevelChunk chunk = level.getChunkAt(pos);
        chunk.getData(PLACED_BLOCKS).addBlock(pos);
        chunk.setUnsaved(true);
    }

    public static void RemoveBlock(Level level, BlockPos pos){
        if(level.isClientSide) return;
        LevelChunk chunk = level.getChunkAt(pos);
        chunk.getData(PLACED_BLOCKS).removeBlock(pos);
        chunk.setUnsaved(true);
    }


    public static boolean IsPlayerPlaced(Level level, BlockPos pos){
        if(level.isClientSide) return false;
        LevelChunk chunk = level.getChunkAt(pos);
        return chunk.getData(PLACED_BLOCKS).isPlacedByPlayer(pos);
    }
}
