package com.tyzsskills.impl.client.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.tyzsskills.Tyzsskills;
import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.client.records.SkillTooltipData;
import com.tyzsskills.impl.client.tools.StringTools;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class SkillTooltip implements ClientTooltipComponent {
    public SkillTooltip(@NotNull SkillTooltipData data){
        skill = data.skill();

        this.descriptionLines = StringTools.getSkillDescription(this.skill);
        this.displayName = StringTools.getSkillFormattedName(skill);
        this.requirementLines = StringTools.getSkillRequirements(skill);
    }

    private final ISkill skill;
    protected static final ResourceLocation DEFAULT_ICON = ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "textures/gui/skills/default.png");
    protected static final ResourceLocation LOCK_ICON = ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "textures/gui/skills/locked.png");


    private final Component displayName;
    private final List<FormattedCharSequence> descriptionLines;
    private final List<Component> requirementLines;
    private final Component requirementLine = Component.translatable("gui.tyzs_skills.requirements").withStyle(ChatFormatting.DARK_GRAY);


    @Override
    public int getHeight() {
        var headerHeight = 29;
        var textHeight = this.descriptionLines.size() * 10;
        var infosHeight = 0;

        if (hasInfos()) {
            infosHeight += 4;

            if (!isShiftPressed()) infosHeight += 10;
            else {
                infosHeight += 2;
                infosHeight += requirementLines.size() * 10;
            }
        }

        return headerHeight + textHeight + infosHeight;
    }

    @Override
    public int getWidth(Font font) {
        var headerWidth = 26 + font.width(this.displayName);
        var requirementWidth = font.width(this.requirementLine);
        var maxDescWidth = 122;

        return Math.max(headerWidth, Math.max(maxDescWidth, requirementWidth));
    }

    @Override
    public void renderImage(@NotNull Font font, int x, int y, GuiGraphics guiGraphics) {
        renderSkillIcon(guiGraphics, x, y);

        renderGlint(guiGraphics, x, y);

        renderTitle(guiGraphics, x, y, font);

        renderSeparation(guiGraphics, x, y, font, y + 25);

        var endY = renderSkillDescription(guiGraphics, x, y, font);

        renderInfos(guiGraphics, x, y, font, endY + 2);
    }


    //GETTERS
    @NotNull
    public ISkill getSkill(){return  skill;}
    private boolean isMaxed(){return ClientCache.getSkillLevel(skill.getID()) >= skill.getMaximumLevel();}
    private boolean isShiftPressed(){return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT);}
    private boolean hasInfos() {
        return skill.getRequiredLevel() != 0
                || !skill.getRawIncompatibilities().isEmpty()
                || !skill.getRawPrerequisites().isEmpty();
    }

    //UTILS
    private void renderTitle(GuiGraphics gui, int x, int y, Font font) {
        gui.drawString(font, this.displayName, x + 26, y + 7, 0xFFFFFF, true);
    }

    private void renderInfos(GuiGraphics gui, int x, int y, Font font, int startY){
        if(hasInfos()){

            var currentY = startY + 4;

            if(!isShiftPressed()) {
                gui.drawString(font, requirementLine, x, currentY, 0xFFFFFF, true);
            }
            else{
                renderSeparation(gui, x, y, font, startY);
                for (var line : requirementLines){
                    gui.drawString(font, line, x, currentY, 0xFFFFFF, true);
                    currentY += 10;
                }
            }
        }
    }

    private void renderSkillIcon(GuiGraphics gui, int x, int y){
        ResourceLocation icon = skill.isAvailable(ClientCache.getCurrentContext(skill.getID())) ? ResourceLocation.tryParse(skill.getIcon()) : LOCK_ICON;
        if(icon == null) icon = DEFAULT_ICON;

        var borderColor = isMaxed() ? 0xFFD6AD55 : 0xFFD6D6D6;

        gui.fill(x + 1, y, x + 21, y + 22, borderColor); //border
        gui.fill(x, y + 1, x + 22, y + 21, borderColor); //border
        gui.fill(x + 2, y + 1, x + 20, y + 21, 0xFF222222); //background
        gui.fill(x + 1, y + 2, x + 21, y + 20, 0xFF222222); //background
        gui.blit(icon, x + 3, y + 3, 0, 0, 16, 16, 16, 16); //icon
    }

    private int renderSkillDescription(GuiGraphics gui, int x, int y, Font font){
        int currentTextY = y + 28;
        for (FormattedCharSequence line : this.descriptionLines) {
            gui.drawString(font, line, x, currentTextY, 0xFFFFFF, true);
            currentTextY += 10;
        }

        return currentTextY;
    }

    private void renderSeparation(GuiGraphics gui, int x, int y, Font font, int absoluteY){
        var totalWidth = this.getWidth(font);
        var lineWidth = (int)(totalWidth * 0.85f);

        float startX = x + (totalWidth - lineWidth) / 2f;
        float endX = startX + lineWidth;
        float centerX = startX + lineWidth / 2f;

        float leftWaypoint = startX + (lineWidth * .05f);
        float rightWaypoint = endX - (lineWidth * .05f);

        var colorTransparent = isMaxed() ? 0x00D6AD55 : 0x00555555;
        var colorMid = isMaxed() ? 0xCCD6AD55 : 0xCC555555;
        var colorOpaque = isMaxed() ? 0xFFD6AD55 : 0xFF555555;

        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Matrix4f matrix = gui.pose().last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        bufferbuilder.addVertex(matrix, startX, absoluteY, 0).setColor(colorTransparent);
        bufferbuilder.addVertex(matrix, startX, absoluteY + 1, 0).setColor(colorTransparent);
        bufferbuilder.addVertex(matrix, leftWaypoint, absoluteY + 1, 0).setColor(colorMid);
        bufferbuilder.addVertex(matrix, leftWaypoint, absoluteY, 0).setColor(colorMid);

        bufferbuilder.addVertex(matrix, leftWaypoint, absoluteY, 0).setColor(colorMid);
        bufferbuilder.addVertex(matrix, leftWaypoint, absoluteY + 1, 0).setColor(colorMid);
        bufferbuilder.addVertex(matrix, centerX, absoluteY + 1, 0).setColor(colorOpaque);
        bufferbuilder.addVertex(matrix, centerX, absoluteY, 0).setColor(colorOpaque);

        bufferbuilder.addVertex(matrix, centerX, absoluteY, 0).setColor(colorOpaque);
        bufferbuilder.addVertex(matrix, centerX, absoluteY + 1, 0).setColor(colorOpaque);
        bufferbuilder.addVertex(matrix, rightWaypoint, absoluteY + 1, 0).setColor(colorMid);
        bufferbuilder.addVertex(matrix, rightWaypoint, absoluteY, 0).setColor(colorMid);

        bufferbuilder.addVertex(matrix, rightWaypoint, absoluteY, 0).setColor(colorMid);
        bufferbuilder.addVertex(matrix, rightWaypoint, absoluteY + 1, 0).setColor(colorMid);
        bufferbuilder.addVertex(matrix, endX, absoluteY + 1, 0).setColor(colorTransparent);
        bufferbuilder.addVertex(matrix, endX, absoluteY, 0).setColor(colorTransparent);

        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
        RenderSystem.disableBlend();
    }

    private void renderGlint(GuiGraphics gui, int x, int y){
        long time = Util.getMillis();

        var loopInterval = 4000L;
        var animDuration = 1250L;

        var currentTimeInLoop = time % loopInterval;


        if (currentTimeInLoop <= animDuration) {

            var t = currentTimeInLoop / (float)animDuration;
            var offset = (float)Math.pow(t, 2.5f);

            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, .2f);

            gui.enableScissor(x, y, x + 22, y + 22);

            var glintX = (int)(x + 22 - (44 * offset));
            var glintY = (int)(y + 22 - (44 * offset));

            ResourceLocation glintTexture = ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "textures/gui/glint_sweep.png");
            gui.blit(glintTexture, glintX, glintY, 0, 0, 22, 22, 22, 22);

            gui.disableScissor();

            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
        }
    }
}
