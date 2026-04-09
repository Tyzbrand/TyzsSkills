package com.tyzsskills.api.interfaces;

import java.util.List;

/**
 * ReadOnly interface used to access value information
 */
public interface IValueSet {
    List<Float> getValues();
    String getUnit();

    default float getValue(int lvl){
        var values = this.getValues();
        if(values == null || values.isEmpty()) return 0f;

        var index = Math.max(0, lvl - 1);
        if(index >= values.size()) return values.getLast();

        return values.get(index);
    }

}
