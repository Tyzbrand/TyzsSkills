package com.tyzsskills.server.payloads;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.client.screen.LevelTriggerOverlay;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record LevelToastPayload(int level, int spGain) implements CustomPacketPayload{
    public static final Type<LevelToastPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "level_toast_payload"));


    public static final StreamCodec<ByteBuf, LevelToastPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, LevelToastPayload::level,
            ByteBufCodecs.INT, LevelToastPayload::spGain,
            LevelToastPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type(){
        return TYPE;
    }

    public static void Handle(final LevelToastPayload payload, final IPayloadContext ctx){
        ctx.enqueueWork(() -> {
            LevelTriggerOverlay.ShowLevelUp(payload.level(), payload.spGain());
            } );
    }

}
