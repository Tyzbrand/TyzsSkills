package com.tyzsskills.impl.server.payloads;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.impl.client.ClientCache;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record StatsSpSpentPayload(int spSpent) implements CustomPacketPayload{
    public static final Type<StatsSpSpentPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "stats_sp_spent_payload"));


    public static final StreamCodec<ByteBuf, StatsSpSpentPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, StatsSpSpentPayload::spSpent,
            StatsSpSpentPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type(){
        return TYPE;
    }

    public static void Handle(final StatsSpSpentPayload payload, final IPayloadContext ctx){
        ctx.enqueueWork(() -> {
            ClientCache.UpdateClientStatSpSpent(payload.spSpent());
        });
    }

}
