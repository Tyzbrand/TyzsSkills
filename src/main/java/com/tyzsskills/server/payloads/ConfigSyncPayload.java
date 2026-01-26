package com.tyzsskills.server.payloads;

import com.tyzsskills.Config;
import com.tyzsskills.Tyzsskills;
import com.tyzsskills.client.ClientCache;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;

public record ConfigSyncPayload(boolean refundSys, double refundPer, int traitLevel, boolean traitSys) implements CustomPacketPayload{
    public static final Type<ConfigSyncPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "config_sync_payload"));


    public static final StreamCodec<ByteBuf, ConfigSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ConfigSyncPayload::refundSys,
            ByteBufCodecs.DOUBLE, ConfigSyncPayload::refundPer,
            ByteBufCodecs.INT, ConfigSyncPayload::traitLevel,
            ByteBufCodecs.BOOL, ConfigSyncPayload::traitSys,
            ConfigSyncPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type(){
        return TYPE;
    }

    public static void Handle(final ConfigSyncPayload payload, final IPayloadContext ctx){
        ctx.enqueueWork(() -> {
            Map<String, Object> configMap = new HashMap<>();

            configMap.put(Config.REFUND_SYSTEM_KEY , payload.refundSys());
            configMap.put(Config.REFUND_PERCENTAGE_KEY, payload.refundPer());
            configMap.put(Config.TRAIT_UNLOCK_LEVEL_KEY, payload.traitLevel());
            configMap.put(Config.TRAIT_SYSTEM_KEY, payload.traitSys());

            ClientCache.SyncConfig(configMap);
        });
    }

}
