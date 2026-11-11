package com.tyzsskills.impl.client.models;

import com.tyzsskills.Config;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.interfaces.ISpell;
import com.tyzsskills.impl.client.Styles;
import com.tyzsskills.impl.client.cache.ClientCache;
import com.tyzsskills.impl.client.screen.SpellWheel;
import com.tyzsskills.impl.client.tooltips.spells.SpellAssignTooltip;
import com.tyzsskills.impl.client.tooltips.spells.SpellPurchaseTooltip;
import com.tyzsskills.impl.client.tooltips.spells.SpellTooltip;
import com.tyzsskills.impl.server.spells.SpellRules;
import com.tyzsskills.integration.ui.models.*;
import com.tyzsskills.integration.ui.records.UIStyles;
import com.tyzsskills.integration.ui.records.UITexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class SpellCard extends UIContainer {
    public static final int WIDTH = 64, HEIGHT = 30;

    protected final ISpell spell;
    protected final UITexture icon;
    protected final ClientCache cache;
    protected final Supplier<Boolean> focusSpell;

    private MutableComponent text;

    private boolean canBuySpell;

    protected int latestCacheVersion = -1;

    //region STYLES
    private static final UIStyles.ImageStyle SKILL_CARD =
            new UIStyles.ImageStyle(64,30, 69, 262, Styles.MAIN_TEXTURE);

    private static final UIStyles.ButtonStyle CADRE_DEFAULT =
            new UIStyles.ButtonStyle(22, 22, 138, 267,  Styles.MAIN_TEXTURE);
    private static final UIStyles.ButtonStyle CADRE_ASSIGNED =
            new UIStyles.ButtonStyle(22, 22, 160, 267,  Styles.MAIN_TEXTURE);
    private static final UIStyles.ButtonStyle CADRE_UNASSIGNED =
            new UIStyles.ButtonStyle(22, 22, 182, 267,  Styles.MAIN_TEXTURE);

    private static final UIStyles.ButtonStyle PURCHASE_BTN =
            new UIStyles.ButtonStyle(31, 9, 139, 217, 170, 217, 201, 217, Styles.MAIN_TEXTURE);
    private static final UIStyles.ButtonStyle UPGRADE_BTN =
            new UIStyles.ButtonStyle(15, 9, 108, 217, 123, 217, Styles.MAIN_TEXTURE);
    private static final UIStyles.ButtonStyle ASSIGN_BTN =
            new UIStyles.ButtonStyle(15, 9, 77, 217, 92, 217, Styles.MAIN_TEXTURE);

    private static final UIStyles.BackgroundStyle CUSTOM_BACK =
            new UIStyles.BackgroundStyle(Styles.COLOR_BG, Styles.COLOR_BORDER, 3, 3);
    //endregion

    //DIRECT
    public SpellCard(ISpell spell, int offsetX, int offsetY, Supplier<Boolean> focusSpell){
        super(offsetX, offsetY, WIDTH, HEIGHT);

        this.spell = spell;
        cache = ClientCache.get();
        this.focusSpell = focusSpell;

        var tempIcon = ResourceLocation.tryParse(spell.getIcon());
        if(tempIcon == null) tempIcon = Styles.DEFAULT_ICON.texture();
        icon = new UITexture(tempIcon, 16);

        updateData();

        this.initElements();
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        updateData();
        super.render(gui, mouseX, mouseY, partialTick);
    }

    //IMPLEMENTATION
    private void initElements(){
        //BACKGROUND
        this.addChild(new UIImage(0, 0, () -> SKILL_CARD));

        //CADRE && ICON
        var spellTooltip = new SpellTooltip(spell);
        this.addChild(new UIButton(4, 4, this::getCadreStyle, this::handleAssignation)
                .withTooltip(() -> spellTooltip));
        this.addChild(new UIImage(7, 7, 16, 16, () -> icon)
                .withScale(.95f, Enums.ScalePivot.CENTER));

        //PROGRESS BAR
        this.addChild(new UIText(30, 8, () -> text)
                .withBackground(() -> CUSTOM_BACK, () -> false)
                .withScale(.75f, Enums.ScalePivot.TOP_LEFT));


        //BUTTONS
        var purchaseTooltip = new SpellPurchaseTooltip(spell);
        this.addChild(new UIButton(28, 17, () -> PURCHASE_BTN, this::handlePurchase)
                .withDisabled(() -> !canBuySpell)
                .withVisibility(() -> !isPurchased())
                .withTooltip(() -> purchaseTooltip));

        var assignTooltip = new SpellAssignTooltip(spell);
        this.addChild(new UIButton(28, 17, () -> ASSIGN_BTN, this::handleAssignation)
                .withVisibility(this::isPurchased)
                .withTooltip(() -> assignTooltip));

        //TODO: translation
        var upgradeTooltip = new UITooltip(Component.literal("Upgrade"));
        this.addChild(new UIButton(44, 17, () -> UPGRADE_BTN, this::handleInfo)
                .withVisibility(this::isPurchased)
                .withTooltip(() -> upgradeTooltip));
    }
    private void updateData(){
        var currentCacheVersion = ClientCache.get().getVersion();
        if(latestCacheVersion == currentCacheVersion) return;
        else latestCacheVersion = currentCacheVersion;

        var pCtx = cache.getPlayerContext();
        var sCtx = cache.SPELLS.getSpellContext(spell.getID());

        canBuySpell = SpellRules.canBuySpell(sCtx, pCtx, cache.getConfigBool(Config.PURCHASE_SYSTEM_KEY, true));

        text = getText();
    }

    //region EXTRACTED LOGIC
    // ==================== BUTTON CLICKS ====================
    private boolean handleAssignation(){
        if(!isPurchased()) return false;
        if(isAssigned()) return cache.SPELLS.triggerSpellAction(spell, Enums.SpellAction.UNASSIGNMENT, cache.SPELLS.getSpellSlot(spell.getID()));

        Minecraft.getInstance().setScreen(new SpellWheel(Enums.WheelMode.ASSIGN, spell));
        return true;
    }

    private boolean handlePurchase(){
        if(isPurchased()) return false;
        return cache.SPELLS.triggerSpellAction(spell, Enums.SpellAction.PURCHASE, -1);
    }

    private boolean handleInfo(){
        return focusSpell.get();
    }

    // ==================== CONDITIONS ====================
    private boolean isPurchased(){
        return cache.SPELLS.isSpellPurchased(spell.getID());
    }

    private boolean isAssigned(){
        return cache.SPELLS.isSpellAssigned(spell.getID());
    }


    // ==================== DATA ====================
    private MutableComponent getText(){
        var part = 0;
        var total = 0;

        for(var property : spell.getProperties()){
            part += cache.SPELLS.getPropertyLevel(spell.getID(), property.key());
            total += property.getMaximumLevel();
        }

        return Component.literal(part + "/" + total);
    }

    // ==================== STYLES ====================
    private UIStyles.ButtonStyle getCadreStyle(){
        if(!isPurchased()) return CADRE_DEFAULT;
        if(isAssigned()) return CADRE_ASSIGNED;
        return CADRE_UNASSIGNED;
    }
    //endregion
}
