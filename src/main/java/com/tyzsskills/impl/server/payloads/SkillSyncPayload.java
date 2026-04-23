package com.tyzsskills.impl.server.payloads;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.server.model.Skill;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record SkillSyncPayload(List<Skill> skills) implements CustomPacketPayload{
    public static final Type<SkillSyncPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "skill_sync_payload"));


    public static final StreamCodec<FriendlyByteBuf, SkillSyncPayload> STREAM_CODEC = StreamCodec.composite(
            Skill.STREAM_CODEC.apply(ByteBufCodecs.list()),
            SkillSyncPayload::skills,
            SkillSyncPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type(){
        return TYPE;
    }

    public static void Handle(final SkillSyncPayload payload, final IPayloadContext ctx){
        ctx.enqueueWork(() -> {ClientCache.updateSkills(payload.skills());} );
    }

}
