package com.tyzsskills.impl.server.payloads;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.impl.client.ClientCache;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SkillBookmarksPayload(String id, boolean state) implements CustomPacketPayload{
    public static final Type<SkillBookmarksPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "skill_bookmark_payload"));


    public static final StreamCodec<ByteBuf, SkillBookmarksPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SkillBookmarksPayload::id,
            ByteBufCodecs.BOOL, SkillBookmarksPayload::state,
            SkillBookmarksPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type(){
        return TYPE;
    }

    public static void Handle(final SkillBookmarksPayload payload, final IPayloadContext ctx){
        ctx.enqueueWork(() -> {ClientCache.syncBookmark(payload.id(), payload.state());} );
    }

}
