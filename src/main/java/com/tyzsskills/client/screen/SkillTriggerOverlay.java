package com.tyzsskills.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tyzsskills.Config;
import com.tyzsskills.Tyzsskills;
import com.tyzsskills.client.ClientCache;
import com.tyzsskills.server.model.Skill;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

public class SkillTriggerOverlay implements LayeredDraw.Layer {


    private record Notification(ResourceLocation icon, long startTime){}

    private static final List<Notification> activeNotifications = new ArrayList<>();

    private static final long duration = 2000L;
    private static final long fadeIn = 200L;
    private static final long fadeOut = 500L;

    private static final int spacing = 32;

    private static final float scale = .65f;

    private static final ResourceLocation DEFAULT_ICON = ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "textures/gui/skills/default.png");


    private static final int COLOR_BG = 0XAA000000;
    private static final int COLOR_BORDER = 0XFFD6AD55;

    public static void ShowSkillIcon(String id){
        if(!Config.SHOW_SKILL_OVERLAY.get()) return;
        Skill skill = ClientCache.GetSkill(id.toLowerCase());
        if(skill != null){
            var icon = ResourceLocation.tryParse(skill.GetIcon());
            if(icon == null || Minecraft.getInstance().getResourceManager().getResource(icon).isEmpty()){
                icon = DEFAULT_ICON;
            }

            for(int i = 0; i<activeNotifications.size(); i++){
                if(activeNotifications.get(i).icon.equals(icon)){
                    activeNotifications.set(i, new Notification(icon, System.currentTimeMillis()));
                    return;
                }
            }

            activeNotifications.add(new Notification(icon, System.currentTimeMillis()));
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (activeNotifications.isEmpty()) return;

        var mc = Minecraft.getInstance();
        if(mc.player == null) return;
        if(mc.player.isCreative() && !Config.SHOW_OVERLAYS_IN_CREATIVE.get()) return;

        int height = mc.getWindow().getGuiScaledHeight();
        long now = System.currentTimeMillis();

        activeNotifications.removeIf(n -> (now - n.startTime) > duration);
        if (activeNotifications.isEmpty()) return;


        int index = 0;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        for (var notif : activeNotifications) {
            renderSingleNotification(guiGraphics, notif, index, height, now);
            index++;
        }

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
    }



    private void renderSingleNotification(GuiGraphics guiGraphics, Notification notif, int index, int screenHeight, long now){
        long timeSinceActivation = now - notif.startTime;
        float alpha = 1f;

        if(timeSinceActivation < fadeIn) alpha = (float)timeSinceActivation/fadeIn;
        else if (timeSinceActivation > (duration - fadeOut)) alpha = (float)(duration - timeSinceActivation)/fadeOut;
        else{
            float timeInPhase = timeSinceActivation - fadeIn;
            float speed = .01f;

            alpha = .8f + .2f * Mth.sin(timeInPhase * speed);
        }
        alpha = Mth.clamp(alpha, 0f, 1f);
        if (alpha <= 0.05f) return;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(scale, scale, 1f);

        int iconSize = 16;
        int padding = 5;
        int boxSize = iconSize + padding * 2;


        int scaledScreenHeight = (int)(screenHeight/scale);
        int startY = (int)(scaledScreenHeight * .25f);

        int drawX = (int)(padding + 2);
        int drawY = startY + (index * spacing);

        int boxY = startY + (index * spacing) - padding;
        int boxX = drawX - padding;

        int bgAlpha = (int)((COLOR_BG >> 24 & 255) * alpha);
        int bgColor = (bgAlpha << 24) | (COLOR_BG & 0x00FFFFFF);
        renderTooltipStyleRect(guiGraphics, boxX, boxY, boxSize, boxSize, bgColor);

        int borderAlpha = (int)((COLOR_BORDER >> 24 & 255) * alpha);
        int borderColor = (borderAlpha << 24) | (COLOR_BORDER & 0x00FFFFFF);
        renderTooltipStyleBorder(guiGraphics, boxX, boxY, boxSize, boxSize, borderColor);

        RenderSystem.setShaderColor(1f, 1f, 1f, alpha);

        guiGraphics.blit(notif.icon(), drawX, drawY, 0, 0, iconSize, iconSize, iconSize, iconSize);

        guiGraphics.pose().popPose();
    }

    public static void Clear(){
        activeNotifications.clear();
    }

    private void renderTooltipStyleRect(GuiGraphics gui, int x, int y, int width, int height, int color) {
        gui.fill(x, y + 1, x + width, y + height - 1, color);
        gui.fill(x + 1, y, x + width - 1, y + 1, color);
        gui.fill(x + 1, y + height - 1, x + width - 1, y + height, color);
    }

    private void renderTooltipStyleBorder(GuiGraphics gui, int x, int y, int width, int height, int color) {
        gui.fill(x + 1, y, x + width - 1, y + 1, color);
        gui.fill(x + 1, y + height - 1, x + width - 1, y + height, color);
        gui.fill(x, y + 1, x + 1, y + height - 1, color);
        gui.fill(x + width - 1, y + 1, x + width, y + height - 1, color);
    }
}
