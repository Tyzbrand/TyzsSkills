package com.tyzsskills.api.ui;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.ArrayList;
import java.util.List;

public class UIContainer extends UIElement{
    private final List<UIElement> children = new ArrayList<>();

    public UIContainer(int offsetX, int offsetY, int width, int height) {super(offsetX, offsetY, width, height);}

    //Fluents
    public UIElement addChild(UIElement child){children.add(child); return this;}

    //Overrides
    @Override
    protected void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        for(var child : children){
            child.updatePosition(x, y);
            child.draw(gui, mouseX, mouseY, partialTick, true, isCurrentShaded());
        }
    }

    @Override
    protected boolean onClick(int mouseX, int mouseY) {
        for(var child : children){
            if(child.handleClick(mouseX, mouseY, true)) return true;
        }
        return false;
    }

    @Override
    protected List<Either<FormattedText, TooltipComponent>> resolveTooltips(int mouseX, int mouseY) {
        for (var child : children) {
            var childTooltip = child.getTooltips(mouseX, mouseY);
            if (!childTooltip.isEmpty()) return childTooltip;
        }
        return super.resolveTooltips(mouseX, mouseY);
    }
}
