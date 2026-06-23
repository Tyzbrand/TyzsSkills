package com.tyzsskills.api.records;

import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;

public record SpellProperty(String key, List<Integer> prices, List<Float> values, String displayName, String description) {

    public SpellProperty{
        prices = prices == null ? List.of() : List.copyOf(prices);
        values = values == null ? List.of() : List.copyOf(values);
    }

    public float getValue(int lvl){
        if(lvl <= 0) return 0f;
        if(values.isEmpty()) return 0f;

        var index = Math.max(0, lvl - 1);
        if(index >= values.size()) return values.getLast();

        return values.get(index);
    }

    public int getPrice(int lvl){
        if(lvl <= 0) return 0;
        if(prices.isEmpty()) return 0;

        var index = Math.max(0, lvl - 1);
        if(index >= prices.size()) return prices.getLast();

        return prices.get(index);
    }

    public int getMaximumLevel(){return Math.min(values.size(), prices.size());}

    //Network
    public void writeToBuffer(FriendlyByteBuf buffer){
        buffer.writeUtf(key);

        buffer.writeCollection(prices, FriendlyByteBuf::writeInt);
        buffer.writeCollection(values, FriendlyByteBuf::writeFloat);

        buffer.writeUtf(displayName);
        buffer.writeUtf(description);
    }

    public static SpellProperty readFromBuffer(FriendlyByteBuf buffer){
        var readKey = buffer.readUtf();

        var readPrices = buffer.readCollection(ArrayList::new, FriendlyByteBuf::readInt);
        var readValues = buffer.readCollection(ArrayList::new, FriendlyByteBuf::readFloat);

        var readDisplayName = buffer.readUtf();
        var readDescription = buffer.readUtf();

        return new SpellProperty(readKey, readPrices, readValues, readDisplayName, readDescription);
    }
}
