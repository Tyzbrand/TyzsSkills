package com.tyzsskills.api.records;

import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;

public record ValueSet (List<Float> values, String unit) {
    public ValueSet{
        values = values != null ? List.copyOf(values) : List.of();
    }

    public float getValue(int lvl){
        if(lvl <= 0) return 0f;
        if(values.isEmpty()) return 0f;

        var index = Math.max(0, lvl - 1);
        if(index >= values.size()) return values.getLast();

        return values.get(index);
    }

    //Network
    public void writeToBuffer(FriendlyByteBuf buffer){
        buffer.writeCollection(values, FriendlyByteBuf::writeFloat);
        buffer.writeUtf(unit);
    }

    public static ValueSet readFromBuffer(FriendlyByteBuf buffer){
        var readValues = buffer.readCollection(ArrayList::new, FriendlyByteBuf::readFloat);
        var readUnit = buffer.readUtf();

        return new ValueSet(readValues, readUnit);
    }
}
