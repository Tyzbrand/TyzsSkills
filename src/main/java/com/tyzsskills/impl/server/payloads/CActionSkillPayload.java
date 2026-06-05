package com.tyzsskills.impl.server.payloads;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.impl.server.skills.SkillManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public record CActionSkillPayload(String id, int actionType) implements CustomPacketPayload{
    public static final Type<CActionSkillPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "action_skill_payload"));


    public static final StreamCodec<ByteBuf, CActionSkillPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, CActionSkillPayload::id,
            ByteBufCodecs.INT, CActionSkillPayload::actionType,
            CActionSkillPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type(){
        return TYPE;
    }

    public static void Handle(final CActionSkillPayload payload, final IPayloadContext ctx){
        ctx.enqueueWork(() -> {
            if(ctx.player() instanceof ServerPlayer player){
                if(payload.actionType() == 0) SkillManager.tryBuySkill(player, payload.id().toLowerCase()); //PURCHASE
                else if (payload.actionType() == 1) SkillManager.tryRefundSkill(player, payload.id().toLowerCase()); //REFUND
                else if (payload.actionType() == 2) SkillManager.bookmarkSkill(player, payload.id().toLowerCase()); // BOOKMARK
                else if(payload.actionType() == 3) SkillManager.tryBulkBuy(player, payload.id().toLowerCase()); //MAX PURCHASE
                else if(payload.actionType() == 4) SkillManager.tryBulkRefund(player, payload.id().toLowerCase()); //MAX REFUND
            }

        });
    }

}
