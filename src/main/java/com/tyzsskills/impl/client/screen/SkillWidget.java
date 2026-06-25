package com.tyzsskills.impl.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.datafixers.util.Either;
import com.tyzsskills.Config;
import com.tyzsskills.Tyzsskills;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.ui.UIButton;
import com.tyzsskills.api.ui.UIStyleRegistries;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.client.SoundPlayer;
import com.tyzsskills.impl.client.tools.SortingTools;
import com.tyzsskills.impl.client.tooltips.SkillTooltipData;
import com.tyzsskills.impl.client.tools.StringTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.lwjgl.glfw.GLFW;


import java.util.ArrayList;
import java.util.List;

public class SkillWidget {

    protected static final ResourceLocation DEFAULT_ICON = ResourceLocation.parse("minecraft:textures/item/barrier.png");

    protected static final ResourceLocation REF_TEXTURE = ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "textures/gui/background.png");
    protected static final int TEXTURE_W = 325, TEXTURE_H = 325;

    protected static final int U_BACKGROUND = 166, V_BACKGROUND = 142;
    protected static final int U_BACKGROUND_FINAL = 230;

    public static final int WIDTH = 64, HEIGHT = 30;

    protected static final int BTN_W = 9, BTN_H = 9;

    protected UIButton PURCHASE_BTN;
    protected static final int PURCHASE_BTN_X = 38, PURCHASE_BTN_Y = 17;

    protected UIButton REFUND_BTN;
    protected static final int REFUND_BTN_X = 27, REFUND_BTN_Y = 17;

    protected UIButton BOOKMARK_BTN;
    protected static final int BOOKMARK_BTN_X = 49, BOOKMARK_BTN_Y = 17;


    protected final ISkill skill;
    protected final ResourceLocation icon;
    protected int x, y;
    protected final ClientCache cache;

    public SkillWidget(ISkill skill){
        this.skill = skill;
        this.cache = ClientCache.get();

        var candidate = ResourceLocation.tryParse(skill.getIcon());

         if(candidate != null && Minecraft.getInstance().getResourceManager().getResource(candidate).isPresent()){
            this.icon = candidate;
        }
        else this.icon = DEFAULT_ICON;

        this.initUIElements();
    }

    private void initUIElements(){
        PURCHASE_BTN = new UIButton(x + PURCHASE_BTN_X, y + PURCHASE_BTN_Y, UIStyleRegistries.PURCHASE_BTN, () -> {
            var actionTask = isShiftPressed() ? Enums.ClientAction.BULK_PURCHASE : Enums.ClientAction.PURCHASE;
            cache.triggerAction(skill, actionTask);
        });

        REFUND_BTN = new UIButton(x + REFUND_BTN_X, y + REFUND_BTN_Y, UIStyleRegistries.REFUND_BTN, () -> {
            var actionTask = isShiftPressed() ? Enums.ClientAction.BULK_REFUND : Enums.ClientAction.REFUND;
            cache.triggerAction(skill, actionTask);
        });

        BOOKMARK_BTN = new UIButton(x + BOOKMARK_BTN_X, y + BOOKMARK_BTN_Y, UIStyleRegistries.BOOKMARK_BTN_OFF, () -> {
            cache.triggerAction(skill, Enums.ClientAction.BOOKMARK);
            if(Minecraft.getInstance().screen instanceof MainGUI gui && SortingTools.getMainCategory() == Enums.SortingCategory.BOOKMARKS)
                gui.refreshList();
        });
    }

    public void render(GuiGraphics gui, int x, int y, int mouseX, int mouseY, float partialTick){
        this.x = x;
        this.y = y;

        Font font = Minecraft.getInstance().font;
        boolean isLocked = !skill.isAvailable(cache.getCurrentContext(skill.getID()));

        int currentU = cache.getSkillLevel(skill.getID().toLowerCase()) >= skill.getMaximumLevel() ? U_BACKGROUND_FINAL : U_BACKGROUND;

        if(isLocked){
            drawWithShade(gui, () -> {
                gui.blit(REF_TEXTURE, x, y, currentU, V_BACKGROUND, WIDTH, HEIGHT, TEXTURE_W, TEXTURE_H);
                gui.blit(icon, x+7, y+7, 0, 0, 16, 16, 16, 16);
            });
        }
        else {
            gui.blit(REF_TEXTURE, x, y, currentU, V_BACKGROUND, WIDTH, HEIGHT, TEXTURE_W, TEXTURE_H);
            gui.blit(icon, x+7, y+7, 0, 0, 16, 16, 16, 16);
        }


        MutableComponent text = !isLocked ?
                Component.translatable("gui.tyzs_skills.Lvl")
                .append(": " + cache.getSkillLevel(skill.getID()) + "/" + skill.getMaximumLevel())
                :
                Component.translatable("gui.tyzs_skills.locked");



        float scale = 0.58f;
        int fixedWidth = (int)(30 / scale);

        int textHeight = font.wordWrapHeight(text, fixedWidth);
        int padding = 3;

        gui.pose().pushPose();
        gui.pose().translate(x+29, y+8, 0);
        gui.pose().scale(scale, scale, 1f);

        if(isLocked){
            drawWithShade(gui, () -> {
                renderBackdrop(gui, -padding, -padding, fixedWidth + (padding*2), textHeight + (padding*2), COLOR_BG);
                gui.drawWordWrap(font, text, 0, 1, fixedWidth, 0xFFFFFF);
            });
        }
        else{
            renderBackdrop(gui, -padding, -padding, fixedWidth + (padding*2), textHeight + (padding*2), COLOR_BG);
            gui.drawWordWrap(font, text, 0, 1, fixedWidth, 0xFFFFFF);
        }


        gui.pose().popPose();


        this.BOOKMARK_BTN.x = x + BOOKMARK_BTN_X;
        this.BOOKMARK_BTN.y = y + BOOKMARK_BTN_Y;
        this.BOOKMARK_BTN.style = cache.isSkillBookMarked(skill.getID().toLowerCase()) ? UIStyleRegistries.BOOKMARK_BTN_ON : UIStyleRegistries.BOOKMARK_BTN_OFF;

        if(isLocked) drawWithShade(gui, () -> this.BOOKMARK_BTN.render(gui, mouseX, mouseY, partialTick));
        else this.BOOKMARK_BTN.render(gui, mouseX, mouseY, partialTick);

       if(canAffordPurchase()){
           this.PURCHASE_BTN.x = x + PURCHASE_BTN_X;
           this.PURCHASE_BTN.y = y + PURCHASE_BTN_Y;
           this.PURCHASE_BTN.style = isShiftPressed() ? UIStyleRegistries.BULK_PURCHASE_BTN : UIStyleRegistries.PURCHASE_BTN;

           if(isLocked) drawWithShade(gui, () -> this.PURCHASE_BTN.render(gui, mouseX, mouseY, partialTick));
           else this.PURCHASE_BTN.render(gui, mouseX, mouseY, partialTick);
       }

       if(canAffordRefund()) {
           this.REFUND_BTN.x = x + REFUND_BTN_X;
           this.REFUND_BTN.y = y + REFUND_BTN_Y;
           this.REFUND_BTN.style = isShiftPressed() ? UIStyleRegistries.BULK_REFUND_BTN : UIStyleRegistries.REFUND_BTN;

           if(isLocked) drawWithShade(gui, () -> this.REFUND_BTN.render(gui, mouseX, mouseY, partialTick));
           else this.REFUND_BTN.render(gui, mouseX, mouseY, partialTick);
       }
    }


    public List<Either<FormattedText, TooltipComponent>> getTooltip(int mouseX, int mouseY){
        List<Either<FormattedText, TooltipComponent>> tooltip = new ArrayList<>();
        int currentLevel = cache.getSkillLevel(this.skill.getID().toLowerCase());

        boolean isLocked = !skill.isAvailable(cache.getCurrentContext(skill.getID()));

        if(isMouseOver(mouseX, mouseY, x+4, y+4, 22, 22)) {
            tooltip.add(Either.right(new SkillTooltipData(this.skill)));
            return tooltip;
        }


        if(isMouseOver(mouseX, mouseY, x+27, y+17, BTN_W, BTN_H) && canRefund() && !isLocked) {
            if(isShiftPressed()) {
                for (var line : StringTools.getTooltipAction(skill, currentLevel, Enums.TooltipType.REFUND, false, true))
                    tooltip.add(Either.left(line));
            }
            else {
                for(var line : StringTools.getTooltipAction(skill, currentLevel, Enums.TooltipType.REFUND, false))
                    tooltip.add(Either.left(line));
            }
            return tooltip;
        }

        if(isMouseOver(mouseX, mouseY, x+38, y+17, BTN_W, BTN_H) && canBuy() && !isLocked){ //BUY
            if(isShiftPressed()){
                for (var line : StringTools.getTooltipAction(skill, currentLevel, Enums.TooltipType.PURCHASE, canAffordPurchase(), true))
                    tooltip.add(Either.left(line));
            }
            else {
                for(var line : StringTools.getTooltipAction(skill, currentLevel, Enums.TooltipType.PURCHASE, canAffordPurchase()))
                    tooltip.add(Either.left(line));
            }
            return tooltip;
        }
        return tooltip;
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return isMouseOver(mouseX, mouseY, x, y, WIDTH, HEIGHT);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button){

        if(canAffordPurchase() && this.PURCHASE_BTN.mouseClicked((int)mouseX, (int)mouseY)) return true;

        if(canAffordRefund() && this.REFUND_BTN.mouseClicked((int)mouseX, (int)mouseY)) return true;

        return this.BOOKMARK_BTN.mouseClicked((int) mouseX, (int) mouseY);
    }


    protected boolean isMouseOver(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    //Utils
    protected static final int COLOR_BG = 0xD5000000;
    protected static final int COLOR_BORDER = 0xFFD6AD55;

    protected void drawWithShade (GuiGraphics gui, Runnable blit){
        gui.setColor(0.3F, 0.3F, 0.3F, 1.0F);
        blit.run();
        gui.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private boolean isShiftPressed(){return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT);}
    protected void renderBackdrop(GuiGraphics gui, int x, int y, int width, int height, int color) {
        // Fond
        gui.fill(x, y + 1, x + width, y + height - 1, color);
        gui.fill(x + 1, y, x + width - 1, y + 1, color);
        gui.fill(x + 1, y + height - 1, x + width - 1, y + height, color);

        // Bordure
        if(cache.getSkillLevel(skill.getID().toLowerCase()) < skill.getMaximumLevel()) return;
        gui.fill(x + 1, y, x + width - 1, y + 1, COLOR_BORDER); // Haut
        gui.fill(x + 1, y + height - 1, x + width - 1, y + height, COLOR_BORDER); // Bas
        gui.fill(x, y + 1, x + 1, y + height - 1, COLOR_BORDER); // Gauche
        gui.fill(x + width - 1, y + 1, x + width, y + height - 1, COLOR_BORDER); // Droite
    }

    protected boolean canAffordPurchase(){
        return skill.canBuy(cache.getCurrentContext(skill.getID()), cache.getConfigBool(Config.PURCHASE_SYSTEM_KEY, true));
    }
    protected boolean canAffordRefund(){
        return skill.canRefund(cache.getCurrentContext(skill.getID()), cache.getConfigBool(Config.REFUND_SYSTEM_KEY, false));
    }
    protected boolean canBuy(){
        return cache.getConfigBool(Config.PURCHASE_SYSTEM_KEY, true) && skill.isPurchasable();
    }
    protected boolean canRefund(){
        return cache.getConfigBool(Config.REFUND_SYSTEM_KEY, true) && skill.isRefundable();
    }
}
