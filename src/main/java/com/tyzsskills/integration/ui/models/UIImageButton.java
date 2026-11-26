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
        var src = getShadowSource();
        if(src == null) return;
        gui.blit(src.texture(), getRawX(), getRawY(), src.u(), src.v(), src.w(), src.h(), src.texW(), src.texH());
    }

    @Override
    public ShadowSource getShadowSource() {
        var s = style.get();
        if(s == null) return null;

        int finalU, finalV;

        if(disabledWhen.get()) {finalU = s.uDisabled(); finalV = s.vDisabled();}
        else if(isHovered()) {finalU = s.uHover(); finalV = s.vHover();}
        else {finalU = s.u(); finalV = s.v();}

        return new ShadowSource(s.image().texture(), finalU, finalV, getRawWidth(), getRawHeight(), s.image().width(), s.image().height());
    }
}
