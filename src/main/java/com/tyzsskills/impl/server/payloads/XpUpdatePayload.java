package com.tyzsskills.impl.server.payloads;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.impl.client.ClientCache;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record XpUpdatePayload(float xp, float gained, boolean triggersOverlay, float limit) implements CustomPacketPayload{
    public static final Type<XpUpdatePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "xp_update_payload"));


    public static final StreamCodec<ByteBuf, XpUpdatePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, XpUpdatePayload::xp,
            ByteBufCodecs.FLOAT, XpUpdatePayload::gained,
            ByteBufCodecs.BOOL, XpUpdatePayload::triggersOverlay,
            ByteBufCodecs.FLOAT, XpUpdatePayload::limit,
            XpUpdatePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type(){
        return TYPE;
    }

    public static void Handle(final XpUpdatePayload payload, final IPayloadContext ctx){
        ctx.enqueueWork(() -> {ClientCache.updateClientCacheXP(payload.xp(), payload.gained(), payload.triggersOverlay(), payload.limit());} );
    }

}
