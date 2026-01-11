package com.tyzsskills.server.payloads;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.client.ClientCache;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SkillLevelSyncPayload(String id, int level) implements CustomPacketPayload{
    public static final Type<SkillLevelSyncPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "skill_level_sync_payload"));


    public static final StreamCodec<ByteBuf, SkillLevelSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SkillLevelSyncPayload::id,
            ByteBufCodecs.INT, SkillLevelSyncPayload::level,
            SkillLevelSyncPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type(){
        return TYPE;
    }

    public static void Handle(final SkillLevelSyncPayload payload, final IPayloadContext ctx){
        ctx.enqueueWork(() -> {ClientCache.UpdateSkillLevels(payload.id(), payload.level());} );
    }

}
