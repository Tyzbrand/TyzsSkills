package com.tyzsskills.impl.client.models;

import com.tyzsskills.Config;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.interfaces.ISpell;
import com.tyzsskills.impl.client.Styles;
import com.tyzsskills.impl.client.cache.ClientCache;
import com.tyzsskills.impl.client.screen.SpellWheel;
import com.tyzsskills.impl.client.tooltips.SpellActionTooltip;
import com.tyzsskills.impl.client.tooltips.SpellTooltip;
import com.tyzsskills.impl.server.spells.SpellRules;
import com.tyzsskills.integration.ui.models.UIButton;
import com.tyzsskills.integration.ui.models.UIContainer;
import com.tyzsskills.integration.ui.models.UIImage;
import com.tyzsskills.integration.ui.models.UIProgressBar;
import com.tyzsskills.integration.ui.records.UIStyles;
import com.tyzsskills.integration.ui.records.UITexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class SpellCard extends UIContainer {
    public static final int WIDTH = 64, HEIGHT = 30;

    protected final ISpell spell;
    protected final UITexture icon;
    protected final ClientCache cache;

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

    private static final UIStyles.ButtonStyle ACTION_BUTTON =
            new UIStyles.ButtonStyle(15, 9, 136, 216, 151, 216, 166, 216, Styles.MAIN_TEXTURE);
    private static final UIStyles.ButtonStyle INFO_BUTTON =
            new UIStyles.ButtonStyle(15, 9, 182, 216, 197, 216, Styles.MAIN_TEXTURE);

    private static final UIStyles.ImageStyle PROGRESS_EMPTY =
            new UIStyles.ImageStyle(31, 5, 102, 217, Styles.MAIN_TEXTURE);
    private static final UIStyles.ImageStyle PROGRESS_FILL =
            new UIStyles.ImageStyle(31, 5, 71, 217, Styles.MAIN_TEXTURE);


    //endregion

    //DIRECT
    public SpellCard(ISpell spell, int offsetX, int offsetY){
        super(offsetX, offsetY, WIDTH, HEIGHT);

        this.spell = spell;
        cache = ClientCache.get();

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
        this.addChild(new UIImage(28, 8, () -> PROGRESS_EMPTY));
        this.addChild(new UIProgressBar(28, 8, () -> PROGRESS_FILL, () -> cache.SPELLS.getPropertiesProgress(spell.getID())));



        //BUTTONS
        var actionTooltip = new SpellActionTooltip(spell);
        this.addChild(new UIButton(28, 15, () -> ACTION_BUTTON, this::handleAction)
                .withDisabled(this::isActionDisabled)
                .withTooltip(() -> actionTooltip));

        this.addChild(new UIButton(44, 15, () -> INFO_BUTTON, this::handleInfo));
    }

    private void updateData(){
        var currentCacheVersion = ClientCache.get().getVersion();
        if(latestCacheVersion == currentCacheVersion) return;
        else latestCacheVersion = currentCacheVersion;
    }

    //region EXTRACTED LOGIC
    // ==================== BUTTON CLICKS ====================
    private boolean handleAssignation(){
        if(!isPurchased()) return false;
        if(isAssigned()) return cache.SPELLS.triggerAction(spell, Enums.SpellAction.UNASSIGNMENT, cache.SPELLS.getSpellSlot(spell.getID()));

        Minecraft.getInstance().setScreen(new SpellWheel(Enums.WheelMode.ASSIGN, spell));
        return true;
    }

    private boolean handleAction(){
        if(!isPurchased()) return cache.SPELLS.triggerAction(spell, Enums.SpellAction.PURCHASE, -1);
        return handleAssignation();
    }

    private boolean handleInfo(){
        return true;
    }

    // ==================== CONDITIONS ====================
    private boolean isPurchased(){
        return cache.SPELLS.isSpellPurchased(spell.getID());
    }

    private boolean isAssigned(){
        return cache.SPELLS.isSpellAssigned(spell.getID());
    }

    private boolean isActionDisabled(){
        return !isPurchased() && !SpellRules.canBuySpell(cache.SPELLS.getSpellContext(spell.getID()), cache.getPlayerContext(),
                cache.getConfigBool(Config.PURCHASE_SYSTEM_KEY, true));
    }

    // ==================== DATA ====================


    // ==================== STYLES ====================
    private UIStyles.ButtonStyle getCadreStyle(){
        if(!isPurchased()) return CADRE_DEFAULT;
        if(isAssigned()) return CADRE_ASSIGNED;
        return CADRE_UNASSIGNED;
    }
    //endregion
}
