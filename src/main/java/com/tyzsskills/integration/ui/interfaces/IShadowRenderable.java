package com.tyzsskills.integration.ui.interfaces;

import com.tyzsskills.integration.ui.abstracts.UIElement;

import java.util.function.Supplier;

public interface IShadowRenderable<T extends UIElement> {
    T withShadow(Supplier<Float> sizeSupplier);

    default T withShadow(float size) {
        return withShadow(() -> size);
    }
}
