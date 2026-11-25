package com.tyzsskills.integration.ui.models;

import com.tyzsskills.integration.ui.abstracts.UIButton;
import com.tyzsskills.integration.ui.interfaces.IShadowRenderable;
import com.tyzsskills.integration.ui.records.UIStyles.ButtonStyle;
import net.minecraft.client.gui.GuiGraphics;

import java.util.function.Supplier;

public class UIImageButton extends UIButton implements IShadowRenderable<UIImageButton> {

    private final Supplier<ButtonStyle> style;

    @Override
    public UIImageButton withShadow(Supplier<Float> sizeSupplier) {
        this.shadowSize = sizeSupplier != null ? sizeSupplier : () -> 0f;
        return this;
    }

    public UIImageButton(int offsetX, int offsetY, Supplier<ButtonStyle> style, Supplier<Boolean> onClick) {
        super(offsetX, offsetY, style.get().width(), style.get().height(), onClick);

        this.style = style;
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        int finalU;
        int finalV;
        var currentStyle = style.get();

        if(disabledWhen.get()) {finalU = currentStyle.uDisabled(); finalV = currentStyle.vDisabled();}
        else if(isHovered()) {finalU =  currentStyle.uHover(); finalV = currentStyle.vHover();}
        else {finalU = currentStyle.u(); finalV = currentStyle.v();}

        gui.blit(currentStyle.image().texture(), getRawX(), getRawY(), finalU, finalV, getRawWidth(), getRawHeight(), currentStyle.image().size(), currentStyle.image().size());
    }
}
