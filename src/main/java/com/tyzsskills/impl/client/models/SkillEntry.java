package com.tyzsskills.impl.client.models;

import com.tyzsskills.impl.client.screen.SkillCard;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SkillEntry extends CustomScrollView.Entry{
    private final List<SkillCard> widgets = new ArrayList<>();
    private final int spacing = 2;

    public SkillEntry(){

    }

    public void addWidget(SkillCard widget){
        this.widgets.add(widget);
    }

    public SkillCard getHoveredWidget(double mouseX, double mouseY) {
        for (SkillCard widget : widgets) {
            if (widget.isMouseOver((int)mouseX, (int)mouseY)) {
                return widget;
            }
        }
        return null;
    }

    @Override
    public void render(GuiGraphics gui, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isHovered, float partialTick) {
        int currentX = left;

        for(SkillCard widget : widgets){
            widget.render(gui, currentX, top, mouseX, mouseY, partialTick);

            currentX+= SkillCard.WIDTH + spacing;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button){
        for(SkillCard widget : widgets){
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
