package com.tyzsskills.api.ui;

import net.minecraft.client.gui.GuiGraphics;
import oshi.util.tuples.Pair;

import java.util.function.Supplier;

public class UIBackground extends UIElement{
    private final int backgroundColor;

    private Supplier<Pair<Boolean, Integer>> drawBorder = () -> new Pair<>(false, 0);

    //Fluent
    public UIBackground withBorder(Supplier<Pair<Boolean, Integer>> border){this.drawBorder = border; return this;}

    public UIBackground(int offsetX, int offsetY, int width, int height, int backgroundColor) {
        super(offsetX, offsetY, width, height);
        this.backgroundColor = backgroundColor;
    }


    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        gui.fill(x, y + 1, x + width, y + height - 1, backgroundColor);
        gui.fill(x + 1, y, x + width - 1, y + 1, backgroundColor);
        gui.fill(x + 1, y + height - 1, x + width - 1, y + height, backgroundColor);


        var border = drawBorder.get();
        if(!border.getA()) return;
        var borderColor = drawBorder.get().getB();

        gui.fill(x + 1, y, x + width - 1, y + 1, borderColor);
        gui.fill(x + 1, y + height - 1, x + width - 1, y + height, borderColor);
        gui.fill(x, y + 1, x + 1, y + height - 1, borderColor);
        gui.fill(x + width - 1, y + 1, x + width, y + height - 1, borderColor);
    }
}
