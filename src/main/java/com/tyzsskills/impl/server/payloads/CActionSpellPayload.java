package com.tyzsskills.impl.server.payloads;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.api.Enums;
import com.tyzsskills.impl.server.sp.SpManager;
import com.tyzsskills.impl.server.spells.SpellManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public record CActionSpellPayload(String id, Enums.SpellAction actionType, int slot) implements CustomPacketPayload{
    public static final Type<CActionSpellPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "action_spell_payload"));


    public static final StreamCodec<RegistryFriendlyByteBuf, CActionSpellPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, CActionSpellPayload::id,
            StreamCodec.of(FriendlyByteBuf::writeEnum, buf -> buf.readEnum(Enums.SpellAction.class)), CActionSpellPayload::actionType,
            ByteBufCodecs.INT, CActionSpellPayload::slot,
            CActionSpellPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type(){
        return TYPE;
    }

    public static void Handle(final CActionSpellPayload payload, final IPayloadContext ctx){
        ctx.enqueueWork(() -> {

            if(ctx.player() instanceof ServerPlayer player){
                if(PayloadSecurity.isSpamming(player)) {
                    player.sendSystemMessage(Component.literal("ANTI DDOS"));
                    return;
                }

                var success = true;
                switch(payload.actionType()){
                    case PURCHASE -> success = SpellManager.tryBuySpell(player, payload.id());
                    case ASSIGNMENT -> success = SpellManager.tryAssignSpell(player, payload.id(), payload.slot);
                    case CAST -> success = SpellManager.tryCastSpell(player, payload.id());
                }

                if(!success){
                    PacketDistributor.sendToPlayer(player, new UpdatePayloads.SpPayload(SpManager.getSP(player)));
                    PacketDistributor.sendToPlayer(player, new UpdatePayloads.SpellPurchasePayload(payload.id(), SpellManager.isSpellPurchased(player, payload.id())));


                    if(payload.slot() > 0){
                        PacketDistributor.sendToPlayer(player, new UpdatePayloads.SpellSlotChange(payload.slot(), SpellManager.getSpellInSlot(player, payload.slot())));
                    }

                }
            }

        });
    }

}
