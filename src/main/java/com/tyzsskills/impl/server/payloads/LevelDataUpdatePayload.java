package com.tyzsskills.impl.server.payloads;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.server.xp.XpManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record LevelDataUpdatePayload(XpManager.LevelData data) implements CustomPacketPayload {

    public static final Type<LevelDataUpdatePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "level_data_update_payload"));


    public static final StreamCodec<ByteBuf, LevelDataUpdatePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, (payload) -> payload.data.goal(),
            ByteBufCodecs.INT, (payload) -> payload.data.reward(),
            (goal, reward) -> new LevelDataUpdatePayload(new XpManager.LevelData(goal, reward))

    );

    @Override
    public Type<? extends CustomPacketPayload> type(){
        return TYPE;
    }

    public static void Handle(final LevelDataUpdatePayload payload, final IPayloadContext ctx){
        ctx.enqueueWork(() -> {
            ClientCache.UpdateClientCacheLevelData(payload.data());} );
    }
}
