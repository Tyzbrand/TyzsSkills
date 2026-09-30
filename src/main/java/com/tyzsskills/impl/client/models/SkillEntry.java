package com.tyzsskills.impl.client.models;

import com.tyzsskills.impl.client.screen.SkillCardLegacy;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SkillEntry extends CustomScrollView.Entry{
    private final List<SkillCardLegacy> widgets = new ArrayList<>();
    public static final int SPACING = 2;
    private static final int PADDING_LEFT = 0;

    public void addWidget(SkillCardLegacy widget){
        this.widgets.add(widget);
    }

    public SkillCardLegacy getHoveredWidget(double mouseX, double mouseY) {
        for (SkillCardLegacy widget : widgets) {
            if (widget.isMouseOver((int)mouseX, (int)mouseY)) {
                return widget;
            }
        }
        return null;
    }

    @Override
    public void render(GuiGraphics gui, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isHovered, float partialTick) {
        int currentX = left + PADDING_LEFT;

        for(SkillCardLegacy widget : widgets){
            widget.render(gui, currentX, top, mouseX, mouseY, partialTick);
            currentX += SkillCardLegacy.WIDTH + SPACING;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button){
        for(SkillCardLegacy widget : widgets){
            if(widget.mouseClicked(mouseX, mouseY, button)){
                return true;
            }
        }
        return  false;
    }

    @Override
    public @NotNull Component getNarration() {
        return Component.empty();
    }

}
