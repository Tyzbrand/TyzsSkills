package com.tyzsskills.api.interfaces;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.List;

/**
 * ReadOnly interface used to access modifier information
 */
public interface IModifier {
    String getAttribute();
    AttributeModifier.Operation getOperation();
    List<Float> getValues();
    String getUnit();


}
