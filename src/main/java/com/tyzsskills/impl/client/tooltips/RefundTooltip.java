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

public class RefundTooltip extends UITooltip {
    private final ISkill skill;
    private final ClientCache cache;
    private final UIContainer statContainer;

    private boolean latestShiftState = false;
    private int latestCacheVersion = -1;

    private double refundPercentage;

    private MutableComponent currentRefundLine;
    private final List<MutableComponent> currentDataLines = new ArrayList<>();

    public RefundTooltip(ISkill skill) {
        this.skill = skill;
        this.cache = ClientCache.get();

        this.addChild(new UIText(0, 0, () -> currentRefundLine));

        statContainer = new UIContainer(0, 11);
        this.addChild(statContainer);

        this.withVisibility(() -> skill.isRefundable() && cache.getConfigBool(Config.REFUND_SYSTEM_KEY, true) && cache.getSkillLevel(skill.getID()) > 0);

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

        refundPercentage = cache.getConfigDouble(Config.REFUND_PERCENTAGE_KEY, 30d);

        var currentLvl = cache.getSkillLevel(skill.getID());
        var targetLvl = getTargetedLevel(currentLvl);

        refreshPriceLine(currentLvl, targetLvl);
        refreshDataLines(currentLvl, targetLvl);
    }

    //UTILS
    //==================TEXTS==================
    private void refreshPriceLine(int currentLvl, int targetLvl){
        if(currentLvl <= 0) return;

        currentRefundLine = Component.empty()
                .append(Component.translatable("gui.tyzs_skills.gain"))
                .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(getFinalGain(currentLvl, targetLvl) + " ").withStyle(ChatFormatting.BLUE))
                .append(Component.translatable("gui.tyzs_skills.SP").withStyle(ChatFormatting.BLUE));
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

        if (currentLvl == 0) return;

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
    private int getFinalGain(int currentLvl, int targetLvl){
        var totalGain = 0;
        var refundRate = (float)(refundPercentage / 100f);
        for (var i = currentLvl; i > targetLvl; i--) totalGain += Math.round(skill.getPrice(i) * refundRate);
        return totalGain;
    }

    private int getTargetedLevel(int currentLvl){return !latestShiftState ? currentLvl - 1 : 0;
    }
}
