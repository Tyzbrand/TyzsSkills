package com.tyzsskills.impl.client.tools;

import com.tyzsskills.Config;
import com.tyzsskills.api.Enums;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.server.model.Skill;
import com.tyzsskills.impl.server.model.Trait;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

public class StringTools {

    public static String valueSmartFormat(float value){
        float absValue = Math.abs(value);

        if (absValue >= 1_000_000f) {return SMART_FORMATTER.get().format(value / 1_000_000f) + "M";}
        if (absValue >= 10_000f) {return SMART_FORMATTER.get().format(value / 1_000f) + "k";}
        return SMART_FORMATTER.get().format(value);
    }

    public static MutableComponent getPriceLine(Skill skill, int currentLvl, boolean canBuy, boolean isMax){
        LocalPlayer client = Minecraft.getInstance().player;
        if(skill == null || !skill.isPurchasable() || client == null) return Component.translatable("gui.tyzs_skills.error_value").withStyle(ChatFormatting.RED);

        if(currentLvl >= skill.getMaximumLevel()) return Component.translatable("gui.tyzs_skills.level_max").withStyle(ChatFormatting.GOLD);

        boolean effectiveMax = isMax && !(skill instanceof Trait);
        int totalSpAmount = 0;
        var prices = skill.getPrices();
        boolean affordable = canBuy;

        if (!effectiveMax) {
            if(currentLvl < prices.size()) totalSpAmount = prices.get(currentLvl);
        } else {
            int availableSp = ClientCache.GetSP();
            int simulatedSp = availableSp;
            int levelsAffordable = 0;

            for (int i = currentLvl; i < skill.getMaximumLevel(); i++) {
                if (i >= prices.size()) break;
                int price = prices.get(i);

                if (simulatedSp >= price) {
                    simulatedSp -= price;
                    totalSpAmount += price;
                    levelsAffordable++;
                } else break;

            }

            if (levelsAffordable > 0) {
                affordable = true;
            } else {
                if(currentLvl < prices.size()) totalSpAmount = prices.get(currentLvl);
                affordable = false;
            }
        }

        var style = affordable ? ChatFormatting.BLUE : ChatFormatting.RED;
        return Component.translatable("gui.tyzs_skills.cost").withStyle(ChatFormatting.GRAY)
                .append(": ")
                .append(Component.literal(String.valueOf(totalSpAmount)).withStyle(style)).append(" ")
                .append(Component.translatable("gui.tyzs_skills.SP").withStyle(style));
    }


    public static List<MutableComponent> getTooltipAction(Skill skill, int currentLvl, Enums.TooltipType type, boolean canBuy) {
        return getTooltipAction(skill, currentLvl, type, canBuy, false);
    }

    public static List<MutableComponent> getTooltipAction(Skill skill, int currentLvl, Enums.TooltipType type, boolean canBuy, boolean isMax) {
        LocalPlayer client = Minecraft.getInstance().player;
        List<MutableComponent> lines = new ArrayList<>();

        if(skill == null || client == null) return lines;

        boolean isPurchase = type == Enums.TooltipType.PURCHASE;
        boolean effectiveMax = isMax && !(skill instanceof Trait);
        int targetLvl = currentLvl;

        if(isPurchase) {
            if(currentLvl >= skill.getMaximumLevel()) {
                lines.add(Component.translatable("gui.tyzs_skills.level_max").withStyle(ChatFormatting.GOLD));
                return lines;
            }

            lines.add(getPriceLine(skill, currentLvl, canBuy, isMax));

            if (!effectiveMax) {
                targetLvl = currentLvl + 1;
            } else {
                int availableSp = ClientCache.GetSP();
                int simulatedSp = availableSp;
                int levelsAffordable = 0;
                var prices = skill.getPrices();

                for (int i = currentLvl; i < skill.getMaximumLevel(); i++) {
                    if (i >= prices.size()) break;
                    int price = prices.get(i);

                    if (simulatedSp >= price) {
                        simulatedSp -= price;
                        targetLvl++;
                        levelsAffordable++;
                    } else break;
                }
                if (levelsAffordable == 0) targetLvl = currentLvl + 1;

            }
        } else {
            // LE BLOC QUI MANQUAIT EST ICI
            if(currentLvl <= 0) return lines;
            lines.add(getRefundLine(skill, currentLvl, isMax));

            targetLvl = effectiveMax ? 0 : currentLvl - 1;
        }

        if (targetLvl == currentLvl) return lines;

        if(skill.getType() == Enums.SkillType.GENERIC || skill.getType() == Enums.SkillType.CUSTOM){
            for(var modifier : skill.getModifiers()){
                float diff = modifier.getValue(targetLvl) - modifier.getValue(currentLvl);
                if(diff == 0) continue;
                lines.add(getValueLine(diff, modifier.unit()));
            }
        }
        else if(skill.getType() == Enums.SkillType.IMMUTABLE){
            for(var valueSet : skill.getValues().values()){
                float diff = valueSet.getValue(targetLvl) - valueSet.getValue(currentLvl);
                if(diff == 0) continue;
                lines.add(getValueLine(diff, valueSet.unit()));
            }
        }

        return lines;
    }

    public static MutableComponent getRefundLine(Skill skill, int currentLvl, boolean isMax) {
        if(skill == null || currentLvl <= 0) return Component.empty();

        boolean effectiveMax = isMax && !(skill instanceof Trait);
        int totalSpAmount = 0;
        var prices = skill.getPrices();

        if (!effectiveMax) {
            if (currentLvl - 1 < prices.size()) {
                int p = prices.get(currentLvl - 1);
                int ref = (int)(p * (Config.REFUND_PERCENTAGE.get() / 100f));
                totalSpAmount = p > 0 ? Math.max(1, ref) : 0;
            }
        } else {
            for (int i = currentLvl - 1; i >= 0; i--) {
                if (i < prices.size()) {
                    int p = prices.get(i);
                    int ref = (int)(p * (Config.REFUND_PERCENTAGE.get() / 100f));
                    totalSpAmount += p > 0 ? Math.max(1, ref) : 0;
                }
            }
        }

        return Component.translatable("gui.tyzs_skills.gain").withStyle(ChatFormatting.GRAY)
                .append(": ")
                .append(Component.literal(String.valueOf(totalSpAmount)).withStyle(ChatFormatting.BLUE)).append(" ")
                .append(Component.translatable("gui.tyzs_skills.SP").withStyle(ChatFormatting.BLUE));
    }

    public static List<MutableComponent> getSkillDescription(Skill skill) {
        List<MutableComponent> lines = new ArrayList<>();
        String rawDesc = Component.translatable(skill.getDescription()).getString();
        var currentLvl = ClientCache.GetSkillLevel(skill.getID().toLowerCase());

        if(skill.getType() == Enums.SkillType.GENERIC || skill.getType() == Enums.SkillType.CUSTOM){
            var modifiers = skill.getModifiers();

            for(int i = 0; i < modifiers.size(); i++){
                var targetText = i == 0 ? "{value}" : String.format("{value%d}", i + 1);

                if (rawDesc.contains(targetText)) {
                    var value = modifiers.get(i).getValue(currentLvl);

                    var color = ChatFormatting.WHITE;
                    if(value < 0){color = ChatFormatting.RED;}
                    else if (value > 0){color = ChatFormatting.GREEN;}

                    String coloredValue = color + valueSmartFormat(value) + ChatFormatting.GRAY;
                    rawDesc = rawDesc.replace(targetText, coloredValue);
                }
            }
        }

        if(skill.getType() == Enums.SkillType.IMMUTABLE) {
            for(var entry : skill.getValues().entrySet()){
                String targetText = "{" + entry.getKey() + "}";

                if (rawDesc.contains(targetText)) {
                    float value = entry.getValue().getValue(currentLvl);

                    var color = ChatFormatting.WHITE;
                    if(value < 0){color = ChatFormatting.RED;}
                    else if (value > 0){color = ChatFormatting.GREEN;}

                    String coloredValue = color + valueSmartFormat(value) + ChatFormatting.GRAY;
                    rawDesc = rawDesc.replace(targetText, coloredValue);
                }
            }
        }

        Font font = Minecraft.getInstance().font;
        MutableComponent fullDesc = Component.literal(rawDesc).withStyle(ChatFormatting.GRAY);

        List<FormattedCharSequence> splitLines = font.split(fullDesc, 152);

        for (FormattedCharSequence line : splitLines) {
            MutableComponent lineComponent = Component.empty();

            line.accept((index, style, codePoint) -> {
                lineComponent.append(Component.literal(String.valueOf((char) codePoint)).withStyle(style));
                return true;
            });

            lines.add(lineComponent);
        }

        return lines;
    }


    //Utils
    private static MutableComponent getValueLine(float diff, String unit){
        var color = ChatFormatting.WHITE;

        if(diff < 0){color = ChatFormatting.RED;}
        else if (diff > 0){color = ChatFormatting.GREEN;}

        var sign = diff > 0 ? "+" : "";

        return Component.literal("-> ")
                .append(Component.literal(sign + valueSmartFormat(diff) + " ").withStyle(color))
                .append(Component.translatable(unit));
    }
    private static final ThreadLocal<DecimalFormat> SMART_FORMATTER = ThreadLocal.withInitial(() -> {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');

        DecimalFormat format = new DecimalFormat("0.#", symbols);
        format.setRoundingMode(RoundingMode.DOWN);
        return format;
    });









}
