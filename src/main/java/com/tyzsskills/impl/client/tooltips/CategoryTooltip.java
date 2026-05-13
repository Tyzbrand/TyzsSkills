package com.tyzsskills.impl.client.tooltips;

import com.tyzsskills.api.model.Category;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class CategoryTooltip implements ClientTooltipComponent {

    private static final ResourceLocation DEFAULT_ICON = ResourceLocation.parse("minecraft:textures/item/barrier.png");

    private final ResourceLocation icon;
    private final Component displayName;

    private final float iconScale = .8f;
    private final int scaledSize = Math.round(16 * iconScale);

    public CategoryTooltip(@NotNull CategoryTooltipData data){
        var parsedIcon = ResourceLocation.tryParse(data.category().icon());
        this.icon = parsedIcon == null ? DEFAULT_ICON : parsedIcon;

        this.displayName = Component.translatable(data.category().displayName());
    }

    @Override
    public int getHeight() {
        return Math.max(scaledSize, 9);
    }

    @Override
    public int getWidth(Font font) {
        return scaledSize + 4 + font.width(this.displayName);
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        var iconOffsetY = -1;
        var textOffsetY = 2;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y + iconOffsetY, 0);
        guiGraphics.pose().scale(iconScale, iconScale, 1.0f);
        guiGraphics.setColor(1.0f, 1.0f, 1.0f, 1f);

        guiGraphics.blit(this.icon, 0, 0, 0, 0, 16, 16, 16, 16);
        guiGraphics.pose().popPose();


        var textX = x + scaledSize + 4;
        var textY = y + textOffsetY;

        guiGraphics.drawString(font, this.displayName, textX, textY, 0xFFFFFF, true);
    }

}
