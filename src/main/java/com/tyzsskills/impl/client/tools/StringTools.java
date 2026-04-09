package com.tyzsskills.impl.client.tools;

import com.tyzsskills.Config;
import com.tyzsskills.api.Enums;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.server.model.Skill;
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

    public static MutableComponent getPriceLine(Skill skill, int lvlToBuy, boolean canBuy){
        LocalPlayer client = Minecraft.getInstance().player;
        if(skill == null || !skill.isPurchasable() || client == null) return Component.translatable("gui.tyzs_skills.error_value").withStyle(ChatFormatting.RED);

        if(lvlToBuy  > skill.getMaximumLevel()) return Component.translatable("gui.tyzs_skills.level_max").withStyle(ChatFormatting.GOLD);

        var prices = skill.getPrices();
        var priceIndex = lvlToBuy - 1;
        if(priceIndex < 0 ||priceIndex >= prices.size()) return Component.translatable("gui.tyzs_skills.error_value").withStyle(ChatFormatting.RED);

        int price = prices.get(priceIndex);

        return canBuy ?
                Component.translatable("gui.tyzs_skills.cost").withStyle(ChatFormatting.GRAY)
                        .append(": ")
                        .append(Component.literal(ChatFormatting.BLUE + String.valueOf(price))).append(" ")
                        .append(Component.translatable("gui.tyzs_skills.SP").withStyle(ChatFormatting.BLUE))
                :
                (Component.translatable("gui.tyzs_skills.cost")
                        .append(": ").append(Component.literal(String.valueOf(price)))
                        .append(" ").append(Component.translatable("gui.tyzs_skills.SP")).withStyle(ChatFormatting.RED));
    }

    public static List<MutableComponent> getTooltipAction(Skill skill, int targetedLvl, Enums.TooltipType type, boolean canBuy){
        LocalPlayer client = Minecraft.getInstance().player;
        List<MutableComponent> lines = new ArrayList<>();

        var isPurchase = type == Enums.TooltipType.PURCHASE;

        if(skill == null || client == null) return lines;

        if(targetedLvl  > skill.getMaximumLevel()) {
            lines.add(Component.translatable("gui.tyzs_skills.level_max").withStyle(ChatFormatting.GOLD));
            return lines;
        }

        if(isPurchase) lines.add(getPriceLine(skill, targetedLvl, canBuy));
        else {
            if(targetedLvl < 0) return  lines;

            int initialPrice = skill.getPrices().get(targetedLvl - 1);
            int finalPrice = Math.max(1, (int)(initialPrice * (Config.REFUND_PERCENTAGE.get() / 100f)));

            lines.add(Component.translatable("gui.tyzs_skills.gain").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(finalPrice + " ").withStyle(ChatFormatting.BLUE))
                    .append(Component.translatable("gui.tyzs_skills.SP").withStyle(ChatFormatting.BLUE)));
        }


        if(skill.getType() == Enums.SkillType.GENERIC || skill.getType() == Enums.SkillType.CUSTOM){
            for(var modifier : skill.getModifiers()){
                var targetValue = isPurchase ? modifier.getValue(targetedLvl) : modifier.getValue(targetedLvl - 1);
                var currentValue = isPurchase ? modifier.getValue(targetedLvl - 1) : modifier.getValue(targetedLvl);

                var diff = targetValue - currentValue;
                if(diff == 0) continue;

                lines.add(getValueLine(diff ,modifier.unit()));
            }
            return lines;
        }

        if(skill.getType() == Enums.SkillType.IMMUTABLE){
            for(var valueSet : skill.getValues().values()){
                var targetValue = isPurchase ? valueSet.getValue(targetedLvl) : valueSet.getValue(targetedLvl - 1);
                var currentValue = isPurchase ? valueSet.getValue(targetedLvl - 1) : valueSet.getValue(targetedLvl);

                var diff = targetValue - currentValue;
                if(diff == 0) continue;

                lines.add(getValueLine(diff ,valueSet.unit()));
            }
            return lines;
        }

        return lines;
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
