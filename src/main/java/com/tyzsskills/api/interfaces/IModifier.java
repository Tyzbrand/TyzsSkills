package com.tyzsskills.api.interfaces;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.Collections;
import java.util.List;

/**
 * ReadOnly interface used to access modifier information
 */
public interface IModifier {
    String getAttribute();
    AttributeModifier.Operation getOperation();
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
