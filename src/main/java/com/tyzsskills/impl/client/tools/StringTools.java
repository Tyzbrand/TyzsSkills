package com.tyzsskills.impl.client.tools;

import com.tyzsskills.Config;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.server.model.Skill;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;

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

    public static MutableComponent getPriceLine(ISkill skill, int currentLvl, boolean canBuy, boolean isMax){
        LocalPlayer client = Minecraft.getInstance().player;
        if(skill == null || !skill.isPurchasable() || client == null) return Component.translatable("gui.tyzs_skills.error_value").withStyle(ChatFormatting.RED);

        if(currentLvl >= skill.getMaximumLevel()) return Component.translatable("gui.tyzs_skills.level_max").withStyle(ChatFormatting.GOLD);

        int totalSpAmount = 0;
        var prices = skill.getPrices();
        boolean affordable = canBuy;

        if (!isMax) {
            if(currentLvl < prices.size()) totalSpAmount = prices.get(currentLvl);
        } else {
            int availableSp = ClientCache.get().getSp();
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


    public static List<MutableComponent> getTooltipAction(ISkill skill, int currentLvl, Enums.TooltipType type, boolean canBuy) {
        return getTooltipAction(skill, currentLvl, type, canBuy, false);
    }

    public static List<MutableComponent> getTooltipAction(ISkill skill, int currentLvl, Enums.TooltipType type, boolean canBuy, boolean isMax) {
        LocalPlayer client = Minecraft.getInstance().player;
        List<MutableComponent> lines = new ArrayList<>();

        if(skill == null || client == null) return lines;

        boolean isPurchase = type == Enums.TooltipType.PURCHASE;
        int targetLvl = currentLvl;

        if(isPurchase) {
            if(currentLvl >= skill.getMaximumLevel()) {
                lines.add(Component.translatable("gui.tyzs_skills.level_max").withStyle(ChatFormatting.GOLD));
                return lines;
            }

            lines.add(getPriceLine(skill, currentLvl, canBuy, isMax));

            if (!isMax) {
                targetLvl = currentLvl + 1;
            } else {
                int availableSp = ClientCache.get().getSp();
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

            targetLvl = isMax ? 0 : currentLvl - 1;
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

    public static MutableComponent getRefundLine(ISkill skill, int currentLvl, boolean isMax) {
        if(skill == null || currentLvl <= 0) return Component.empty();

        int totalSpAmount = 0;
        var prices = skill.getPrices();

        float refundRate = (float)(ClientCache.get().getConfigDouble(Config.REFUND_PERCENTAGE_KEY, 30D) / 100f);

        if (!isMax) {
            if (currentLvl - 1 < prices.size()) {
                int p = prices.get(currentLvl - 1);
                totalSpAmount = (p <= 0) ? 0 : Math.round(p * refundRate);
            }
        } else {
            for (int i = currentLvl - 1; i >= 0; i--) {
                if (i < prices.size()) {
                    int p = prices.get(i);
                    if (p > 0) {
                        totalSpAmount += Math.round(p * refundRate);
                    }
                }
            }
        }

        return Component.translatable("gui.tyzs_skills.gain").withStyle(ChatFormatting.GRAY)
                .append(": ")
                .append(Component.literal(String.valueOf(totalSpAmount)).withStyle(ChatFormatting.BLUE)).append(" ")
                .append(Component.translatable("gui.tyzs_skills.SP").withStyle(ChatFormatting.BLUE));
    }

    public static List<FormattedCharSequence> getSkillDescription(ISkill skill) {
        List<MutableComponent> lines = new ArrayList<>();
        String rawDesc = Component.translatable(skill.getDescription()).getString();
        var currentLvl = ClientCache.get().getSkillLevel(skill.getID().toLowerCase());

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

        return font.split(fullDesc, 122);
    }

    @NotNull
    public static Component getSkillFormattedName(ISkill skill){
        return Component.translatable(skill.getDisplayName()).withStyle(ChatFormatting.DARK_PURPLE);
    }

    @NotNull
    public static List<Component> getSkillRequirements(ISkill skill){
        var lines = new ArrayList<Component>();
        var cache = ClientCache.get();

        var requiredLvl = skill.getRequiredLevel();
        var incompatibilities = skill.getRawIncompatibilities();
        var prerequisites = skill.getRawPrerequisites();

        if(requiredLvl != 0) {
            var color = requiredLvl <= cache.getLevel() ? ChatFormatting.GREEN : ChatFormatting.RED;
            var message = Component.empty()
                    .append(Component.literal("◆").withStyle(ChatFormatting.BLUE))
                    .append(Component.translatable("gui.tyzs_skills.level_lock").withStyle(ChatFormatting.BLUE))
                    .append(Component.literal(": ").withStyle(ChatFormatting.BLUE))
                    .append(Component.literal(String.valueOf(requiredLvl)).withStyle(color));

            lines.add(message);
        }

        if(!incompatibilities.isEmpty()){
            var header = Component.empty()
                    .append(Component.literal("◆").withStyle(ChatFormatting.BLUE))
                    .append(Component.translatable("gui.tyzs_skills.incompatibilities"))
                    .append(Component.literal(":")).withStyle(ChatFormatting.BLUE);

            lines.add(header);

            for(var conflict : incompatibilities){
                var conflictSkill = cache.getSkill(conflict);
                if (conflictSkill == null) continue;

                var color = cache.getSkillLevel(conflict) > 0 ? ChatFormatting.RED : ChatFormatting.DARK_GRAY;

                var message = Component.empty()
                        .append(Component.literal("- ").withStyle(ChatFormatting.DARK_GRAY))
                        .append(Component.translatable(conflictSkill.getDisplayName()).withStyle(color));

                lines.add(message);
            }
        }

        if(!prerequisites.isEmpty()){
            var header = Component.empty()
                    .append(Component.literal("◆").withStyle(ChatFormatting.BLUE))
                    .append(Component.translatable("gui.tyzs_skills.prerequisite"))
                    .append(Component.literal(":")).withStyle(ChatFormatting.BLUE);

            lines.add(header);

            for(var prerequisite : prerequisites){
                var prerequisiteSkill = cache.getSkill(prerequisite);
                if (prerequisiteSkill == null) continue;

                var color = cache.getSkillLevel(prerequisite) <= 0 ? ChatFormatting.RED : ChatFormatting.GREEN;
                var message = Component.empty()
                        .append(Component.literal("- ").withStyle(ChatFormatting.DARK_GRAY))
                        .append(Component.translatable(prerequisiteSkill.getDisplayName()).withStyle(color));

                lines.add(message);
            }
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
