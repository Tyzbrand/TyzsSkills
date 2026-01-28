package com.tyzsskills.client.models;

import com.tyzsskills.client.ClientCache;
import com.tyzsskills.client.screen.MainGUI;
import com.tyzsskills.server.active.AttributeRegistry;
import com.tyzsskills.server.model.Skill;
import com.tyzsskills.server.model.Trait;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

public class TraitWidget extends SkillWidget{

    private static final int U_INACTIVE = 166, V_INACTIVE = 142;
    private static final int U_ACTIVE = 82, V_ACTIVE = 251;

    private Trait trait;

    public TraitWidget(Skill skill){
        super(skill);
    }

    @Override
    public void render(GuiGraphics gui, int x, int y, int mouseX, int mouseY, float partialTick) {
        this.x = x;
        this.y = y;

        trait = (Trait)skill;
        Font font = Minecraft.getInstance().font;

        boolean isOwned = ClientCache.GetSkillLevel(trait.GetID().toLowerCase()) > 0;

        int currentU = isOwned? U_ACTIVE : U_INACTIVE;
        int currentV = isOwned? V_ACTIVE : V_INACTIVE;
        gui.blit(REF_TEXTURE, x, y, currentU, currentV, WIDTH, HEIGHT, TEXTURE_W, TEXTURE_H);

        gui.blit(icon, x+7, y+7, 0, 0, 16, 16, 16, 16);

        MutableComponent state;
        int color;

        state = isOwned? Component.translatable("gui.tyzs_skills.active") : Component.translatable("gui.tyzs_skills.inactive");
        color = isOwned? 0xFF1D7525 : 0xFF8F2626;


        float scale = 0.58f;
        int fixedWidth = (int)(30 / scale);

        int textHeight = font.wordWrapHeight(state, fixedWidth);
        int padding = 3;


        gui.pose().pushPose();
        gui.pose().translate(x+29, y+8, 0);
        gui.pose().scale(scale, scale, 1f);

        renderBackdrop(gui, -padding, -padding, fixedWidth + (padding*2), textHeight + (padding*2), color);
        gui.drawWordWrap(font, state, 0, 1, fixedWidth, 0xFFFFFF);

        gui.pose().popPose();

        if(CanBuy(skill)){
            boolean isHoverBuyBtn = isMouseOver(mouseX, mouseY, x+38, y+17, BTN_W, BTN_H);
            int currentBuyU = U_BUY_BTN;
            if(isHoverBuyBtn){currentBuyU = U_BUY_BTN_HOVER;}
            gui.blit(REF_TEXTURE, x+38, y+17, currentBuyU, V_BUY_BTN, BTN_W, BTN_H, TEXTURE_W, TEXTURE_H);
        }
        if(CanRefund(skill)) {
            boolean isHoverRefundBtn = isMouseOver(mouseX, mouseY, x+27, y+17, BTN_W, BTN_H);
            int currentRefundU = U_REFUND_BTN;
            if(isHoverRefundBtn){currentRefundU = U_REFUND_BTN_HOVER;}
            gui.blit(REF_TEXTURE, x+27, y+17, currentRefundU, V_REFUND_BTN, BTN_W, BTN_H, TEXTURE_W, TEXTURE_H);
        }
    }

    @Override
    public List<Component> getTooltip(int mouseX, int mouseY) {
        List<Component> tooltip = new ArrayList<>();

        if(CanRefund(skill) && isMouseOver(mouseX, mouseY, x+27, y+17, BTN_W, BTN_H)){ //REFUND
            tooltip.add(Component.translatable("gui.tyzs_skills.refund"));
            return tooltip;
        }

        //Plusieurs TOOTLIPS
        if(skill.IsPurchasable() && skill.IsSkillActive() && isMouseOver(mouseX, mouseY, x+38, y+17, BTN_W, BTN_H)){ //BUY

            int currentLvl = ClientCache.GetSkillLevel(skill.GetID());
            if(currentLvl >= skill.GetMaximumLevel()) {
                tooltip.add(Component.translatable("gui.tyzs_skills.level_max").withStyle(ChatFormatting.GOLD));
                return tooltip;
            }

            var prices = skill.GetPrices();
            if(currentLvl >= prices.size()) return tooltip; // Sécurité liste
            int price = prices.get(currentLvl);


            if(ClientCache.GetSP() < price) {
                tooltip.add(Component.empty().append(GetPriceString(skill)));
                return tooltip;
            }

            LocalPlayer player = Minecraft.getInstance().player;
            if(player != null){
                var attr = player.getAttribute(AttributeRegistry.TRAIT_POWER);
                if(attr != null){
                    int maxPower = (int)attr.getValue();
                    int futurePower = ClientCache.GetPower() + this.trait.getPowerWeight();

                    if(futurePower > maxPower){
                        tooltip.add(Component.translatable("gui.tyzs_skills.power_needed").withStyle(ChatFormatting.RED));
                        return tooltip;
                    }
                }
            }

            tooltip.add(Component.empty().append(GetPriceString(skill)));

            MutableComponent powerTrad = Component.translatable("gui.tyzs_skills.power");
            String powerLow = powerTrad.getString().toLowerCase();

            tooltip.add(
                    Component.literal("+")
                            .append(Component.literal(String.valueOf(this.trait.getPowerWeight())))
                            .append(Component.literal(" "))
                            .append(Component.literal(powerLow)) .withStyle(ChatFormatting.RED));
            return tooltip;
        }

        if(isMouseOver(mouseX, mouseY, x+4, y+4, 22, 22)){ //DESCRTIPTION
            tooltip.add(Component.translatable(skill.GetDisplayName()).withStyle(ChatFormatting.DARK_PURPLE));
            String rawDesc = Component.translatable(skill.GetDescription()).getString();

            if (rawDesc.contains("{value}")) {
                int currentLvl = ClientCache.GetSkillLevel(skill.GetID().toLowerCase());
                var values = skill.GetValues();

                float val = 0f;

                if (values != null && !values.isEmpty()) {
                    if (currentLvl > 0) {
                        int index = Math.min(currentLvl - 1, values.size() - 1);
                        val = values.get(index);
                    }

                    String coloredValue = ChatFormatting.GREEN + MainGUI.SmartFormat(val) + ChatFormatting.GRAY;
                    rawDesc = rawDesc.replace("{value}", coloredValue);

                } else {
                    rawDesc = rawDesc.replace("{value}", ChatFormatting.GREEN + "0" + ChatFormatting.GRAY);
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

                tooltip.add(lineComponent);
            }

            MutableComponent powerTrad = Component.translatable("gui.tyzs_skills.power");
            String powerLow = powerTrad.getString().toLowerCase();

            MutableComponent powerComponent =
                    Component.literal("(" + this.trait.getPowerWeight() + " ")
                            .append(Component.literal(powerLow))
                            .append(Component.literal(")")).withStyle(ChatFormatting.BLUE);
            tooltip.add(powerComponent);
        }
        return tooltip;
    }

    @Override
    protected boolean CanBuy(Skill skill) {
        LocalPlayer client = Minecraft.getInstance().player;
        if(client == null || skill == null) return false;

        if(!skill.IsPurchasable() || !skill.IsSkillActive()) return false;

        var currentLvl = ClientCache.GetSkillLevel(skill.GetID());
        if(currentLvl >= skill.GetMaximumLevel()) return false;

        var prices = skill.GetPrices();
        if(currentLvl >= prices.size()) return false;
        int price = prices.get(currentLvl);

        var attr = client.getAttribute(AttributeRegistry.TRAIT_POWER);
        if(attr == null) return false;

        int freeSpace = (int)attr.getValue() - ClientCache.GetPower();
        if(trait.getPowerWeight() > freeSpace) return false;

        return price <= ClientCache.GetSP();
    }
}
