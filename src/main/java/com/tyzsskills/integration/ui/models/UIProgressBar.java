package com.tyzsskills.integration.ui.models;

import com.tyzsskills.integration.ui.abstracts.UIElement;
import com.tyzsskills.integration.ui.records.UIStyles;
import net.minecraft.client.gui.GuiGraphics;

import java.util.function.Supplier;

public class UIProgressBar extends UIElement {

    private final Supplier<UIStyles.ImageStyle> style;
    private Supplier<Float> value = () -> 0f;

    public UIProgressBar(int offsetX, int offsetY, Supplier<UIStyles.ImageStyle> style, Supplier<Float> value){
        super(offsetX, offsetY, style.get().width(), style.get().height());

        this.style = style;
        this.value = value;
    }

    @Override
    protected void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        var ratio = Math.clamp(value.get(), 0f, 1f);
        if(ratio <= 0f) return;

        var currentStyle = style.get();

        var widthToDraw = (int)(ratio * currentStyle.width());
        if(widthToDraw > 0f) {
            gui.blit(currentStyle.image().texture(), getRawX(), getRawY(), currentStyle.u(), currentStyle.v(),
                    widthToDraw, currentStyle.height(), currentStyle.image().width(), currentStyle.image().height());
        }
    }
}
