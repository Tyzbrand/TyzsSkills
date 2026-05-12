package com.tyzsskills.impl.server.payloads;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.api.model.Category;
import com.tyzsskills.impl.client.ClientCache;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;


import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public record CategoriesPayload(Map<String, Category> categories) implements CustomPacketPayload {
    public static final Type<CategoriesPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "categories_payload"));


    public static final StreamCodec<ByteBuf, CategoriesPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, Category.STREAM_CODEC),
            CategoriesPayload::categories,
            CategoriesPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type(){
        return TYPE;
    }

    public static void Handle(final CategoriesPayload payload, final IPayloadContext ctx){
        ctx.enqueueWork(() -> ClientCache.updateClientCacheCategories(payload.categories()));
    }

}