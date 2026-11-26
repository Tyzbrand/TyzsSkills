package com.tyzsskills.impl.client.tooltips;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.tools.FormatTools;
import com.tyzsskills.impl.client.cache.ClientCache;
import com.tyzsskills.integration.ui.models.UIText;
import com.tyzsskills.integration.ui.models.UITooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class StatsTooltip extends UITooltip {

    private MutableComponent skillLine;
    private MutableComponent spellLine;
    private MutableComponent sessionXpLine;
    private MutableComponent xpLine;
    private MutableComponent spEarnsLine;
    private MutableComponent spSpendsLine;

    private final ClientCache cache;

    private int latestCacheVersion = -1;

    public StatsTooltip() {
        cache = ClientCache.get();

        //TODO: translation
        var header = Component.literal("Statistics");
        addChild(new UIText(0, 0, () -> header).withScale(1.3f, Enums.ScalePivot.TOP_LEFT));

        var startY = 9; var space = 9; var scale = .87f;
        addChild(new UIText(0, startY += space, () -> sessionXpLine).withScale(scale, Enums.ScalePivot.TOP_LEFT));
        addChild(new UIText(0, startY += space, () -> xpLine).withScale(scale, Enums.ScalePivot.TOP_LEFT));
        addChild(new UIText(0, startY += space, () -> spEarnsLine).withScale(scale, Enums.ScalePivot.TOP_LEFT));
        addChild(new UIText(0, startY += space, () -> spSpendsLine).withScale(scale, Enums.ScalePivot.TOP_LEFT));
        addChild(new UIText(0, startY += space, () -> skillLine).withScale(scale, Enums.ScalePivot.TOP_LEFT));
        addChild(new UIText(0, startY + space, () -> spellLine).withScale(scale, Enums.ScalePivot.TOP_LEFT));

        updateLines();
    }

    @Override
    public void updatePosition(int parentX, int parentY) {
        updateLines();
        super.updatePosition(parentX, parentY);
    }

    private void updateLines(){
        var currentCacheVersion = cache.getVersion();
        if(currentCacheVersion == latestCacheVersion) return;
        latestCacheVersion = currentCacheVersion;

        sessionXpLine = buildRow("gui.tyzs_skills.stats.session_xp", FormatTools.bigFloat(cache.METADATA.getSessionXp()));
        xpLine = buildRow("gui.tyzs_skills.stats.all_time_xp", FormatTools.bigFloat(cache.METADATA.getAllTimeXp()));
        spEarnsLine = buildRow("gui.tyzs_skills.stats.sp_earned", FormatTools.bigFloat(cache.METADATA.getEarnedSp()));
        spSpendsLine = buildRow("gui.tyzs_skills.stats.sp_spent", FormatTools.bigFloat(cache.METADATA.getSpentSp()));
        skillLine = buildRow("gui.tyzs_skills.stats.skill_unlocked",
                String.valueOf(cache.METADATA.getOwnedSkills()), String.valueOf(cache.METADATA.getMaxSkills()));
        spellLine = buildRow("gui.tyzs_skills.stats.spell_unlocked",
                String.valueOf(cache.METADATA.getOwnedSpells()), String.valueOf(cache.SPELLS.getAllSpells().size()));
    }

    private MutableComponent buildRow(String key, String value){
        return Component.empty()
                .append(Component.literal("▶ ").withStyle(ChatFormatting.BLUE))
                .append(Component.translatable(key).withStyle(ChatFormatting.BLUE))
                .append(Component.literal(": ").withStyle(ChatFormatting.BLUE))
                .append(Component.literal(value).withStyle(ChatFormatting.GRAY));
    }

    private MutableComponent buildRow(String key, String value, String max){
        return Component.empty()
                .append(Component.literal("▶ ").withStyle(ChatFormatting.BLUE))
                .append(Component.translatable(key).withStyle(ChatFormatting.BLUE))
                .append(Component.literal(": ").withStyle(ChatFormatting.BLUE))
                .append(Component.literal(value + "/" + max).withStyle(ChatFormatting.GRAY));
    }
}
