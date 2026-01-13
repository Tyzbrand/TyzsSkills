package com.tyzsskills.server.payloads;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.client.ClientCache;
import com.tyzsskills.client.screen.SkillTriggerOverlay;
import com.tyzsskills.server.model.Skill;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record SkillTriggerPayload(String id) implements CustomPacketPayload{
    public static final Type<SkillTriggerPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "skill_trigger_payload"));


    public static final StreamCodec<FriendlyByteBuf, SkillTriggerPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SkillTriggerPayload::id,
            SkillTriggerPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type(){
        return TYPE;
    }

    public static void Handle(final SkillTriggerPayload payload, final IPayloadContext ctx){
        ctx.enqueueWork(() -> {
            SkillTriggerOverlay.ShowSkillIcon(payload.id());
        } );
    }

}
