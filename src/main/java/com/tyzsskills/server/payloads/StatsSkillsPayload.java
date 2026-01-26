package com.tyzsskills.server.payloads;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.client.ClientCache;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record StatsSkillsPayload(int skillGain) implements CustomPacketPayload{
    public static final Type<StatsSkillsPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "stats_skill_gain_payload"));


    public static final StreamCodec<ByteBuf, StatsSkillsPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, StatsSkillsPayload::skillGain,
            StatsSkillsPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type(){
        return TYPE;
    }

    public static void Handle(final StatsSkillsPayload payload, final IPayloadContext ctx){
        ctx.enqueueWork(() -> {
            ClientCache.UpdateClientStatSkills(payload.skillGain());
        });
    }

}
