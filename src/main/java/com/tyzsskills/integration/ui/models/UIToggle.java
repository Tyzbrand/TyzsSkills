package com.tyzsskills.integration.ui.models;

import com.tyzsskills.impl.client.SoundPlayer;
import com.tyzsskills.integration.ui.UIElement;
import com.tyzsskills.integration.ui.records.UIStyles;
import net.minecraft.client.gui.GuiGraphics;

import java.util.function.Supplier;

public class UIToggle extends UIElement {

    private final Supplier<UIStyles.ToggleStyle> style;
    private final Supplier<Boolean> state;
    private final Supplier<Boolean> onClick;

    public UIToggle(int offsetX, int offsetY, int width, int height, Supplier<Boolean> onClick, Supplier<Boolean> state, Supplier<UIStyles.ToggleStyle> style){
        super(offsetX, offsetY, width, height);

        this.onClick = onClick;
        this.state = state;
        this.style = style;
    }

    @Override
    protected void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        int finalU;
        int finalV;

        var currentStyle = style.get();
        var currentState = state.get();
        var isHovering = isHovering(mouseX, mouseY);

        if(currentState){
            finalU = isHovering ? currentStyle.uTrueHover() : currentStyle.uTrue();
            finalV = isHovering ? currentStyle.vTrueHover() : currentStyle.vTrue();
        }
        else{
            finalU = isHovering ? currentStyle.uFalseHover() : currentStyle.uFalse();
            finalV = isHovering ? currentStyle.vFalseHover() : currentStyle.vFalse();
        }

        gui.blit(currentStyle.image().texture(), getRawX(), getRawY(), finalU, finalV, getRawWidth(), getRawHeight(), currentStyle.image().size(), currentStyle.image().size());
    }

    @Override
    protected boolean onClick(int mouseX, int mouseY) {
        if(isHovering(mouseX, mouseY) && onClick != null){
            if(onClick.get()) SoundPlayer.PlayUIClick();
            return true;
        }
        return false;
    }
}
