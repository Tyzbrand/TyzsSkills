package com.tyzsskills.impl.client.tooltips.spells;

import com.tyzsskills.api.interfaces.ISpell;
import com.tyzsskills.impl.client.cache.ClientCache;
import com.tyzsskills.integration.ui.models.UIText;
import com.tyzsskills.integration.ui.models.UITooltip;
import net.minecraft.network.chat.Component;

public class SpellAssignTooltip extends UITooltip {
    public SpellAssignTooltip(ISpell spell) {
        var cache = ClientCache.get();

        //TODO: translation
        this.addChild(new UIText(0, 0,
                () -> cache.SPELLS.isSpellAssigned(spell.getID()) ? Component.literal("Unassign") : Component.literal("Assign")));
    }
}
