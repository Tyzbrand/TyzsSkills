package com.tyzsskills.impl.server.payloads;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.impl.client.ClientCache;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record StatsXpPayload(float xpGain) implements CustomPacketPayload{
    public static final Type<StatsXpPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "stats_xp_payload"));


    public static final StreamCodec<ByteBuf, StatsXpPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, StatsXpPayload::xpGain,
            StatsXpPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type(){
        return TYPE;
    }

    public static void Handle(final StatsXpPayload payload, final IPayloadContext ctx){
        ctx.enqueueWork(() -> {
            ClientCache.updateClientStatXP(payload.xpGain());
        });
    }

}
