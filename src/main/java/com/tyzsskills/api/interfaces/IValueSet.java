package com.tyzsskills.api.interfaces;

import java.util.List;

/**
 * ReadOnly interface used to access value information
 */
public interface IValueSet {
    List<Float> getValues();
    String getUnit();



}
