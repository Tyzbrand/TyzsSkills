package com.tyzsskills.server.payloads;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.client.ClientCache;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record StatsSpEarnedPayload(int spGain) implements CustomPacketPayload{
    public static final Type<StatsSpEarnedPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "stats_sp_gain_payload"));


    public static final StreamCodec<ByteBuf, StatsSpEarnedPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, StatsSpEarnedPayload::spGain,
            StatsSpEarnedPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type(){
        return TYPE;
    }

    public static void Handle(final StatsSpEarnedPayload payload, final IPayloadContext ctx){
        ctx.enqueueWork(() -> {
            ClientCache.UpdateClientStatSpEarned(payload.spGain());
        });
    }

}
