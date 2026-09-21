package com.tyzsskills.impl.client.ui;

import com.mojang.datafixers.util.Either;
import com.tyzsskills.api.Enums;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public abstract class UIElement {

    public int offsetX, offsetY, x, y, width, height;
    protected float scale = 1f;
    protected Enums.ScalePivot scalePivot = Enums.ScalePivot.TOP_LEFT;

    private boolean currentShaded = false;
    protected boolean isCurrentShaded(){return currentShaded;}

    protected Supplier<Boolean> visibleWhen = () -> true;
    protected Supplier<Boolean> shadedWhen = () -> false;
    protected Supplier<List<Either<FormattedText, TooltipComponent>>> tooltip = () -> EMPTY_TOOLTIP;

    public UIElement(int offsetX, int offsetY, int width, int height){
        this.offsetX = offsetX;
        this.offsetY = offsetY;

        this.width = width;
        this.height = height;
    }

    //Fluents
    public UIElement withVisibility(Supplier<Boolean> supplier) {this.visibleWhen = supplier; return this;}
    public UIElement withShade(Supplier<Boolean> supplier) {this.shadedWhen = supplier; return this;}
    public UIElement withScale(float scale, Enums.ScalePivot scalePivot) {this.scale = scale; this.scalePivot = scalePivot; return this;}

    //Tooltips
    private UIElement withRawTooltip(Supplier<List<Either<FormattedText, TooltipComponent>>> supplier) {
        this.tooltip = supplier != null ? supplier : () -> EMPTY_TOOLTIP;
        return this;
    }
    public UIElement withTooltip(Supplier<? extends FormattedText> supplier) {
        return this.withRawTooltip(() -> {
            FormattedText text = supplier.get();
            if (text == null) return EMPTY_TOOLTIP;
            return List.of(Either.left(text));
        });
    }
    public UIElement withTooltipLines(Supplier<? extends List<? extends FormattedText>> supplier) {
        return this.withRawTooltip(() -> {
            var lines = supplier.get();
            if (lines == null || lines.isEmpty()) return EMPTY_TOOLTIP;
            return lines.stream().<Either<FormattedText, TooltipComponent>>map(Either::left).toList();
        });
    }
    public UIElement withCustomTooltip(Supplier<? extends TooltipComponent> supplier) {
        return this.withRawTooltip(() -> {
            TooltipComponent data = supplier.get();
            if (data == null) return EMPTY_TOOLTIP;
            return List.of(Either.right(data));
        });
    }

    //Draws
    protected abstract void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick);

    public final void draw(GuiGraphics gui, int mouseX, int mouseY, float partialTick, boolean inheritedVisible, boolean inheritedShaded){
        var visible = inheritedVisible && visibleWhen.get();
        if(!visible) return;

        currentShaded = inheritedShaded || shadedWhen.get();

        if(scale != 1f) drawScaled(gui, mouseX, mouseY, partialTick, currentShaded);
        else drawShadedOrNot(gui, mouseX, mouseY, partialTick, currentShaded);
    }

    public final void draw(GuiGraphics gui, int mouseX, int mouseY, float partialTick){
        draw(gui, mouseX, mouseY, partialTick, true, false);
    }

    public void drawTooltips(GuiGraphics gui, Font font, int mouseX, int mouseY) {
        var tooltips = getTooltips(mouseX, mouseY);
        if (tooltips.isEmpty()) return;

        List<Component> textLines = new ArrayList<>();
        TooltipComponent customComponent = null;

        for (var either : tooltips) {
            var left = either.left();
            if (left.isPresent()) {
                FormattedText ft = left.get();
                textLines.add(ft instanceof Component c ? c : Component.literal(ft.getString()));
            }
            var right = either.right();
            if (right.isPresent()) {
                customComponent = right.get();
            }
        }

        gui.renderTooltip(font, textLines, Optional.ofNullable(customComponent), mouseX, mouseY);
    }

    //Clicks
    protected boolean onClick(int mouseX, int mouseY){return false;}

    public final boolean handleClick(int mouseX, int mouseY, boolean inheritedVisible) {
        boolean visible = inheritedVisible && visibleWhen.get();
        if (!visible) return false;
        return onClick(mouseX, mouseY);
    }

    public final boolean handleClick(int mouseX, int mouseY){
        return handleClick(mouseX, mouseY, true);
    }

    //Tooltips
    protected List<Either<FormattedText, TooltipComponent>> resolveTooltips(int mouseX, int mouseY) {
        return tooltip.get();
    }

    public final List<Either<FormattedText, TooltipComponent>> getTooltips(int mouseX, int mouseY, boolean inheritedVisible) {
        boolean visible = inheritedVisible && visibleWhen.get();
        if (!visible || !isHovering(mouseX, mouseY)) return EMPTY_TOOLTIP;
        return resolveTooltips(mouseX, mouseY);
    }

    public final List<Either<FormattedText, TooltipComponent>> getTooltips(int mouseX, int mouseY) {
        return getTooltips(mouseX, mouseY, true);
    }


    //Tools
    public boolean isHovering(int mouseX, int mouseY){
        if (width <= 0 && height <= 0) return true;
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
    public final void updatePosition(int parentX, int parentY){
        this.x = parentX + this.offsetX;
        this.y = parentY + this.offsetY;
    }

    //Utils
    private void drawShadedOrNot(GuiGraphics gui, int mouseX, int mouseY, float partialTick, boolean shaded) {
        if (!shaded) { render(gui, mouseX, mouseY, partialTick); return; }
        gui.setColor(0.3f, 0.3f, 0.3f, 1.0f);
        render(gui, mouseX, mouseY, partialTick);
        gui.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void drawScaled(GuiGraphics gui, int mouseX, int mouseY, float partialTick, boolean shaded) {
        float pivotX = (scalePivot == Enums.ScalePivot.CENTER) ? x + (width / 2f) : x;
        float pivotY = (scalePivot == Enums.ScalePivot.CENTER) ? y + (height / 2f) : y;

        gui.pose().pushPose();
        gui.pose().translate(pivotX, pivotY, 0);
        gui.pose().scale(scale, scale, 1.0f);
        gui.pose().translate(-pivotX, -pivotY, 0);

        drawShadedOrNot(gui, mouseX, mouseY, partialTick, shaded);

        gui.pose().popPose();
    }

    //STATIC
    public static final List<Either<FormattedText, TooltipComponent>> EMPTY_TOOLTIP = List.of();


}
