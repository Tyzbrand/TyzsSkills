package com.tyzsskills.impl.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.datafixers.util.Either;
import com.tyzsskills.Config;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.impl.client.ui.*;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.client.tooltips.SkillTooltipData;
import com.tyzsskills.impl.client.tools.StringTools;
import com.tyzsskills.impl.server.skills.SkillRules;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.lwjgl.glfw.GLFW;
import oshi.util.tuples.Pair;

import java.util.List;

public class SkillWidget {
    protected int x, y;
    public static final int WIDTH = 64, HEIGHT = 30;
    protected final UIContainer skillCard;
    protected final ISkill skill;
    protected final ResourceLocation icon;
    protected final ClientCache cache;

    protected boolean isLocked;
    protected boolean isMaxed;
    protected boolean canAffordPurchase;
    protected boolean canAffordRefund;
    protected boolean canBuy;
    protected boolean canRefund;
    protected boolean isShiftPressed;

    protected int currentLevel;

    public SkillWidget(ISkill skill){
        this.skill = skill;
        this.cache = ClientCache.get();
        this.skillCard = new UIContainer(0, 0, WIDTH, HEIGHT);

        var candidate = ResourceLocation.tryParse(skill.getIcon());
         if(candidate != null && Minecraft.getInstance().getResourceManager().getResource(candidate).isPresent()){
            this.icon = candidate;
        }
        else this.icon = UIStyleRegistries.DEFAULT_SKILL_ICON;

        this.init();
    }

    private void init(){
        //BACKGROUND & ICON
        skillCard.addChild(new UIImage(0, 0, () -> isMaxed? UIStyleRegistries.SKILL_CARD_COMPLETE : UIStyleRegistries.SKILL_CARD));
        skillCard.addChild(new UIImage(7, 7, 16, 16, this.icon, 16)
                .withTooltip(() -> List.of(Either.right(new SkillTooltipData(this.skill)))));

        //BADGE
        skillCard.addChild(new UIBackground(27, 7, 53, 13, UIStyleRegistries.COLOR_BG)
                .withBorder(() -> new Pair<>(isMaxed, UIStyleRegistries.COLOR_BORDER_MAXED))
                .withScale(.65f, Enums.ScalePivot.TOP_LEFT));
        var textScale = .57f;
        skillCard.addChild(new UIText(29, 9, 30, 10, () ->
                !isLocked ?  Component.translatable("gui.tyzs_skills.Lvl").append(": " + cache.getSkillLevel(skill.getID()) + "/" + skill.getMaximumLevel())
                        : Component.translatable("gui.tyzs_skills.locked"))
                .withWrapWidth((int)(30 / textScale)).withScale(textScale, Enums.ScalePivot.TOP_LEFT));

        //BUTTONS
        skillCard.addChild(
        new UIButton(38, 17,
                () -> isShiftPressed ? UIStyleRegistries.BULK_PURCHASE_BTN : UIStyleRegistries.PURCHASE_BTN,
                () -> {if(isLocked) return false; return cache.triggerAction(skill, isShiftPressed ? Enums.ClientAction.BULK_PURCHASE : Enums.ClientAction.PURCHASE);}
        ).withVisibility(() -> !isLocked && !isMaxed && canAffordPurchase && canBuy)
                .withTooltip(() -> {
                    if (isLocked) return UIElement.EMPTY_TOOLTIP;
                    var lines = StringTools.getTooltipAction(skill, currentLevel, Enums.TooltipType.PURCHASE, canAffordPurchase, isShiftPressed);
                    return lines.stream().map(Either::<FormattedText, TooltipComponent>left).toList();}));

        skillCard.addChild(new UIButton(27, 17,
                () -> isShiftPressed ? UIStyleRegistries.BULK_REFUND_BTN : UIStyleRegistries.REFUND_BTN,
                () -> {if(isLocked) return false; return cache.triggerAction(skill, isShiftPressed ? Enums.ClientAction.BULK_REFUND : Enums.ClientAction.REFUND);}
        ).withVisibility(() -> currentLevel > 0 &&  !isLocked && canAffordRefund && canRefund)
                .withTooltip(() -> {
                    if (isLocked) return UIElement.EMPTY_TOOLTIP;
                    var lines = StringTools.getTooltipAction(skill, currentLevel, Enums.TooltipType.REFUND, false, isShiftPressed);
                    return lines.stream().map(Either::<FormattedText, TooltipComponent>left).toList();
                }));

        skillCard.addChild(new UIButton(49, 17,
                () -> cache.isSkillBookMarked(skill.getID().toLowerCase()) ? UIStyleRegistries.BOOKMARK_BTN_ON : UIStyleRegistries.BOOKMARK_BTN_OFF,
                () -> cache.triggerAction(skill, Enums.ClientAction.BOOKMARK)));

        skillCard.withShade(() -> isLocked);
    }

    public void render(GuiGraphics gui, int x, int y, int mouseX, int mouseY, float partialTick){
        this.updateData();

        this.x = x;
        this.y = y;

        skillCard.updatePosition(this.x, this.y);
        skillCard.draw(gui, mouseX, mouseY, partialTick);
    }

    private void updateData(){
        var sCtx = cache.getSkillContext(skill.getID());
        var pCtx = cache.getPlayerContext();

        this.isShiftPressed = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT);
        this.isLocked = !SkillRules.isAvailable(sCtx, pCtx);
        this.isMaxed = cache.getSkillLevel(skill.getID().toLowerCase()) >= skill.getMaximumLevel();
        this.canAffordPurchase = SkillRules.canBuy(sCtx, pCtx, cache.getConfigBool(Config.PURCHASE_SYSTEM_KEY, true));
        this.canAffordRefund = SkillRules.canRefund(sCtx, cache.getConfigBool(Config.REFUND_SYSTEM_KEY, false));
        this.canBuy = cache.getConfigBool(Config.PURCHASE_SYSTEM_KEY, true) && skill.isPurchasable();
        this.canRefund = cache.getConfigBool(Config.REFUND_SYSTEM_KEY, true) && skill.isRefundable();

        this.currentLevel = cache.getSkillLevel(this.skill.getID());
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button){
        return skillCard.handleClick((int)mouseX, (int)mouseY);
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x && mouseX < x + WIDTH && mouseY >= y && mouseY < y + HEIGHT;
    }

    public List<Either<FormattedText, TooltipComponent>> getTooltip(int mouseX, int mouseY){
        return skillCard.getTooltips(mouseX, mouseY);
    }

}
