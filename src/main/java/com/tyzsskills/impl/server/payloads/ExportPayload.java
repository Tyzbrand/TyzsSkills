package com.tyzsskills.impl.server.payloads;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.impl.client.ExportManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ExportPayload() implements CustomPacketPayload {

    public static final Type<ExportPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "export_payload"));

    public static final StreamCodec<FriendlyByteBuf, ExportPayload> STREAM_CODEC = StreamCodec.unit(new ExportPayload());


    @Override
    public Type<? extends CustomPacketPayload> type(){
        return TYPE;
    }

    public static void Handle(final ExportPayload payload, final IPayloadContext ctx){ctx.enqueueWork(ExportManager::saveExport);}
}
