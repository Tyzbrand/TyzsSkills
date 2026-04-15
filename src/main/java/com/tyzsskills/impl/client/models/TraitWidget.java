package com.tyzsskills.impl.client.models;

import com.tyzsskills.Config;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.client.screen.MainGUI;
import com.tyzsskills.impl.client.tools.StringTools;
import com.tyzsskills.impl.server.active.AttributeRegistry;
import com.tyzsskills.impl.server.model.Skill;
import com.tyzsskills.impl.server.model.Trait;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

public class TraitWidget extends SkillWidget{

    private static final int U_INACTIVE = 166, V_INACTIVE = 142;
    private static final int U_ACTIVE = 82, V_ACTIVE = 251;

    private final Trait trait;

    public TraitWidget(Skill skill){
        super(skill);
        trait = (Trait)skill;
    }

    @Override
    public void render(GuiGraphics gui, int x, int y, int mouseX, int mouseY, float partialTick) {
        this.x = x;
        this.y = y;

        Font font = Minecraft.getInstance().font;

        boolean isOwned = ClientCache.GetSkillLevel(trait.getID().toLowerCase()) > 0;

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
            int currentLvl = ClientCache.GetSkillLevel(skill.getID());
            if(currentLvl < 0) return  tooltip;

            int initialPrice = skill.getPrices().get(currentLvl - 1);
            int finalPrice = Math.max(1, (int)(initialPrice * (Config.REFUND_PERCENTAGE.get() / 100f)));

            tooltip.add(Component.translatable("gui.tyzs_skills.gain").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(finalPrice + " ").withStyle(ChatFormatting.BLUE))
                    .append(Component.translatable("gui.tyzs_skills.SP").withStyle(ChatFormatting.BLUE)));
            return tooltip;
        }

        if(skill.isPurchasable() && skill.isSkillActive() && isMouseOver(mouseX, mouseY, x+38, y+17, BTN_W, BTN_H)){ //BUY

            int currentLvl = ClientCache.GetSkillLevel(skill.getID());
            if(currentLvl >= skill.getMaximumLevel()) {
                tooltip.add(Component.translatable("gui.tyzs_skills.level_max").withStyle(ChatFormatting.GOLD));
                return tooltip;
            }

            tooltip.add(StringTools.getPriceLine(skill, currentLvl, CanBuy(skill), false));

            LocalPlayer player = Minecraft.getInstance().player;
            if(player != null){
                var attr = player.getAttribute(AttributeRegistry.TRAIT_POWER);
                if(attr != null){
                    int maxPower = (int)attr.getValue();
                    int futurePower = ClientCache.GetPower() + this.trait.getPowerWeight();

                    if(futurePower > maxPower){
                        tooltip.add(Component.translatable("gui.tyzs_skills.power_needed").withStyle(ChatFormatting.RED));
                    }
                }
            }

            MutableComponent powerTrad = Component.translatable("gui.tyzs_skills.power");
            String powerLow = powerTrad.getString().toLowerCase();
            tooltip.add(
                    Component.literal("+")
                            .append(Component.literal(String.valueOf(this.trait.getPowerWeight())))
                            .append(Component.literal(" "))
                            .append(Component.literal(powerLow).withStyle(ChatFormatting.RED)));
            return tooltip;
        }

        if(isMouseOver(mouseX, mouseY, x+4, y+4, 22, 22)){ //DESCRTIPTION
            tooltip.add(Component.translatable(skill.getDisplayName()).withStyle(ChatFormatting.DARK_PURPLE));
            tooltip.addAll(StringTools.getSkillDescription(skill));
            return tooltip;
        }
        return tooltip;
    }

    @Override
    protected boolean CanBuy(Skill skill) {
        LocalPlayer client = Minecraft.getInstance().player;
        if(client == null || skill == null) return false;

        if(!skill.isPurchasable() || !skill.isSkillActive()) return false;

        var currentLvl = ClientCache.GetSkillLevel(skill.getID());
        if(currentLvl >= skill.getMaximumLevel()) return false;

        var prices = skill.getPrices();
        if(currentLvl >= prices.size()) return false;
        int price = prices.get(currentLvl);

        var attr = client.getAttribute(AttributeRegistry.TRAIT_POWER);
        if(attr == null) return false;

        int freeSpace = (int)attr.getValue() - ClientCache.GetPower();
        if(trait.getPowerWeight() > freeSpace) return false;

        return price <= ClientCache.GetSP();
    }
}
