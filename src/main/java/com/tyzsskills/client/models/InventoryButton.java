package com.tyzsskills.client.models;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tyzsskills.Config;
import com.tyzsskills.Tyzsskills;
import com.tyzsskills.server.active.SoundRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class InventoryButton extends AbstractWidget {


    private static final ResourceLocation TEXTURE_NORMAL = ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "textures/gui/background.png");

    private final Runnable onPress;
    private boolean isDragging = false;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;

    public InventoryButton(int x, int y, Runnable onPress) {
        super(x, y, 14, 14, Component.literal("Skills Button"));
        this.onPress = onPress;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.isDragging) {
            this.setX(mouseX - this.dragOffsetX);
            this.setY(mouseY - this.dragOffsetY);
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        guiGraphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);


        int currentU = this.isHovered ? 97 : 83;


        guiGraphics.blit(TEXTURE_NORMAL, this.getX(), this.getY(), currentU, 154, 14, 14, 325, 325);

        guiGraphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();

        if (this.isHovered() && !this.isDragging) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font,
                    Component.translatable("button.tyzs_skills.inventory_btn"), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isHoveredOrFocused()) {
            if (button == 1) {
                this.isDragging = true;
                this.dragOffsetX = (int) (mouseX - this.getX());
                this.dragOffsetY = (int) (mouseY - this.getY());
                this.playDownSound(Minecraft.getInstance().getSoundManager());
                return true;
            } else if (button == 0) {
                this.playDownSound(Minecraft.getInstance().getSoundManager());
                this.onPress.run();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.isDragging) {
            this.isDragging = false;
            Config.INVENTORY_BUTTON_X.set(this.getX());
            Config.INVENTORY_BUTTON_Y.set(this.getY());
            Config.CLIENT_SPEC.save();
            return true;
        }
        return false;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }

    @Override
    public void playDownSound(SoundManager handler) {
        LocalPlayer player = Minecraft.getInstance().player;
        if(player != null) SoundRegistry.PlayUIClick();
    }
}
