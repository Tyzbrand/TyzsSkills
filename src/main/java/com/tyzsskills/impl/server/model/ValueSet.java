package com.tyzsskills.impl.server.model;

import com.tyzsskills.api.interfaces.IValueSet;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValueSet implements IValueSet {
    protected List<Float> values;
    protected String unit;

    public ValueSet(List<Float> values, String unit){
        this.values = values != null ? new ArrayList<>(values) : new ArrayList<>();
        this.unit = unit;
    }


    @Override
    public List<Float> getValues(){return Collections.unmodifiableList(values);}
    @Override
    public String getUnit(){return unit;}

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
