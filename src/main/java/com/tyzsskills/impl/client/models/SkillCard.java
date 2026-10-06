package com.tyzsskills.impl.client.models;

import com.tyzsskills.Config;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.impl.client.Styles;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.client.active.ComponentManager;
import com.tyzsskills.impl.server.skills.SkillRules;
import com.tyzsskills.integration.ui.records.UIStyles;
import com.tyzsskills.integration.ui.models.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class SkillCard extends UIContainer {
    public static final int WIDTH = 64, HEIGHT = 30;

    protected final ISkill skill;
    protected final ResourceLocation icon;
    protected final ClientCache cache;

    protected boolean isLocked;
    protected boolean isMaxed;
    protected boolean isBookmarked;
    protected boolean isDeactivated;
    protected boolean canAffordPurchase;
    protected boolean canAffordRefund;
    protected boolean canBuy;
    protected boolean canRefund;
    protected int currentLevel;

    protected boolean isShiftPressed(){return Screen.hasShiftDown();}

    protected int latestCacheVersion = -1;

    //region STYLES
    private static final UIStyles.ImageStyle SKILL_CARD =
            new UIStyles.ImageStyle(64,30, 69, 262, Styles.MAIN_TEXTURE);
    private static final UIStyles.ImageStyle SKILL_CARD_MAXED =
            new UIStyles.ImageStyle(64,30, 69, 292, Styles.MAIN_TEXTURE);

    private static final UIStyles.ButtonStyle CADRE_DEFAULT =
            new UIStyles.ButtonStyle(22, 22, 138, 267,  Styles.MAIN_TEXTURE);
    private static final UIStyles.ButtonStyle CADRE_ACTIVE =
            new UIStyles.ButtonStyle(22, 22, 160, 267,  Styles.MAIN_TEXTURE);
    private static final UIStyles.ButtonStyle CADRE_INACTIVE =
            new UIStyles.ButtonStyle(22, 22, 182, 267,  Styles.MAIN_TEXTURE);
    private static final UIStyles.ButtonStyle CADRE_MAXED =
            new UIStyles.ButtonStyle(22, 22, 204, 267,  Styles.MAIN_TEXTURE);

    private static final UIStyles.ButtonStyle PURCHASE_BTN =
            new UIStyles.ButtonStyle(9, 9, 69, 227, 78, 227, 87, 227, Styles.MAIN_TEXTURE);
    private static final UIStyles.ButtonStyle BULK_PURCHASE_BTN =
            new UIStyles.ButtonStyle(9, 9, 127, 227, 136, 227, 87, 227, Styles.MAIN_TEXTURE);

    private static final UIStyles.ButtonStyle REFUND_BTN =
            new UIStyles.ButtonStyle(9, 9, 98, 227, 107, 227, 116, 227, Styles.MAIN_TEXTURE);
    private static final UIStyles.ButtonStyle BULK_REFUND_BTN =
            new UIStyles.ButtonStyle(9, 9, 147, 227, 156, 227, 116, 227, Styles.MAIN_TEXTURE);

    private static final UIStyles.ButtonStyle BOOKMARK_ON =
            new UIStyles.ButtonStyle(9, 9, 166, 227, 175, 227, Styles.MAIN_TEXTURE);
    private static final UIStyles.ButtonStyle BOOKMARK_OFF =
            new UIStyles.ButtonStyle(9, 9, 186, 227, 195, 227, Styles.MAIN_TEXTURE);

    private static final UIStyles.BackgroundStyle LEVEL_BACKGROUND =
            new UIStyles.BackgroundStyle(Styles.COLOR_BG, Styles.COLOR_BORDER_MAXED, 3, 3);
    //endregion

    //DIRECT
    public SkillCard(ISkill skill, int offsetX, int offsetY){
        super(offsetX, offsetY, WIDTH, HEIGHT);

        this.skill = skill;
        cache = ClientCache.get();
        icon = resolveIcon();

        updateData();

        this.withShade(() -> isLocked);
        this.initElements();
    }

    @Override
    protected void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        updateData();
        super.render(gui, mouseX, mouseY, partialTick);
    }

    //IMPLEMENTATION
    private void initElements(){
        //BACKGROUND & ICON
        this.addChild(new UIImage(0, 0, () -> isMaxed ? SKILL_CARD_MAXED : SKILL_CARD));
        this.addChild(new UIButton(4, 4, this::getCadreStyle, this::handleDeactivation));
        this.addChild(new UIImage(7, 7, 16, 16, this.icon, 16).withScale(.95f, Enums.ScalePivot.CENTER));
//                .withCustomTooltip(() -> new SkillTooltipData(this.skill)));

        //BADGE
        var textScale = .57f;
        this.addChild(new UIText(29, 9, 30, 10, this::getSkillLevelText)
                .withWrapWidth((int)(30 / textScale)).withAlignment(Enums.TextAlignment.LEFT).withBackground(() -> LEVEL_BACKGROUND, () -> isMaxed)
                .withScale(textScale, Enums.ScalePivot.TOP_LEFT));

        //BUTTONS
        this.addChild(new UIButton(38, 17, () -> isShiftPressed() ? BULK_PURCHASE_BTN : PURCHASE_BTN, this::handlePurchase)
                        .withDisabled(this::isPurchaseDisabled).withTooltipLines(this::getPurchaseTooltip));

        this.addChild(new UIButton(27, 17, () -> isShiftPressed() ? BULK_REFUND_BTN : REFUND_BTN, this::handleRefund)
                .withDisabled(this::isRefundDisabled).withTooltipLines(this::getRefundTooltip));

        this.addChild(new UIButton(49, 17, () -> isBookmarked ? BOOKMARK_ON : BOOKMARK_OFF, this::handleBookmark));
    }
    private ResourceLocation resolveIcon(){
        var candidate = ResourceLocation.tryParse(skill.getIcon());
        return candidate != null && Minecraft.getInstance().getResourceManager().getResource(candidate).isPresent() ?
                candidate : Styles.DEFAULT_SKILL_ICON.texture();
    }
    private void updateData(){
        var currentCacheVersion = ClientCache.get().getVersion();
        if(latestCacheVersion == currentCacheVersion) return;
        else latestCacheVersion = currentCacheVersion;

        var sCtx = cache.getSkillContext(skill.getID());
        var pCtx = cache.getPlayerContext();

        this.isLocked = !SkillRules.isAvailable(sCtx, pCtx);
        this.isMaxed = cache.getSkillLevel(skill.getID()) >= skill.getMaximumLevel();
        this.isBookmarked = cache.isSkillBookMarked(skill.getID());
        this.isDeactivated = cache.isSkillDeactivated(skill.getID());
        this.canAffordPurchase = SkillRules.canBuy(sCtx, pCtx, cache.getConfigBool(Config.PURCHASE_SYSTEM_KEY, true));
        this.canAffordRefund = SkillRules.canRefund(sCtx, pCtx, cache.getConfigBool(Config.REFUND_SYSTEM_KEY, false));
        this.canBuy = cache.getConfigBool(Config.PURCHASE_SYSTEM_KEY, true) && skill.isPurchasable();
        this.canRefund = cache.getConfigBool(Config.REFUND_SYSTEM_KEY, true) && skill.isRefundable();

        this.currentLevel = cache.getSkillLevel(skill.getID());
    }

    //region EXTRACTED LOGIC
    // ==================== BUTTON CLICKS ====================
    private boolean handlePurchase(){
        return cache.triggerAction(skill, isShiftPressed() ? Enums.ClientAction.BULK_PURCHASE : Enums.ClientAction.PURCHASE);
    }
    private boolean handleRefund(){
        return cache.triggerAction(skill, isShiftPressed() ? Enums.ClientAction.BULK_REFUND : Enums.ClientAction.REFUND);
    }
    private boolean handleBookmark(){
        return cache.triggerAction(skill, Enums.ClientAction.BOOKMARK);
    }
    private boolean handleDeactivation(){
        return cache.triggerAction(skill, Enums.ClientAction.DEACTIVATION);
    }

    // ==================== CONDITIONS ====================
    private boolean isPurchaseDisabled(){
        return isLocked || isMaxed || !canAffordPurchase || !canBuy;
    }
    private boolean isRefundDisabled(){
        return currentLevel <= 0 || isLocked || !canAffordRefund || !canRefund;
    }

    // ==================== TEXTS & TOOLTIPS ====================
    private List<MutableComponent> getPurchaseTooltip(){
        if (isLocked) return List.of();
        return  ComponentManager.getTooltipAction(skill, currentLevel, Enums.TooltipType.PURCHASE, canAffordPurchase, isShiftPressed());
    }
    private List<MutableComponent> getRefundTooltip(){
        if (isLocked) return List.of();
        return ComponentManager.getTooltipAction(skill, currentLevel, Enums.TooltipType.REFUND, false, isShiftPressed());
    }
    private MutableComponent getSkillLevelText(){
        return !isLocked
                ? Component.translatable("gui.tyzs_skills.Lvl").append(": " + currentLevel + "/" + skill.getMaximumLevel())
                : Component.translatable("gui.tyzs_skills.locked");
    }

    // ==================== STYLES ====================
    private UIStyles.ButtonStyle getCadreStyle(){
        if(isDeactivated) return CADRE_INACTIVE;
        if(isMaxed) return CADRE_MAXED;
        return currentLevel > 0 ? CADRE_ACTIVE : CADRE_DEFAULT;
    }
    //endregion
}
