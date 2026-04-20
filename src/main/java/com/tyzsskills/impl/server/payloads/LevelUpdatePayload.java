package com.tyzsskills.impl.server.payloads;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.impl.client.ClientCache;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record LevelUpdatePayload(int level) implements CustomPacketPayload{
    public static final Type<LevelUpdatePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "level_update_payload"));


    public static final StreamCodec<ByteBuf, LevelUpdatePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, LevelUpdatePayload::level,
            LevelUpdatePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type(){
        return TYPE;
    }

    public static void Handle(final LevelUpdatePayload payload, final IPayloadContext ctx){
        ctx.enqueueWork(() -> {ClientCache.updateClientCacheLevel(payload.level());} );
    }

}
