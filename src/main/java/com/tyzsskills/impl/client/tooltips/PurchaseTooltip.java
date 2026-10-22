package com.tyzsskills.impl.client.tooltips;

import com.tyzsskills.Config;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.tools.FormatTools;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.integration.ui.models.UIContainer;
import com.tyzsskills.integration.ui.models.UIText;
import com.tyzsskills.integration.ui.models.UITooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;

public class PurchaseTooltip extends UITooltip {
    private final ISkill skill;
    private final ClientCache cache;
    private final UIContainer statContainer;

    private boolean latestShiftState = false;
    private int latestCacheVersion = -1;

    private MutableComponent currentPriceLine;
    private final List<MutableComponent> currentDataLines = new ArrayList<>();

    public PurchaseTooltip(ISkill skill) {
        this.skill = skill;
        this.cache = ClientCache.get();

        this.addChild(new UIText(0, 0, () -> currentPriceLine));

        statContainer = new UIContainer(0, 11);
        this.addChild(statContainer);

        this.withVisibility(() -> skill.isPurchasable() && cache.getConfigBool(Config.PURCHASE_SYSTEM_KEY, true));

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
        var currentShiftState = Screen.hasShiftDown();

        if (latestCacheVersion == currentCacheVersion && latestShiftState == currentShiftState) return;
        latestCacheVersion = currentCacheVersion; latestShiftState = currentShiftState;

        var currentLvl = cache.SKILLS.getSkillLevel(skill.getID());
        var currentSp = cache.METADATA.getSp();
        var targetLvl = getTargetedLevel(currentLvl, currentSp);

        refreshPriceLine(currentLvl, targetLvl, currentSp);
        refreshDataLines(currentLvl, targetLvl);
    }

    //UTILS
    //==================TEXTS==================
    private void refreshPriceLine(int currentLvl, int targetLvl, int currentSp){
        if(currentLvl >= skill.getMaximumLevel()){
            currentPriceLine = Component.translatable("gui.tyzs_skills.level_max").withStyle(ChatFormatting.GOLD);
            return;
        }

        var nextPrice = skill.getPrice(currentLvl + 1);
        var priceColor = currentSp < nextPrice ? ChatFormatting.RED : ChatFormatting.BLUE;
        var delta = targetLvl - currentLvl;

        currentPriceLine = Component.empty()
                .append(Component.translatable("gui.tyzs_skills.cost"))
                .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(getFinalPrice(currentLvl, targetLvl) + " ").withStyle(priceColor))
                .append(Component.translatable("gui.tyzs_skills.SP").withStyle(priceColor));

        if(latestShiftState) currentPriceLine.append(Component.literal(String.format(" (x%d)", delta)));
    }

    private void addStatLine(float currentStat, float targetedStat, String unit){
        var color = targetedStat > currentStat ? ChatFormatting.GREEN : ChatFormatting.RED;
        var translatedUnit = Component.translatable(unit);

        var message = Component.empty()
                .append(Component.literal("◆ "))
                .append(Component.literal(FormatTools.defaultFloat(currentStat) + " ").withStyle(ChatFormatting.DARK_GRAY))
                .append(translatedUnit)
                .append(Component.literal(" -> "))
                .append(Component.literal(FormatTools.defaultFloat(targetedStat) + " ").withStyle(color))
                .append(translatedUnit);

        currentDataLines.add(message);
    }

    private void refreshDataLines(int currentLvl, int targetLvl){
        currentDataLines.clear();
        statContainer.clear();

        if (currentLvl >= skill.getMaximumLevel()) return;

        if (skill.getType() == Enums.SkillType.IMMUTABLE) {
            for (var valueSet : skill.getValues().values()) {
                var currentStat = valueSet.getValue(currentLvl);
                var targetedStat = valueSet.getValue(targetLvl);

                addStatLine(currentStat, targetedStat, valueSet.unit());
            }
        } else {
            for (var modifier : skill.getModifiers()) {
                var currentStat = modifier.getValue(currentLvl);
                var targetedStat = modifier.getValue(targetLvl);

                addStatLine(currentStat, targetedStat, modifier.unit());
            }
        }

        var startY = -11;
        for (var line : currentDataLines) {
            startY += 11;
            statContainer.addChild(new UIText(0, startY, () -> line));
        }
    }


    //==================DATA==================
    private int getFinalPrice(int currentLvl, int targetLvl){
        var totalPrice = 0;
        for (var i = currentLvl + 1; i <= targetLvl; i++) totalPrice += skill.getPrice(i);

        return totalPrice;
    }


    private int getTargetedLevel(int currentLvl, int availableSp){
        if(!latestShiftState) return currentLvl + 1;

        int targetLvl = 0;
        for(int i = currentLvl + 1; i <= skill.getMaximumLevel(); i++){
            var price = skill.getPrice(i);
            if(price > availableSp) break;

            targetLvl = i;
            availableSp -= price;
        }

        return Math.max(targetLvl, currentLvl + 1);
    }
}
