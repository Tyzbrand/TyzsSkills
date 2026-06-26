package com.tyzsskills.impl.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.datafixers.util.Either;
import com.tyzsskills.Config;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.ui.*;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.client.tools.SortingTools;
import com.tyzsskills.impl.client.tools.UITools;
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
    protected static final int BTN_W = 9, BTN_H = 9;
    public static final int WIDTH = 64, HEIGHT = 30;

    protected UIButton PURCHASE_BTN;
    protected UIButton REFUND_BTN;
    protected UIButton BOOKMARK_BTN;

    protected UIImage CARD_BACKGROUND;
    protected UIImage SKILL_ICON;

    protected UIText SKILL_BADGE;
    protected UIBackground SKILL_BADGE_BACKGROUND;

    private final UIElement[] uiElements;

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
        else this.icon = UIStyleRegistries.DEFAULT_SKILL_ICON;

        this.initUIElements();
        uiElements = new UIElement[]{PURCHASE_BTN, REFUND_BTN, BOOKMARK_BTN, CARD_BACKGROUND, SKILL_ICON, SKILL_BADGE_BACKGROUND};
    }
    private void initUIElements(){
        //BUTTONS
        PURCHASE_BTN = new UIButton(38, 17, UIStyleRegistries.PURCHASE_BTN, () -> {
            var actionTask = isShiftPressed() ? Enums.ClientAction.BULK_PURCHASE : Enums.ClientAction.PURCHASE;
            cache.triggerAction(skill, actionTask);
        });

        REFUND_BTN = new UIButton(27, 17, UIStyleRegistries.REFUND_BTN, () -> {
            var actionTask = isShiftPressed() ? Enums.ClientAction.BULK_REFUND : Enums.ClientAction.REFUND;
            cache.triggerAction(skill, actionTask);
        });

        BOOKMARK_BTN = new UIButton(49, 17, UIStyleRegistries.BOOKMARK_BTN_OFF, () -> {
            cache.triggerAction(skill, Enums.ClientAction.BOOKMARK);
            if(Minecraft.getInstance().screen instanceof MainGUI gui && SortingTools.getMainCategory() == Enums.SortingCategory.BOOKMARKS)
                gui.refreshList();
        });

        //TEXTS
        SKILL_BADGE = new UIText(29, 8, 30, 10, () ->{
            return skill.isAvailable(cache.getCurrentContext(skill.getID())) ?
                    Component.translatable("gui.tyzs_skills.Lvl").append(": " + cache.getSkillLevel(skill.getID()) + "/" + skill.getMaximumLevel())
                    : Component.translatable("gui.tyzs_skills.locked");
        });
        SKILL_BADGE.wrapWidth = (int)(30 / 0.58f);

        //BACKGROUNDS
        SKILL_BADGE_BACKGROUND = new UIBackground(27, 6, 34, 9, UIStyleRegistries.COLOR_BG, UIStyleRegistries.COLOR_BORDER);

        //IMAGES
        CARD_BACKGROUND = new UIImage(x, y, UIStyleRegistries.SKILL_CARD);
        SKILL_ICON = new UIImage(7, 7, 16, 16, this.icon, 16);
    }

    public void render(GuiGraphics gui, int x, int y, int mouseX, int mouseY, float partialTick){
        this.x = x;
        this.y = y;
        for (var element : uiElements) element.updatePosition(this.x, this.y);

        boolean isLocked = !skill.isAvailable(cache.getCurrentContext(skill.getID()));
        boolean isMaxed = cache.getSkillLevel(skill.getID().toLowerCase()) >= skill.getMaximumLevel();

        //BACKGROUND
        CARD_BACKGROUND.style =  isMaxed?
                UIStyleRegistries.SKILL_CARD_COMPLETE : UIStyleRegistries.SKILL_CARD;

        Runnable backDraw = () -> {
            CARD_BACKGROUND.render(gui, mouseX, mouseY, partialTick);
            SKILL_ICON.render(gui, mouseX, mouseY, partialTick);
        };

        if(isLocked) UITools.drawWithShade(gui, backDraw);
        else backDraw.run();

        //BADGE
        SKILL_BADGE_BACKGROUND.drawBorder = isMaxed;
        SKILL_BADGE_BACKGROUND.render(gui, mouseX, mouseY, partialTick);

        Runnable renderText = () -> UITools.drawScaledFromTopLeft(gui, SKILL_BADGE, this.x, this.y, .6f, mouseX, mouseY, partialTick);

        if(isLocked) UITools.drawWithShade(gui, renderText);
        else renderText.run();

        //BOOKMARK
        this.BOOKMARK_BTN.style = cache.isSkillBookMarked(skill.getID().toLowerCase()) ? UIStyleRegistries.BOOKMARK_BTN_ON : UIStyleRegistries.BOOKMARK_BTN_OFF;

        if(isLocked) UITools.drawWithShade(gui, () -> this.BOOKMARK_BTN.render(gui, mouseX, mouseY, partialTick));
        else this.BOOKMARK_BTN.render(gui, mouseX, mouseY, partialTick);

        //PURCHASE
       if(canAffordPurchase()){
           this.PURCHASE_BTN.style = isShiftPressed() ? UIStyleRegistries.BULK_PURCHASE_BTN : UIStyleRegistries.PURCHASE_BTN;

           if(isLocked) UITools.drawWithShade(gui, () -> this.PURCHASE_BTN.render(gui, mouseX, mouseY, partialTick));
           else this.PURCHASE_BTN.render(gui, mouseX, mouseY, partialTick);
       }

       //REFUND
       if(canAffordRefund()) {
           this.REFUND_BTN.style = isShiftPressed() ? UIStyleRegistries.BULK_REFUND_BTN : UIStyleRegistries.REFUND_BTN;

           if(isLocked) UITools.drawWithShade(gui, () -> this.REFUND_BTN.render(gui, mouseX, mouseY, partialTick));
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
    private boolean isShiftPressed(){return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT);}

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
