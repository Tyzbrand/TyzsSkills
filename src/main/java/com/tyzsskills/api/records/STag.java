package com.tyzsskills.api.records;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record STag(@NotNull String id, @NotNull String displayName, int color) {

    public boolean is(@NotNull String tagId){return id.equalsIgnoreCase(tagId);}

    public static final StreamCodec<ByteBuf, STag> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, STag::id,
            ByteBufCodecs.STRING_UTF8, STag::displayName,
            ByteBufCodecs.INT, STag::color,
            STag::new
    );
}
