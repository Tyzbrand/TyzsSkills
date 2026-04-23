package com.tyzsskills.impl.server.payloads;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.impl.client.ClientCache;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SpUpdatePayload(int sp) implements CustomPacketPayload{
    public static final Type<SpUpdatePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "sp_update_payload"));


    public static final StreamCodec<ByteBuf, SpUpdatePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SpUpdatePayload::sp,
            SpUpdatePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type(){
        return TYPE;
    }

    public static void Handle(final SpUpdatePayload payload, final IPayloadContext ctx){
        ctx.enqueueWork(() -> {ClientCache.updateClientCacheSP(payload.sp());} );
    }

}
