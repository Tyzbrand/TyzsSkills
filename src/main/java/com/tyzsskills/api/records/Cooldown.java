package com.tyzsskills.api.records;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record Cooldown(long endTick, long duration) {
    public static final Cooldown EMPTY = new Cooldown(0L, 0L);

    public boolean isCooldownOver(long currentTick){return endTick <= currentTick;}

    public String getFormatedTimer(long gameTime){
        var ticksLeft = Math.max(0L, endTick - gameTime);
        var timeLeft = (ticksLeft + 19L) / 20L;
        return String.format("%02d:%02d", timeLeft / 60, timeLeft % 60);
    }

    //region Network
    public static final StreamCodec<ByteBuf, Cooldown> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG, Cooldown::endTick,
            ByteBufCodecs.VAR_LONG, Cooldown::duration,
            Cooldown::new
    );
    //endregion

    //region NBT
    public CompoundTag toNBT() {
        var tag = new CompoundTag();
        tag.putLong("end_tick", endTick);
        tag.putLong("duration", duration);
        return tag;
    }
    public static Cooldown fromNBT(CompoundTag tag) {
        return new Cooldown(tag.getLong("end_tick"), tag.getLong("duration"));
    }
    //endregion
}
