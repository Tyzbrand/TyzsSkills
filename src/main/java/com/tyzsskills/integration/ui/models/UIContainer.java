package com.tyzsskills.integration.ui.models;

import com.tyzsskills.integration.ui.UIElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

public class UIContainer extends UIElement {
    protected final List<UIElement> children = new ArrayList<>();

    public UIContainer(int offsetX, int offsetY) {
        super(offsetX, offsetY, 0, 0);
    }

    public UIContainer(int offsetX, int offsetY, int width, int height) {
        super(offsetX, offsetY, width, height);
    }


    //Fluents
    public UIContainer addChild(UIElement child) {
        children.add(child);
        return this;
    }

    //Overrides
    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        for(var child : children){
            child.updatePosition(getRawX(), getRawY());
            child.draw(gui, mouseX, mouseY, partialTick, true, isCurrentShaded());
        }
    }

    @Override
    protected boolean onClick(int mouseX, int mouseY) {
        for(int i = children.size() - 1; i >= 0; i--){
            var child = children.get(i);
            if(child.handleClick(mouseX, mouseY, true)) return true;
        }
        return false;
    }

    @Override
    protected boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
        for (int i = children.size() - 1; i >= 0; i--) {
            var child = children.get(i);
            if (child.handleKeyPressed(keyCode, scanCode, modifiers, true)) return true;
        }
        return false;
    }

    @Override
    protected boolean onCharTyped(char codePoint, int modifiers) {
        for (int i = children.size() - 1; i >= 0; i--) {
            var child = children.get(i);
            if (child.handleCharTyped(codePoint, modifiers, true)) return true;
        }
        return false;
    }

    @Override
    protected boolean onScroll(double mouseX, double mouseY, double deltaY) {
        for (int i = children.size() - 1; i >= 0; i--) {
            var child = children.get(i);
            if (child.handleScroll(mouseX, mouseY, deltaY, true)) return true;
        }
        return false;
    }

    @Override
    protected boolean onDrag(double mouseX, double mouseY, int button, double dragX, double dragY) {
        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).handleDrag(mouseX, mouseY, button, dragX, dragY, true)) return true;
        }
        return false;
    }

    @Override
    protected boolean onRelease(double mouseX, double mouseY, int button) {
        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).handleRelease(mouseX, mouseY, button, true)) return true;
        }
        return false;
    }

    public UITooltip resolveTooltip(int mouseX, int mouseY) {
        if (!isVisible() || !isHovering(mouseX, mouseY)) return null;
        for (int i = children.size() - 1; i >= 0; i--) {
            var child = children.get(i);
            if (child instanceof UIContainer containerChild) {
                var tooltip = containerChild.resolveTooltip(mouseX, mouseY);
                if (tooltip != null) return tooltip;
            } else {
                var tooltip = child.getHoveredTooltip(mouseX, mouseY);
                if (tooltip != null) return tooltip;
            }
        }
        return this.getHoveredTooltip(mouseX, mouseY);
    }

    @Override
    public boolean isHovering(int mouseX, int mouseY) {
        if (super.getRawWidth() > 0 && super.getRawHeight() > 0 && super.isHovering(mouseX, mouseY)) return true;
        for (var child : children) {
            if (child.isVisible() && child.isHovering(mouseX, mouseY)) return true;
        }
        return false;
    }

    @Override
    public void updatePosition(int parentX, int parentY) {
        super.updatePosition(parentX, parentY);
        for (var child : children) {
            child.updatePosition(getRawX(), getRawY());
        }
    }

    @Override
    public int getRawWidth() {
        if (super.getRawWidth() > 0) return super.getRawWidth();
        updatePosition(getRawX() - getOffsetX(), getRawY() - getOffsetY());

        var minX = Float.MAX_VALUE;
        var maxX = -Float.MAX_VALUE;
        var hasVisibleChild = false;

        for (var child : children) {
            if (!child.isVisible() || child.getVisualWidth() <= 0 || child.getVisualHeight() <= 0) continue;
            hasVisibleChild = true;
            var cx = child.getVisualX();
            var cw = child.getVisualWidth();
            minX = Math.min(minX, cx);
            maxX = Math.max(maxX, cx + cw);
        }
        return hasVisibleChild ? Math.round(maxX - minX) : 0;
    }

    @Override
    public int getRawHeight() {
        if (super.getRawHeight() > 0) return super.getRawHeight();
        updatePosition(getRawX() - getOffsetX(), getRawY() - getOffsetY());

        var minY = Float.MAX_VALUE;
        var maxY = -Float.MAX_VALUE;
        var hasVisibleChild = false;

        for (var child : children) {
            if (!child.isVisible() || child.getVisualWidth() <= 0 || child.getVisualHeight() <= 0) continue;
            hasVisibleChild = true;
            var cy = child.getVisualY();
            var ch = child.getVisualHeight();
            minY = Math.min(minY, cy);
            maxY = Math.max(maxY, cy + ch);
        }
        return hasVisibleChild ? Math.round(maxY - minY) : 0;
    }

    @Override
    public float getVisualX() {
        if (super.getRawWidth() > 0) return super.getVisualX();
        updatePosition(getRawX() - getOffsetX(), getRawY() - getOffsetY());

        var minX = Float.MAX_VALUE;
        var hasVisibleChild = false;
        for (var child : children) {
            if (!child.isVisible() || child.getVisualWidth() <= 0 || child.getVisualHeight() <= 0) continue;
            hasVisibleChild = true;
            minX = Math.min(minX, child.getVisualX());
        }
        return hasVisibleChild ? minX : super.getVisualX();
    }

    @Override
    public float getVisualY() {
        if (super.getRawHeight() > 0) return super.getVisualY();
        updatePosition(getRawX() - getOffsetX(), getRawY() - getOffsetY());

        var minY = Float.MAX_VALUE;
        var hasVisibleChild = false;
        for (var child : children) {
            if (!child.isVisible() || child.getVisualWidth() <= 0 || child.getVisualHeight() <= 0) continue;
            hasVisibleChild = true;
            minY = Math.min(minY, child.getVisualY());
        }
        return hasVisibleChild ? minY : super.getVisualY();
    }

    public void clear(){
        children.clear();
    }

}
