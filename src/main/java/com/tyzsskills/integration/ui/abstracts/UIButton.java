package com.tyzsskills.integration.ui.abstracts;

import com.tyzsskills.impl.client.SoundPlayer;

import java.util.function.Supplier;

public abstract class UIButton extends UIElement{
    protected final Supplier<Boolean> onClick;
    protected Supplier<Boolean> disabledWhen = () -> false;

    public UIButton(int offsetX, int offsetY, int width, int height, Supplier<Boolean> onClick) {
        super(offsetX, offsetY, width, height);

        this.onClick = onClick;
    }

    //Fluents
    public UIElement withDisabled(Supplier<Boolean> supplier){this.disabledWhen = supplier; return this;}

    @Override
    protected boolean onClick(int mouseX, int mouseY) {
        if(disabledWhen.get()) return false;
        if(isHovering(mouseX, mouseY) && onClick != null){
            if(onClick.get()) SoundPlayer.PlayUIClick();
            return true;
        }
        return false;
    }
}
