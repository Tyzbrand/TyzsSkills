package com.tyzsskills.api.records;

import com.tyzsskills.impl.server.payloads.UpdatePayloads;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record LevelData(float goal, int reward) {
    public static final StreamCodec<ByteBuf, LevelData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, LevelData::goal,
            ByteBufCodecs.INT, LevelData::reward,
            LevelData::new
    );
}
