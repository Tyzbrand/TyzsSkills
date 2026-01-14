package com.tyzsskills.client.models;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class CustomScrollView extends ObjectSelectionList<CustomScrollView.Entry> {

    private final ResourceLocation texture;
    private final int textureW, textureH;
    private final int uScroll, vScroll;
    private final int uScrollHover, vScrollHover;
    private final int scrollWidth, scrollHeight;

    private boolean isScrolling = false;
    private double scrollClickOffset = 0;

    /*
     * Constructor:
     * x,y : Screen coordinates (where to draw list container)
     * textureW, textureH : Container size
     * itemHeight : Line height (1 entry on the container)
     * uBackground, vBackground : Images coordinates on ref texture
     */

    public CustomScrollView(Minecraft minecraft, int x, int y, int width, int height, int itemHeight,
                                ResourceLocation texture, int textureW, int textureH,
                               int uScroll, int vScroll, int uScrollHover, int vScrollHover, int scrollWidth, int scrollHeight){

        super (minecraft, width, height, y, itemHeight);

        this.setX(x);

        this.texture = texture;
        this.textureW = textureW;
        this.textureH = textureH;
        this.uScroll = uScroll;
        this.vScroll = vScroll;
        this.uScrollHover = uScrollHover;
        this.vScrollHover = vScrollHover;
        this.scrollWidth = scrollWidth;
        this.scrollHeight = scrollHeight;

        this.setRenderHeader(false, 0);
    }

    @Override
    public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTick){

        gui.enableScissor(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height);

        this.renderListItems(gui, mouseX, mouseY, partialTick);

        gui.disableScissor();

        int maxScroll = this.getMaxScroll();
        if(maxScroll > 0){
            int scrollBarX = this.getScrollbarPosition();
            int scrollBarY = this.getScrollBarTop();

            boolean isHoveringBar = mouseX >= scrollBarX && mouseX < scrollBarX + scrollWidth
                    && mouseY >= scrollBarY && mouseY < scrollBarY + scrollHeight;

            int currentU = uScroll;
            int currentV = vScroll;

            if(isHoveringBar || isScrolling) {currentU = uScrollHover; currentV = vScrollHover;}

            gui.blit(texture, scrollBarX, scrollBarY, currentU, currentV, scrollWidth, scrollHeight, textureW, textureH);

        }
    }

    public void clearEntries(){
        super.clearEntries();
    }

    public void AddEntry(SkillEntry entry){
        super.addEntry(entry);
    }

    public SkillWidget getHoveredWidget(int mouseX, int mouseY) {
        CustomScrollView.Entry entry = this.getEntryAtPosition(mouseX, mouseY);

        if (entry instanceof SkillEntry skillEntry) {
            return skillEntry.getHoveredWidget(mouseX, mouseY);
        }

        return null;
    }

    private int getScrollBarTop() {
        int maxScroll = this.getMaxScroll();
        if (maxScroll <= 0) return this.getY();

        int barTop = this.getY() + (int)((float)this.getScrollAmount() * (float)(this.height - scrollHeight) / (float)maxScroll);

        if (barTop < this.getY()) barTop = this.getY();
        if (barTop > this.getY() + this.height - scrollHeight) barTop = this.getY() + this.height - scrollHeight;

        return barTop;
    }

    @Override
    protected int getScrollbarPosition(){
        return this.getX() + this.width - scrollWidth - 2;
    }

    @Override
    public int getRowWidth(){
        return this.width - scrollWidth - 4;
    }

    @Override
    protected void renderListBackground(GuiGraphics gui){
        //Enleve le fond e terre
    }

    public abstract static class Entry extends ObjectSelectionList.Entry<Entry>{
        @Override
        public @NotNull Component getNarration(){
            return Component.empty();
        }
    }

    @Override
    protected void renderSelection(GuiGraphics gui, int top, int width, int height, int outerColor, int innerColor) {
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.isScrolling = false;

        if (this.getMaxScroll() > 0) {
            int barX = this.getScrollbarPosition();
            int barY = this.getScrollBarTop();

            if (mouseX >= barX && mouseX <= barX + scrollWidth &&
                    mouseY >= barY && mouseY <= barY + scrollHeight) {

                this.isScrolling = true;
                this.scrollClickOffset = mouseY - barY;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.isScrolling = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.isScrolling) {
            int maxScroll = this.getMaxScroll();
            int trackHeight = this.height;

            if (trackHeight > scrollHeight) {
                double d0 = mouseY - this.getY() - this.scrollClickOffset;

                double newScroll = d0 * (double)maxScroll / (double)(trackHeight - scrollHeight);
                this.setScrollAmount(newScroll);
            }
            return true;
        }
        return false;

    }
}
