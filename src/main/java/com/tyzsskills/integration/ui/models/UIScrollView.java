package com.tyzsskills.integration.ui.models;

import com.tyzsskills.integration.ui.UIElement;
import com.tyzsskills.integration.ui.records.UIStyles;
import net.minecraft.client.gui.GuiGraphics;

import java.util.function.Supplier;

public class UIScrollView extends UIContainer{
    private int contentHeight;
    private double scrollAmount;
    private boolean isDragging = false;
    private int dragGrabOffsetY = 0;

    private final boolean showScrollBar;
    private final int scrollBarWidth = 4;

    public UIScrollView(int offsetX, int offsetY, int width, int height){
        this(offsetX, offsetY, width, height, true);
    }
    public UIScrollView(int offsetX, int offsetY, int width, int height, boolean showScrollBar){
        super(offsetX, offsetY, width, height);

        this.showScrollBar = showScrollBar;
    }

    //PARENTS
    @Override
    protected void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        gui.enableScissor(this.x, this.y, this.x + this.width, this.y + this.height);

        var hovered = isHovering(mouseX, mouseY);

        var hasBar = showScrollBar && getMaxScroll() > 0;
        var trackX = this.x + this.width - scrollBarWidth;
        var onScrollBar = hasBar && mouseX >= trackX && mouseX < this.x + this.width;

        var childMouseX = (hovered && !onScrollBar) ? mouseX : -1;
        var childMouseY = (hovered && !onScrollBar) ? mouseY : -1;

        var scrolledY = (int) (this.y - this.scrollAmount);
        for (var child : children) {
            child.updatePosition(this.x, scrolledY);

            if (child.y + child.height <= this.y || child.y >= this.y + this.height) continue; //Checks if the child is visible (culling)

            child.draw(gui, childMouseX, childMouseY, partialTick, true, isCurrentShaded());
        }

        gui.disableScissor();

        if (showScrollBar && getMaxScroll() > 0) {
            renderScrollBar(gui);
        }
    }
    @Override
    public UIElement addChild(UIElement child) {
        super.addChild(child);
        recalculateContentHeight();
        return this;
    }
    @Override
    public boolean isHovering(int mouseX, int mouseY) {
        return mouseX >= this.x && mouseX < this.x + this.width &&
                mouseY >= this.y && mouseY < this.y + this.height;
    }
    @Override
    protected boolean onClick(int mouseX, int mouseY) {
        if (!isHovering(mouseX, mouseY)) return false;

        var trackX = x + width - scrollBarWidth;
        var trackY = y;

        if (showScrollBar && getMaxScroll() > 0 && mouseX >= trackX && mouseX < trackX + scrollBarWidth) {
            var thumbHeight = getThumbHeight();
            var availableTrack = height - thumbHeight;
            var currentThumbY = trackY + (int)((scrollAmount / getMaxScroll()) * availableTrack);

            if (mouseY >= currentThumbY && mouseY < currentThumbY + thumbHeight) {
                isDragging = true;
                dragGrabOffsetY = mouseY - currentThumbY;
            } else {
                isDragging = true;
                dragGrabOffsetY = thumbHeight / 2;
                updateScrollFromMouse(mouseY, trackY, availableTrack);
            }
            return true;
        }

        return super.onClick(mouseX, mouseY);
    }
    @Override
    protected boolean onScroll(double mouseX, double mouseY, double deltaY) {
        if (!isHovering((int) mouseX, (int) mouseY)) return false;
        if (super.onScroll(mouseX, mouseY, deltaY)) return true;

        setScrollAmount(scrollAmount - (deltaY * 20d));
        return true;
    }
    @Override
    protected boolean onDrag(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0 && isDragging) {
            var trackY = y;
            var availableTrack = height - getThumbHeight();
            updateScrollFromMouse((int)mouseY, trackY, availableTrack);
            return true;
        }
        return super.onDrag(mouseX, mouseY, button, dragX, dragY);
    }
    @Override
    protected boolean onRelease(double mouseX, double mouseY, int button) {
        if (button == 0 && isDragging) {isDragging = false;return true;}
        return super.onRelease(mouseX, mouseY, button);
    }
    @Override
    public void clear() {
        super.clear();
        contentHeight = 0;
        setScrollAmount(0d);
    }

    //IMPLEMENTATION
    private void renderScrollBar(GuiGraphics gui) {
        var trackX = x + width - scrollBarWidth;
        var trackY = y;

        gui.fill(trackX, trackY, trackX + scrollBarWidth, trackY + height, 0x40000000);

        var thumbHeight = getThumbHeight();
        var availableTrack = height - thumbHeight;
        var scrollRatio = (float)(scrollAmount / getMaxScroll());
        var thumbY = trackY + (int)(scrollRatio * availableTrack);

        gui.fill(trackX, thumbY, trackX + scrollBarWidth, thumbY + thumbHeight, 0x80FFFFFF);
    }
    public void recalculateContentHeight() {
        var maxBottom = 0;
        for (var child : children) {
            int bottom = child.offsetY + child.height;
            if (bottom > maxBottom) {
                maxBottom = bottom;
            }
        }
        contentHeight = maxBottom;
        setScrollAmount(scrollAmount);
    }
    private void updateScrollFromMouse(int mouseY, int trackY, int availableTrack) {
        if (availableTrack <= 0) return;

        var targetThumbY = mouseY - dragGrabOffsetY;
        var relativeY = (float)(targetThumbY - trackY);
        var ratio = Math.clamp(relativeY / availableTrack, 0f, 1f);
        setScrollAmount(ratio * getMaxScroll());
    }

    //GETTERS
    public int getMaxScroll(){return Math.max(0, contentHeight - height);}
    public double getScrollAmount() {return scrollAmount;}
    private int getThumbHeight() {
        var visibleRatio = (float)this.height / this.contentHeight;
        return Math.max(12, (int)(this.height * visibleRatio));
    }

    //SETTERS
    public void setScrollAmount(double amount){scrollAmount = Math.clamp(amount, 0, getMaxScroll());}
}
