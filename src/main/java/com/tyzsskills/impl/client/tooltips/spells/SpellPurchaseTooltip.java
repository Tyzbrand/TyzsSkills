package com.tyzsskills.impl.client.tooltips.spells;

import com.tyzsskills.api.interfaces.ISpell;
import com.tyzsskills.impl.client.cache.ClientCache;
import com.tyzsskills.integration.ui.models.UIText;
import com.tyzsskills.integration.ui.models.UITooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class SpellPurchaseTooltip extends UITooltip {
    public SpellPurchaseTooltip(ISpell spell) {
        this.addChild(new UIText(0, 0, () -> getText(spell)));
    }

    private MutableComponent getText(ISpell spell){
        var color = ClientCache.get().METADATA.getSp() < spell.getPrice() ? ChatFormatting.RED : ChatFormatting.BLUE;
        return Component.empty()
                .append(Component.translatable("gui.tyzs_skills.cost"))
                .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(spell.getPrice() + " ").withStyle(color))
                .append(Component.translatable("gui.tyzs_skills.SP").withStyle(color));
    }


}
