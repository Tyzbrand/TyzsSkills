package com.tyzsskills.impl.client.ui;

import net.minecraft.client.gui.GuiGraphics;

public class UIScrollView extends UIContainer{
    private int contentHeight;
    private double scrollAmount;

    public UIScrollView(int offsetX, int offsetY, int width, int height){
        super(offsetX, offsetY, width, height);
    }

    //IMPL
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaY){
        if(!isHovering((int)mouseX, (int)mouseY)) return false;

        setScrollAmount(scrollAmount - (deltaY * 20d));
        return true;
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


    //PARENTS
    @Override
    protected void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        gui.enableScissor(this.x, this.y, this.x + this.width, this.y + this.height);

        var scrolledY = (int) (this.y - this.scrollAmount);
        for (var child : children) {
            child.updatePosition(this.x, scrolledY);
            child.draw(gui, mouseX, mouseY, partialTick, true, isCurrentShaded());
        }

        gui.disableScissor();
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
        return super.onClick(mouseX, mouseY);
    }

    //GETTERS
    public int getMaxScroll(){return Math.max(0, contentHeight - height);}
    public double getScrollAmount() {return scrollAmount;}

    //SETTERS
    public void setScrollAmount(double amount){scrollAmount = Math.clamp(amount, 0, getMaxScroll());}
    public void setContentHeight(int contentHeight){
        this.contentHeight = contentHeight;
        setScrollAmount(scrollAmount);
    }
}
