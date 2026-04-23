package com.tyzsskills.impl.server.payloads;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.api.Enums;
import com.tyzsskills.impl.client.ClientCache;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public record ResetPayload(Enums.ResetType resetType) implements CustomPacketPayload {
    public static final Type<ResetPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "reset_payload"));


    public static final StreamCodec<ByteBuf, ResetPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT.map(val -> Enums.ResetType.values()[val], Enums.ResetType::ordinal), ResetPayload::resetType,
            ResetPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type(){
        return TYPE;
    }

    public static void Handle(final ResetPayload payload, final IPayloadContext ctx){
        ctx.enqueueWork(() -> {ClientCache.clearCache(payload.resetType());});
    }

}
