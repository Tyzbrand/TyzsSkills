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

            int contentHeight = this.getItemCount() * this.itemHeight;

            int barH = (int)((float)(this.height * this.height) / (float)contentHeight);
            barH = Mth.clamp(barH, 32, this.height - 8);

            int scrollBarY = this.getY() + (int)((float)this.getScrollAmount() * (float)(this.height - barH) / (float)maxScroll);
            if(scrollBarY < this.getY()) scrollBarY = this.getY();

            boolean isHoveringBar = mouseX >= scrollBarX && mouseX < scrollBarX + scrollWidth
                    && mouseY >= scrollBarY && mouseY < scrollBarY + barH;

            int currentU = uScroll;
            int currentV = vScroll;

            if(isHoveringBar) {currentU = uScrollHover; currentV = vScrollHover;}

            gui.blit(texture, scrollBarX, scrollBarY, currentU, currentV, scrollWidth, barH, textureW, textureH);

        }
    }

    public void clearEntries(){
        super.clearEntries();
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
}
