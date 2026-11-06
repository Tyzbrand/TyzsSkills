package com.tyzsskills.impl.client.tooltips;

import com.tyzsskills.api.interfaces.ISpell;
import com.tyzsskills.impl.client.cache.ClientCache;
import com.tyzsskills.integration.ui.models.UIText;
import com.tyzsskills.integration.ui.models.UITooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class SpellActionTooltip extends UITooltip {
    private final ISpell spell;
    private final ClientCache cache;

    private MutableComponent currentActionLine;
    private int latestCacheVersion = -1;

    public SpellActionTooltip(ISpell spell) {
        this.spell = spell;
        this.cache = ClientCache.get();

        this.addChild(new UIText(0, 0, () -> currentActionLine));

        updateData();
    }

    @Override
    public void updatePosition(int parentX, int parentY) {
        updateData();
        super.updatePosition(parentX, parentY);
    }

    //IMPL
    private void updateData() {
        var currentCacheVersion = cache.getVersion();
        if (latestCacheVersion == currentCacheVersion ) return;
        latestCacheVersion = currentCacheVersion;

        refreshActionLine();
    }

    private void refreshActionLine(){
        var isPurchased = cache.SPELLS.isSpellPurchased(spell.getID());
        var isAssigned = cache.SPELLS.isSpellAssigned(spell.getID());
        var currentSp = cache.METADATA.getSp();

        if(!isPurchased){

            var color = currentSp < spell.getPrice() ? ChatFormatting.RED : ChatFormatting.BLUE;

            currentActionLine = Component.empty()
                    .append(Component.translatable("gui.tyzs_skills.cost"))
                    .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(spell.getPrice() + " ").withStyle(color))
                    .append(Component.translatable("gui.tyzs_skills.SP").withStyle(color));
        }
        //TODO: translation
        else if(isAssigned) currentActionLine = Component.literal("Unassign");
        //TODO: translation
        else currentActionLine = Component.literal("Assign");
    }

}
