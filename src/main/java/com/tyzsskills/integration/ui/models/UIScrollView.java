package com.tyzsskills.integration.ui.models;

import com.tyzsskills.integration.ui.UIElement;
import net.minecraft.client.gui.GuiGraphics;

public class UIScrollView extends UIContainer{
    private int contentHeight;
    private double scrollAmount;
    private boolean isDragging = false;
    private int dragGrabOffsetY = 0;

    private final boolean showScrollBar;
    private final int scrollBarWidth = 4;

    public UIScrollView(int offsetX, int offsetY, int width, int height) {
        this(offsetX, offsetY, width, height, true);
    }

    public UIScrollView(int offsetX, int offsetY, int width, int height, boolean showScrollBar) {
        super(offsetX, offsetY, width, height);
        this.showScrollBar = showScrollBar;
    }

    // PARENTS
    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        recalculateContentHeight();

        var viewX = Math.round(this.getVisualX());
        var viewY = Math.round(this.getVisualY());
        var viewW = Math.round(this.getVisualWidth());
        var viewH = Math.round(this.getVisualHeight());

        gui.enableScissor(viewX, viewY, viewX + viewW, viewY + viewH);

        var hovered = isHovering(mouseX, mouseY);
        var onScrollBar = isMouseOnScrollBar(mouseX, mouseY);
        var childMouseX = (hovered && !onScrollBar) ? mouseX : -1;
        var childMouseY = (hovered && !onScrollBar) ? mouseY : -1;
        var scrolledY = getScrolledY();

        for (var child : children) {
            child.updatePosition(this.getRawX(), scrolledY);

            if (child.getVisualY() + child.getVisualHeight() <= this.getVisualY()
                    || child.getVisualY() >= this.getVisualY() + this.getVisualHeight()) {
                continue;
            }
            child.draw(gui, childMouseX, childMouseY, partialTick, true, isCurrentShaded());
        }
        gui.disableScissor();
        if (showScrollBar && getMaxScroll() > 0) renderScrollBar(gui);

    }

    @Override
    public void updatePosition(int parentX, int parentY) {
        super.updatePosition(parentX, parentY);
        var scrolledY = getScrolledY();
        for (var child : children) child.updatePosition(this.getRawX(), scrolledY);
    }

    @Override
    public UIContainer addChild(UIElement child) {
        super.addChild(child);
        recalculateContentHeight();
        return this;
    }

    @Override
    public boolean checkHover(int mouseX, int mouseY) {
        var visualWidth = getVisualWidth();
        var visualHeight = getVisualHeight();
        if (visualWidth <= 0 || visualHeight <= 0) return false;

        var visualX = getVisualX();
        var visualY = getVisualY();
        return mouseX >= visualX && mouseX < visualX + visualWidth
                && mouseY >= visualY && mouseY < visualY + visualHeight;
    }

    @Override
    public UITooltip resolveTooltip(int mouseX, int mouseY) {
        if (isMouseOnScrollBar(mouseX, mouseY)) return null;
        return super.resolveTooltip(mouseX, mouseY);
    }

    @Override
    protected boolean onClick(int mouseX, int mouseY) {
        if (!isHovering(mouseX, mouseY)) return false;

        var trackY = getRawY();

        if (isMouseOnScrollBar(mouseX, mouseY)) {
            var thumbHeight = getThumbHeight();
            var availableTrack = getRawHeight() - thumbHeight;
            var currentThumbY = trackY + (int) ((scrollAmount / getMaxScroll()) * availableTrack);

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
            var trackY = getRawY();
            var availableTrack = getRawHeight() - getThumbHeight();
            updateScrollFromMouse((int) mouseY, trackY, availableTrack);
            return true;
        }
        return super.onDrag(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    protected boolean onRelease(double mouseX, double mouseY, int button) {
        if (button == 0 && isDragging) {
            isDragging = false;
            return true;
        }
        return super.onRelease(mouseX, mouseY, button);
    }

    @Override
    public void clear() {
        super.clear();
        contentHeight = 0;
        setScrollAmount(0d);
    }

    // IMPLEMENTATION
    private int getScrolledY() {
        return (int) (this.getRawY() - this.scrollAmount);
    }

    private boolean isMouseOnScrollBar(int mouseX, int mouseY) {
        if (!showScrollBar || getMaxScroll() <= 0 || !isHovering(mouseX, mouseY)) return false;
        var trackX = getVisualX() + getVisualWidth() - (scrollBarWidth * getScale());
        return mouseX >= trackX && mouseX < getVisualX() + getVisualWidth();
    }

    private void renderScrollBar(GuiGraphics gui) {
        var trackX = getRawX() + getRawWidth() - scrollBarWidth;
        var trackY = getRawY();

        gui.fill(trackX, trackY, trackX + scrollBarWidth, trackY + getRawHeight(), 0x40000000);

        var thumbHeight = getThumbHeight();
        var availableTrack = getRawHeight() - thumbHeight;
        var scrollRatio = (float)(scrollAmount / getMaxScroll());
        var thumbY = trackY + (int)(scrollRatio * availableTrack);

        gui.fill(trackX, thumbY, trackX + scrollBarWidth, thumbY + thumbHeight, 0x80FFFFFF);
    }

    public void recalculateContentHeight() {
        var maxBottom = 0;
        var scrolledY = getScrolledY();

        for (var child : children) {
            if (!child.isVisible()) continue;
            child.updatePosition(getRawX(), scrolledY);

            var bottom = Math.round((child.getVisualY() - scrolledY) + child.getVisualHeight());
            if (bottom > maxBottom) maxBottom = bottom;

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

    // GETTERS
    public int getMaxScroll() {return Math.max(0, contentHeight - getRawHeight());}
    public double getScrollAmount() {return scrollAmount;}
    private int getThumbHeight() {
        if (this.contentHeight <= 0) return this.getRawHeight();
        var visibleRatio = (float)getRawHeight() / this.contentHeight;
        return Math.max(12, (int)(getRawHeight() * visibleRatio));
    }

    // SETTERS
    public void setScrollAmount(double amount) {scrollAmount = Math.clamp(amount, 0, getMaxScroll());}
}
