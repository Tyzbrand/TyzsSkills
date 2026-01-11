package com.tyzsskills.client.models;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SkillEntry extends CustomScrollView.Entry{
    private final List<SkillWidget> widgets = new ArrayList<>();
    private final int spacing = 2;

    public SkillEntry(){

    }

    public void addWidget(SkillWidget widget){
        this.widgets.add(widget);
    }

    @Override
    public void render(GuiGraphics gui, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isHovered, float partialTick) {
        int currentX = left;

        for(SkillWidget widget : widgets){
            widget.render(gui, currentX, top, mouseX, mouseY, partialTick);

            currentX+= SkillWidget.WIDTH + spacing;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button){
        for(SkillWidget widget : widgets){
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
