package com.tyzsskills.impl.server.payloads;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.api.Enums;
import com.tyzsskills.impl.server.skills.SkillManager;
import com.tyzsskills.impl.server.sp.SpManager;
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


public record CActionSkillPayload(String id, Enums.SkillAction actionType) implements CustomPacketPayload{
    public static final Type<CActionSkillPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "action_skill_payload"));


    public static final StreamCodec<RegistryFriendlyByteBuf, CActionSkillPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, CActionSkillPayload::id,
            StreamCodec.of(FriendlyByteBuf::writeEnum, buf -> buf.readEnum(Enums.SkillAction.class)), CActionSkillPayload::actionType,
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

                var skill = SkillManager.getSkill(payload.id());
                if(skill == null) return;

                var success = true;
                switch(payload.actionType()){
                    case PURCHASE -> success = SkillManager.tryBuySkill(player, skill);
                    case REFUND -> success = SkillManager.tryRefundSkill(player, skill);
                    case BULK_PURCHASE -> success = SkillManager.tryBulkBuy(player, skill);
                    case BULK_REFUND -> success = SkillManager.tryBulkRefund(player, skill);
                    case BOOKMARK -> SkillManager.bookmarkSkill(player, skill);
                    case DEACTIVATION -> success = SkillManager.tryToggleSkillDeactivation(player, skill);
                }

                if(!success){
                    PacketDistributor.sendToPlayer(player, new UpdatePayloads.SpPayload(SpManager.getSP(player)));
                    PacketDistributor.sendToPlayer(player, new UpdatePayloads.DeactivationsPayload(payload.id(), SkillManager.isSkillDeactivated(player, skill)));
                    PacketDistributor.sendToPlayer(player, new UpdatePayloads.SkillLevelPayload(payload.id(), SkillManager.getPlayerSkillLevel(player, skill)));
                }
            }

        });
    }

}
