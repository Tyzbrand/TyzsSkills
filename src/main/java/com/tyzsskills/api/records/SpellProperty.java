package com.tyzsskills.api.records;

import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;

public record SpellProperty(String key, List<Integer> prices, float baseValue, List<Float> values, String displayName, String description, String unit) {

    public SpellProperty{
        prices = prices == null ? List.of() : List.copyOf(prices);
        values = values == null ? List.of() : List.copyOf(values);
    }

    public float getValue(int lvl){
        if(lvl <= 0 || values.isEmpty()) return baseValue;

        var index = lvl - 1;
        if(index >= values.size()) return values.getLast();

        return values.get(index);
    }

    public int getPrice(int lvl){
        if(lvl <= 0 || prices.isEmpty()) return Integer.MAX_VALUE;

        var index = lvl - 1;
        if(index >= prices.size()) return prices.getLast();

        return prices.get(index);
    }

    public int getMaximumLevel(){return Math.min(values.size(), prices.size());}

    //Network
    public void writeToBuffer(FriendlyByteBuf buffer){
        buffer.writeUtf(key);
        buffer.writeFloat(baseValue);

        buffer.writeCollection(prices, FriendlyByteBuf::writeInt);
        buffer.writeCollection(values, FriendlyByteBuf::writeFloat);

        buffer.writeUtf(displayName);
        buffer.writeUtf(description);
        buffer.writeUtf(unit);
    }

    public static SpellProperty readFromBuffer(FriendlyByteBuf buffer){
        var readKey = buffer.readUtf();
        var readBaseValue = buffer.readFloat();

        var readPrices = buffer.readCollection(ArrayList::new, FriendlyByteBuf::readInt);
        var readValues = buffer.readCollection(ArrayList::new, FriendlyByteBuf::readFloat);

        var readDisplayName = buffer.readUtf();
        var readDescription = buffer.readUtf();
        var readUnit = buffer.readUtf();

        return new SpellProperty(readKey, readPrices, readBaseValue, readValues, readDisplayName, readDescription, readUnit);
    }
}
