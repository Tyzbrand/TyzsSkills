package com.tyzsskills.client.models;

import com.tyzsskills.Config;
import com.tyzsskills.Tyzsskills;
import com.tyzsskills.client.ClientCache;
import com.tyzsskills.server.model.Skill;
import com.tyzsskills.server.payloads.CActionSkillPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.neoforge.network.PacketDistributor;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SkillWidget {

    private static final ResourceLocation REF_TEXTURE = ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "textures/gui/background.png");
    private static final int TEXTURE_W = 325, TEXTURE_H = 325;

    private static final int U_BACKGROUND = 166, V_BACKGROUND = 142;

    public static final int WIDTH = 64, HEIGHT = 30; //Widget Size on screen

    private static final int BTN_W = 9, BTN_H = 9;

    private static final int U_BUY_BTN = 169 ,V_BUY_BTN = 173;
    private static final int U_BUY_BTN_HOVER = 178;


    private static final int U_REFUND_BTN = 200 ,V_REFUND_BTN = 173;
    private static final int U_REFUND_BTN_HOVER = 209;

    private final Skill skill;
    private final ResourceLocation icon;
    private int x, y;

    public SkillWidget(Skill skill){
        this.skill = skill;
        this.icon = ResourceLocation.tryParse(skill.GetIcon());
    }

    public void render(GuiGraphics gui, int x, int y, int mouseX, int mouseY, float partialTick){
        this.x = x;
        this.y = y;

        Font font = Minecraft.getInstance().font;

        gui.blit(REF_TEXTURE, x, y, U_BACKGROUND, V_BACKGROUND, WIDTH, HEIGHT, TEXTURE_W, TEXTURE_H);

        if(icon != null){
            gui.blit(icon, x+7, y+7, 0, 0, 16, 16, 16, 16);
        }

        MutableComponent count =  Component.translatable("gui.tyzs_skills.Lvl")
                .append(": " + ClientCache.GetSkillLevel(skill.GetID()) + "/" + skill.GetMaximumLevel());

        float scale = 0.62f;
        gui.pose().pushPose();
        gui.pose().translate(x+27, y+8, 0);
        gui.pose().scale(scale, scale, 1f);

        gui.drawWordWrap(font, count, 0, 0, (int)(37/scale), 0x666666);
        gui.pose().popPose();

        if(CanBuy(skill)){
            boolean isHoverBuyBtn = isMouseOver(mouseX, mouseY, x+27, y+17, BTN_W, BTN_H);
            int currentBuyU = U_BUY_BTN;
            if(isHoverBuyBtn){currentBuyU = U_BUY_BTN_HOVER;}
            gui.blit(REF_TEXTURE, x+27, y+17, currentBuyU, V_BUY_BTN, BTN_W, BTN_H, TEXTURE_W, TEXTURE_H);
        }
        if(CanRefund(skill)) {
            boolean isHoverRefundBtn = isMouseOver(mouseX, mouseY, x+38, y+17, BTN_W, BTN_H);
            int currentRefundU = U_REFUND_BTN;
            if(isHoverRefundBtn){currentRefundU = U_REFUND_BTN_HOVER;}
            gui.blit(REF_TEXTURE, x+38, y+17, currentRefundU, V_REFUND_BTN, BTN_W, BTN_H, TEXTURE_W, TEXTURE_H);
        }
    }

    public List<Component> getTooltip(int mouseX, int mouseY){
        List<Component> tooltip = new ArrayList<>();

        //Simple TOOLTIP
        if(CanBuy(skill) && isMouseOver(mouseX, mouseY, x+27, y+17, BTN_W, BTN_H)){

            var text = Component.empty()
                    .append(Component.translatable("gui.tyzs_skills.cost").withStyle(ChatFormatting.GRAY))
                    .append(": ")
                    .append(GetPriceString(skill))
                    .append(" ")
                    .append(Component.translatable("gui.tyzs_skills.SP"));

            tooltip.add(text);
            return tooltip;
        }

        if(CanRefund(skill) && isMouseOver(mouseX, mouseY, x+38, y+17, BTN_W, BTN_H)){
            tooltip.add(Component.translatable("gui.tyzs_skills.refund"));
            return tooltip;
        }

        //Plusieurs TOOTLIPS
        /*tooltip.add(Component.translatable(skill.GetDisplayName()));

        MutableComponent rawDescription = Component.translatable(skill.GetDescription());

        Font font = Minecraft.getInstance().font;
        List<FormattedCharSequence> splitLines = font.split(rawDescription, 150);

        for(var line : splitLines){
            tooltip.add(Component.literal(String.valueOf(line)));
        }*/
        return tooltip;
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return isMouseOver(mouseX, mouseY, x, y, WIDTH, HEIGHT);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button){
        if(isMouseOver((int)mouseX, (int)mouseY, x+27, y+17, BTN_W, BTN_H)){
            if(!CanBuy(skill)) return false;
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            ClientCache.PredictBuy(skill);
            PacketDistributor.sendToServer(new CActionSkillPayload(skill.GetID().toLowerCase(), 0));
            return true;
        }

        if(isMouseOver((int)mouseX, (int)mouseY, x+38, y+17, BTN_W, BTN_H)){
            if(!CanRefund(skill)) return false;
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            ClientCache.PredictRefund(skill);
            PacketDistributor.sendToServer(new CActionSkillPayload(skill.GetID().toLowerCase(), 1));
            return true;
        }

        return false;
    }

    private boolean CanBuy(Skill skill){
        LocalPlayer client = Minecraft.getInstance().player;
        if(client == null || skill == null) return false;

        if(!skill.IsPurchasable() || !skill.IsSkillActive()) return false;

        var currentLvl = ClientCache.GetSkillLevel(skill.GetID());
        if(currentLvl >= skill.GetMaximumLevel()) return false;

        var prices = skill.GetPrices();
        if(currentLvl >= prices.size()) return false;
        int price = prices.get(currentLvl);

        return price <= ClientCache.GetSP();
    }

    private boolean CanRefund(Skill skill){
        LocalPlayer client = Minecraft.getInstance().player;
        if(client == null || skill == null) return false;

        if(!ClientCache.GetConfigBool(Config.REFUND_SYSTEM_KEY, false) || !skill.IsSkillActive()) return false;

        var currentLvl = ClientCache.GetSkillLevel(skill.GetID());
        if(currentLvl > skill.GetMaximumLevel() || currentLvl < 1) return false;

        var prices = skill.GetPrices();
        return currentLvl <= prices.size();
    }

    private MutableComponent GetPriceString(Skill skill){
        LocalPlayer client = Minecraft.getInstance().player;
        if(skill == null || !skill.IsPurchasable() || client == null) return Component.translatable("gui.tyzs_skills.error_value");

        var currentLvl = ClientCache.GetSkillLevel(skill.GetID());
        if(currentLvl > skill.GetMaximumLevel()) return Component.translatable("gui.tyzs_skills.error_value");
        if(currentLvl  == skill.GetMaximumLevel()) return  Component.translatable("gui.tyzs_skills.level_max");

        var prices = skill.GetPrices();
        if(currentLvl >= prices.size()) return Component.translatable("gui.tyzs_skills.error_value");

        return Component.literal(String.valueOf(prices.get(currentLvl)));
    }




    private boolean isMouseOver(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
}
