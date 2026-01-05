package com.tyzsskills.server.payloads;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.client.ClientCache;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientMainCachePayload(int level, int sp, float xp) implements CustomPacketPayload{
    public static final Type<ClientMainCachePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "client_main_cache"));


    public static final StreamCodec<ByteBuf, ClientMainCachePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ClientMainCachePayload::level,
            ByteBufCodecs.INT, ClientMainCachePayload::sp,
            ByteBufCodecs.FLOAT, ClientMainCachePayload::xp,
            ClientMainCachePayload::new
    );


    @Override
    public Type<? extends CustomPacketPayload> type(){
        return TYPE;
    }

    public static void Handle(final ClientMainCachePayload payload, final IPayloadContext ctx){
        ctx.enqueueWork(() -> {
            ClientCache.UpdateClientMainCache(payload.level(), payload.sp(), payload.xp());
        } );
    }

}
