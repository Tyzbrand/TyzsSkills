package com.tyzsskills.impl.client.models;

import com.tyzsskills.Config;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.interfaces.ISpell;
import com.tyzsskills.impl.client.Styles;
import com.tyzsskills.impl.client.cache.ClientCache;
import com.tyzsskills.impl.client.screen.SpellWheel;
import com.tyzsskills.impl.client.tooltips.spells.SpellPurchaseTooltip;
import com.tyzsskills.impl.server.spells.SpellRules;
import com.tyzsskills.integration.ui.models.UIButton;
import com.tyzsskills.integration.ui.models.UIContainer;
import com.tyzsskills.integration.ui.models.UIImage;
import com.tyzsskills.integration.ui.records.UIStyles;
import com.tyzsskills.integration.ui.records.UITexture;
import net.minecraft.client.Minecraft;

public class SpellCardLegacy extends UIContainer {
    public static final int WIDTH = 38, HEIGHT = 48;

    private final UITexture icon;
    private final ClientCache cache;
    private final ISpell spell;

    //region STYLES
    private static final UIStyles.ButtonStyle CARD_LOCKED =
            new UIStyles.ButtonStyle(38, 48, 138, 292, Styles.MAIN_TEXTURE);
    private static final UIStyles.ButtonStyle CARD_UNLOCKED =
            new UIStyles.ButtonStyle(38, 48, 214, 292, Styles.MAIN_TEXTURE);
    private static final UIStyles.ButtonStyle CARD_ASSIGNED =
            new UIStyles.ButtonStyle(38, 48, 176, 292, Styles.MAIN_TEXTURE);

    private static final UIStyles.ButtonStyle ACTION_BTN =
            new UIStyles.ButtonStyle(24, 9, 78, 217, 102, 217, 126, 217, Styles.MAIN_TEXTURE);

    //endregion

    public SpellCardLegacy(ISpell spell, int offsetX, int offsetY){
        super(offsetX, offsetY, WIDTH, HEIGHT);

        this.spell = spell;
        cache = ClientCache.get();
        icon = new UITexture(spell.getIcon(), 16);

        addChild(new UIButton(0, 0, this::getCardStyle, this::handleFocus));

        addChild(new UIImage(11, 10, 16, 16, () -> icon));

        var actionTooltip = new SpellPurchaseTooltip(spell);
        addChild(new UIButton(7, 33, () -> ACTION_BTN, this::handleAction)
                .withDisabled(this::isButtonDisabled)
                .withTooltip(() -> actionTooltip));
    }

    //region EXTRACTED LOGIC
    // ==================== CLICKS ====================
    private boolean handleFocus(){
        return true;
    }
    private boolean handleAction(){
        var spellId = spell.getID();
        if(!cache.SPELLS.isSpellPurchased(spellId)) return cache.SPELLS.triggerSpellAction(spell, Enums.SpellAction.PURCHASE, -1);
        if(cache.SPELLS.isSpellAssigned(spellId)) return cache.SPELLS.triggerSpellAction(spell, Enums.SpellAction.UNASSIGNMENT, cache.SPELLS.getSpellSlot(spellId));
        else Minecraft.getInstance().setScreen(new SpellWheel(Enums.WheelMode.ASSIGN, spell));
        return true;
    }

    // ==================== CONDITIONS ====================
    private boolean isAssigned(){return cache.SPELLS.isSpellAssigned(spell.getID());}
    private boolean isPurchased(){return cache.SPELLS.isSpellPurchased(spell.getID());}
    private boolean isButtonDisabled(){
        return !isPurchased() && !SpellRules.canBuySpell(cache.SPELLS.getSpellContext(spell.getID()), cache.getPlayerContext(),
                cache.getConfigBool(Config.PURCHASE_SYSTEM_KEY, true));
    }

    // ==================== STYLES ====================
    private UIStyles.ButtonStyle getCardStyle(){return isPurchased() ? isAssigned() ? CARD_ASSIGNED : CARD_UNLOCKED : CARD_LOCKED;}
    //endregion
}
