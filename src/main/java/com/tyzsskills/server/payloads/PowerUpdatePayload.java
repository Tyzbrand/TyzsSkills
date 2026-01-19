package com.tyzsskills.server.payloads;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.client.ClientCache;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PowerUpdatePayload(int power) implements CustomPacketPayload{
    public static final Type<PowerUpdatePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "power_update_payload"));


    public static final StreamCodec<ByteBuf, PowerUpdatePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PowerUpdatePayload::power,
            PowerUpdatePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type(){
        return TYPE;
    }

    public static void Handle(final PowerUpdatePayload payload, final IPayloadContext ctx){
        ctx.enqueueWork(() -> {ClientCache.UpdateClientCachePower(payload.power);} );
    }

}
