package com.tyzsskills.impl.server.payloads;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.api.Enums;
import com.tyzsskills.impl.server.skills.SkillManager;
import com.tyzsskills.impl.server.sp.SpManager;
import io.netty.buffer.ByteBuf;
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
import org.jetbrains.annotations.NotNull;


public record CActionSkillPayload(String id, Enums.ClientAction actionType) implements CustomPacketPayload{
    public static final Type<CActionSkillPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "action_skill_payload"));


    public static final StreamCodec<RegistryFriendlyByteBuf, CActionSkillPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, CActionSkillPayload::id,
            StreamCodec.of(FriendlyByteBuf::writeEnum, buf -> buf.readEnum(Enums.ClientAction.class)), CActionSkillPayload::actionType,
            CActionSkillPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type(){
        return TYPE;
    }

    public static void Handle(final CActionSkillPayload payload, final IPayloadContext ctx){
        ctx.enqueueWork(() -> {

            if(ctx.player() instanceof ServerPlayer player){
                if(PayloadSecurity.isSpamming(player)) {
                    player.sendSystemMessage(Component.literal("ANTI DDOS"));
                    return;
                }

                var success = true;
                switch(payload.actionType()){
                    case PURCHASE -> success = SkillManager.tryBuySkill(player, payload.id());
                    case REFUND -> success = SkillManager.tryRefundSkill(player, payload.id());
                    case BULK_PURCHASE -> success = SkillManager.tryBulkBuy(player, payload.id());
                    case BULK_REFUND -> success = SkillManager.tryBulkRefund(player, payload.id());
                    case BOOKMARK -> SkillManager.bookmarkSkill(player, payload.id());
                }

                if(!success){
                    PacketDistributor.sendToPlayer(player, new UpdatePayloads.SpPayload(SpManager.getSP(player)));
                    PacketDistributor.sendToPlayer(player, new UpdatePayloads.SkillLevelPayload(payload.id(), SkillManager.getPlayerSkillLevel(player, payload.id())));
                }
            }

        });
    }

}
