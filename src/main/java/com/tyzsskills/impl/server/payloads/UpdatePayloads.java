package com.tyzsskills.impl.server.payloads;

import com.tyzsskills.Config;
import com.tyzsskills.Tyzsskills;
import com.tyzsskills.api.model.Category;
import com.tyzsskills.api.records.LevelData;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.server.Level.LevelManager;
import com.tyzsskills.impl.server.attachments.StatsTracker;
import com.tyzsskills.impl.server.categories.CategoryLoader;
import com.tyzsskills.impl.server.model.Skill;
import com.tyzsskills.impl.server.skills.SkillManager;
import com.tyzsskills.impl.server.sp.SpManager;
import com.tyzsskills.impl.server.xp.XpManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdatePayloads {

    public record PlayerSyncData(int level, int sp, float xp, float totalXp, int spEarned, int spSpent){
        public static final StreamCodec<ByteBuf, PlayerSyncData> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, PlayerSyncData::level,
                ByteBufCodecs.INT, PlayerSyncData::sp,
                ByteBufCodecs.FLOAT, PlayerSyncData::xp,
                ByteBufCodecs.FLOAT, PlayerSyncData::totalXp,
                ByteBufCodecs.INT, PlayerSyncData::spEarned,
                ByteBufCodecs.INT, PlayerSyncData::spSpent,
                PlayerSyncData::new
        );
    }
    public record ConfigSyncData(Map<String, Boolean> booleanMap, Map<String, Double> doubleMap){
        public static final StreamCodec<ByteBuf, ConfigSyncData> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.BOOL), ConfigSyncData::booleanMap,
                ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.DOUBLE), ConfigSyncData::doubleMap,
                ConfigSyncData::new
        );
    }
    public record ServerSyncData(List<Skill> skills, List<String> bookmarks, Map<String, Category> categories, Map<String, Integer> playerSkillLevels){
        public static final StreamCodec<RegistryFriendlyByteBuf, ServerSyncData> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.collection(ArrayList::new, Skill.STREAM_CODEC), ServerSyncData::skills,
                ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.STRING_UTF8), ServerSyncData::bookmarks,
                ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, Category.STREAM_CODEC), ServerSyncData::categories,
                ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.INT), ServerSyncData::playerSkillLevels,
                ServerSyncData::new
        );
    }

    //INIT
    public record InitPayload(ServerSyncData serverData, PlayerSyncData playerData, ConfigSyncData configData, LevelData levelData) implements CustomPacketPayload{

        public static final Type<InitPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "init_update_payload"));

        public static final StreamCodec<RegistryFriendlyByteBuf, InitPayload> STREAM_CODEC = StreamCodec.composite(
                ServerSyncData.STREAM_CODEC, InitPayload::serverData,
                PlayerSyncData.STREAM_CODEC, InitPayload::playerData,
                ConfigSyncData.STREAM_CODEC, InitPayload::configData,
                LevelData.STREAM_CODEC, InitPayload::levelData,
                InitPayload::new
        );


        @Override
        public @NotNull Type<? extends CustomPacketPayload> type(){
            return TYPE;
        }

        public static void Handle(final InitPayload payload, final IPayloadContext ctx){
            ctx.enqueueWork(() -> {
                if(!ClientCache.isReady()) ClientCache.init();

                ClientCache.get().UPDATE.updateInit(payload);
            });
        }
    }

    //METADATA
    public record LevelPayload(int level) implements CustomPacketPayload {
            public static final Type<LevelPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "level_update_payload"));


            public static final StreamCodec<ByteBuf, LevelPayload> STREAM_CODEC = StreamCodec.composite(
                    ByteBufCodecs.INT, LevelPayload::level,
                    LevelPayload::new
            );

            @Override
            public @NotNull Type<? extends CustomPacketPayload> type(){
                return TYPE;
            }

            public static void Handle(final LevelPayload payload, final IPayloadContext ctx){
                ctx.enqueueWork(() -> {ClientCache.get().UPDATE.updateLevel(payload);} );
            }

    }
    public record SpPayload(int sp) implements CustomPacketPayload{
        public static final Type<SpPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "sp_update_payload"));

        public static final StreamCodec<ByteBuf, SpPayload> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, SpPayload::sp,
                SpPayload::new
        );

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type(){
            return TYPE;
        }

        public static void Handle(final SpPayload payload, final IPayloadContext ctx){
            ctx.enqueueWork(() -> {ClientCache.get().UPDATE.updateSp(payload);} );
        }
    }
    public record XpPayload(float xp, float gained, boolean triggersOverlay, float limit) implements CustomPacketPayload{
            public static final Type<XpPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "xp_update_payload"));

            public static final StreamCodec<ByteBuf, XpPayload> STREAM_CODEC = StreamCodec.composite(
                    ByteBufCodecs.FLOAT, XpPayload::xp,
                    ByteBufCodecs.FLOAT, XpPayload::gained,
                    ByteBufCodecs.BOOL, XpPayload::triggersOverlay,
                    ByteBufCodecs.FLOAT, XpPayload::limit,
                    XpPayload::new
            );

            @Override
            public @NotNull Type<? extends CustomPacketPayload> type(){
                return TYPE;
            }

            public static void Handle(final XpPayload payload, final IPayloadContext ctx){
                ctx.enqueueWork(() -> {ClientCache.get().UPDATE.updateXp(payload);} );
            }

    }
    public record LevelDataPayload(LevelData data) implements CustomPacketPayload {
        public static final Type<LevelDataPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "level_data_update_payload"));

        public static final StreamCodec<ByteBuf, LevelDataPayload> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT, (payload) -> payload.data.goal(),
                ByteBufCodecs.INT, (payload) -> payload.data.reward(),
                (goal, reward) -> new LevelDataPayload(new LevelData(goal, reward))

        );

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type(){
            return TYPE;
        }

        public static void Handle(final LevelDataPayload payload, final IPayloadContext ctx){
            ctx.enqueueWork(() -> {ClientCache.get().UPDATE.updateLevelData(payload);});
        }
    }
    public record SkillLevelPayload(String id, int level) implements CustomPacketPayload{
        public static final Type<SkillLevelPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "skill_level_sync_payload"));

        public static final StreamCodec<ByteBuf, SkillLevelPayload> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, SkillLevelPayload::id,
                ByteBufCodecs.INT, SkillLevelPayload::level,
                SkillLevelPayload::new
        );

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type(){
            return TYPE;
        }

        public static void Handle(final SkillLevelPayload payload, final IPayloadContext ctx){
            ctx.enqueueWork(() -> {ClientCache.get().UPDATE.updateSkillLevel(payload);} );
        }

    }
    public record BookmarksPayload(String id, boolean state) implements CustomPacketPayload{
        public static final Type<BookmarksPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "skill_bookmark_payload"));

        public static final StreamCodec<ByteBuf, BookmarksPayload> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, BookmarksPayload::id,
                ByteBufCodecs.BOOL, BookmarksPayload::state,
                BookmarksPayload::new
        );

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type(){
            return TYPE;
        }

        public static void Handle(final BookmarksPayload payload, final IPayloadContext ctx){
            ctx.enqueueWork(() -> {ClientCache.get().UPDATE.updateBookmarks(payload);} );
        }

    }

    //UTILS
    public static UpdatePayloads.InitPayload getInitPayload(ServerPlayer player){
        var skillManager = SkillManager.get();
        var data = player.getData(StatsTracker.DATA);

        return new UpdatePayloads.InitPayload(
                new UpdatePayloads.ServerSyncData(skillManager.getAllSkills(),
                        skillManager.getAllBookmarkIDs(player), CategoryLoader.getCategories(),
                        skillManager.getPlayerSkillLevels(player)),

                new UpdatePayloads.PlayerSyncData(LevelManager.getLevel(player),
                        SpManager.getSP(player), XpManager.getXP(player),
                        data.getAllTimeXp(), data.getTotalSpEarned(), data.getTotalSpSpent()),

                new UpdatePayloads.ConfigSyncData(
                        Map.of(Config.REFUND_SYSTEM_KEY, Config.REFUND_SYSTEM.get(),
                                Config.PURCHASE_SYSTEM_KEY, Config.PURCHASE_SYSTEM.get()),

                        Map.of(Config.REFUND_PERCENTAGE_KEY, Config.REFUND_PERCENTAGE.get())),

                XpManager.getLevelData(LevelManager.getLevel(player)));
    }
}


