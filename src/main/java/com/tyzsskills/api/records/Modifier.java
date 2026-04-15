package com.tyzsskills.api.records;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.ArrayList;
import java.util.List;

public record Modifier (String attribute, AttributeModifier.Operation operation, List<Float> values, String unit){
    public Modifier{
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
        buffer.writeUtf(attribute);
        buffer.writeEnum(operation);
        buffer.writeCollection(values, FriendlyByteBuf::writeFloat);
        buffer.writeUtf(unit);
    }

    public static Modifier readFromBuffer(FriendlyByteBuf buffer){
        var readAttribute = buffer.readUtf();
        var readOperation = buffer.readEnum(AttributeModifier.Operation.class);
        var readValues = buffer.readCollection(ArrayList::new, FriendlyByteBuf::readFloat);
        var readUnit = buffer.readUtf();

        return new Modifier(readAttribute, readOperation, readValues, readUnit);
    }
}



