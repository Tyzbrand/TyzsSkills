package com.tyzsskills.api.records;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record SkillTag(@NotNull String id, @NotNull String displayName, int color) {

    public boolean is(@NotNull String tagId){return id.equalsIgnoreCase(tagId);}

    public static final StreamCodec<ByteBuf, SkillTag> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SkillTag::id,
            ByteBufCodecs.STRING_UTF8, SkillTag::displayName,
            ByteBufCodecs.INT, SkillTag::color,
            SkillTag::new
    );
}
