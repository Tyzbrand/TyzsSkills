package com.tyzsskills.impl.server.model;

import com.tyzsskills.api.interfaces.IModifier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Modifier implements IModifier {
    protected String attribute;
    protected AttributeModifier.Operation operation;
    protected List<Float> values;
    protected String unit;

    public Modifier(String attribute, AttributeModifier.Operation operation, List<Float> values, String unit){
        this.attribute = attribute;
        this.operation = operation;
        this.values = values != null ? new ArrayList<>(values) : new ArrayList<>();
        this.unit = unit;
    }

    @Override
    public String getAttribute(){return attribute;}
    @Override
    public AttributeModifier.Operation getOperation(){return operation;}
    @Override
    public List<Float> getValues(){return Collections.unmodifiableList(values);}
    @Override
    public String getUnit(){return unit;}

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



