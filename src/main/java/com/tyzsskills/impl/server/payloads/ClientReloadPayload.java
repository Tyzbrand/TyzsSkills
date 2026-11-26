package com.tyzsskills.impl.server.payloads;

import com.tyzsskills.Settings;
import com.tyzsskills.Tyzsskills;
import com.tyzsskills.impl.server.core.ErrorManager;
import com.tyzsskills.integration.ui.renderers.UIOverlayRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientReloadPayload() implements CustomPacketPayload {

    public static final Type<ClientReloadPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "client_reload_payload"));

    public static final StreamCodec<FriendlyByteBuf, ClientReloadPayload> STREAM_CODEC = StreamCodec.unit(new ClientReloadPayload());


    @Override
    public Type<? extends CustomPacketPayload> type(){
        return TYPE;
    }

    public static void Handle(final ClientReloadPayload payload, final IPayloadContext ctx){
        ctx.enqueueWork(() -> {
            ErrorManager.clear(true);

            Settings.Client.readSettings();

            UIOverlayRenderer.reloadOverlays();

            ErrorManager.printErrors(ctx.player(), true);
        });
    }
}
