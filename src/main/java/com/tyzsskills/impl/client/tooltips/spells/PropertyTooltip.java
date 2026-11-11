package com.tyzsskills.impl.client.tooltips.spells;

import com.tyzsskills.api.interfaces.ISpell;
import com.tyzsskills.api.records.SpellProperty;
import com.tyzsskills.api.tools.FormatTools;
import com.tyzsskills.impl.client.cache.ClientCache;
import com.tyzsskills.integration.ui.models.UIText;
import com.tyzsskills.integration.ui.models.UITooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class PropertyTooltip extends UITooltip {
    private MutableComponent description;
    private int latestCacheVersion = -1;

    private final ClientCache cache;
    private final SpellProperty property;
    private final ISpell spell;

    public PropertyTooltip(ISpell spell, SpellProperty property){
        this.property = property;
        this.spell = spell;

        cache = ClientCache.get();

        addChild(new UIText(0, 0, () -> description)
                .withWrapWidth(135));

        updateData();
    }

    @Override
    public void updatePosition(int parentX, int parentY) {
        updateData();
        super.updatePosition(parentX, parentY);
    }

    private void updateData(){
        var currentVersion = cache.getVersion();
        if(latestCacheVersion == currentVersion) return;
        latestCacheVersion = currentVersion;

        updateDescription();
    }

    private void updateDescription(){
        description = Component.translatable(property.description());
    }

    //UTILS
    private String getFormatedValue(float value){
        var color = ChatFormatting.WHITE;
        if(value < 0){color = ChatFormatting.RED;}
        else if (value > 0){color = ChatFormatting.GREEN;}

        return color + FormatTools.bigFloat(value) + ChatFormatting.GRAY;
    }
}
