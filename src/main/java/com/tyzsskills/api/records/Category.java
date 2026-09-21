package com.tyzsskills.api.records;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record Category(@NotNull String id, @NotNull String displayName, @NotNull String icon, int order) {
    public static final StreamCodec<ByteBuf, Category> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, Category::id,
            ByteBufCodecs.STRING_UTF8, Category::displayName,
            ByteBufCodecs.STRING_UTF8, Category::icon,
            ByteBufCodecs.INT, Category::order,
            Category::new
    );
}
