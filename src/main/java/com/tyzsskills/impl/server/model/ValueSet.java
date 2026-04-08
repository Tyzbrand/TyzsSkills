package com.tyzsskills.impl.server.model;

import com.tyzsskills.api.interfaces.IValueSet;

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
}
