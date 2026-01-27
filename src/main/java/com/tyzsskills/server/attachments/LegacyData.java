package com.tyzsskills.server.attachments;

import com.tyzsskills.Tyzsskills;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class LegacyData implements INBTSerializable<CompoundTag> {

    private final Map<String, Double> oldVariables = new HashMap<>();

    public double getOldValue(String key){
        return oldVariables.getOrDefault(key, 0.0);
    }

    public boolean hasData(){
        return !oldVariables.isEmpty();
    }


    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {
        for(String key : compoundTag.getAllKeys()){
            try{
                double value = compoundTag.getDouble(key);
                oldVariables.put(key, value);
            }
            catch (Exception ignored){

            }
        }
    }

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Tyzsskills.MODID);

    public static final Supplier<AttachmentType<LegacyData>> PLAYER_VARIABLES = ATTACHMENT_TYPES.register(
            "player_variables", () -> AttachmentType.serializable(LegacyData::new).build()
    );
}
