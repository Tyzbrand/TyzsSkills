package com.tyzsskills.impl.client.tools;

import com.tyzsskills.api.ui.UIElement;
import net.minecraft.client.gui.GuiGraphics;

public class UITools {
    public static void drawWithShade (GuiGraphics gui, Runnable blit){
        gui.setColor(0.3F, 0.3F, 0.3F, 1.0F);
        blit.run();
        gui.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    //-------------SCALE-------------
    public static void drawScaledFromCenter(GuiGraphics gui, UIElement element, int anchorX, int anchorY, float scale, int mouseX, int mouseY, float partialTick){
        int pivotX = anchorX + element.offsetX + (element.width / 2);
        int pivotY = anchorY + element.offsetY + (element.height / 2);
        drawWithScaleAndAnchor(gui, element, anchorX, anchorY, pivotX, pivotY, scale, mouseX, mouseY, partialTick);
    }

    public static void drawScaledFromTopLeft(GuiGraphics gui, UIElement element, int anchorX, int anchorY, float scale, int mouseX, int mouseY, float partialTick){
        int pivotX = anchorX + element.offsetX;
        int pivotY = anchorY + element.offsetY;
        drawWithScaleAndAnchor(gui, element, anchorX, anchorY, pivotX, pivotY, scale, mouseX, mouseY, partialTick);
    }


    private static void drawWithScaleAndAnchor(GuiGraphics gui, UIElement element, int anchorX, int anchorY, int pivotX, int pivotY, float scale, int mouseX, int mouseY, float partialTick){
        gui.pose().pushPose();
        gui.pose().translate(pivotX, pivotY, 0);
        gui.pose().scale(scale, scale, 1.0f);
        gui.pose().translate(-pivotX, -pivotY, 0);

        element.updatePosition(anchorX, anchorY);
        element.render(gui, mouseX, mouseY, partialTick);

        gui.pose().popPose();
    }
}
