package com.tyzsskills.impl.client.models;

import com.tyzsskills.Config;
import com.tyzsskills.Tyzsskills;
import com.tyzsskills.api.Enums;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.client.SoundPlayer;
import com.tyzsskills.impl.client.screen.MainGUI;
import com.tyzsskills.impl.client.tools.StringTools;
import com.tyzsskills.impl.server.active.AttributeRegistry;
import com.tyzsskills.impl.server.model.Skill;
import com.tyzsskills.impl.server.model.Trait;
import com.tyzsskills.impl.server.payloads.CActionSkillPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.neoforge.network.PacketDistributor;


import java.util.ArrayList;
import java.util.List;

public class SkillWidget {

    protected static final ResourceLocation DEFAULT_ICON = ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "textures/gui/skills/default.png");

    protected static final ResourceLocation REF_TEXTURE = ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "textures/gui/background.png");
    protected static final int TEXTURE_W = 325, TEXTURE_H = 325;

    protected static final int U_BACKGROUND = 166, V_BACKGROUND = 142;
    protected static final int U_BACKGROUND_FINAL = 230;

    protected static final int WIDTH = 64, HEIGHT = 30; //Widget Size on screen

    protected static final int BTN_W = 9, BTN_H = 9;

    protected static final int U_BUY_BTN = 169 ,V_BUY_BTN = 173;
    protected static final int U_BUY_BTN_HOVER = 178;

    protected static final int U_BOOK_BTN_HOVER = 226 ,V_BOOK_BTN_HOVER = 173;
    protected static final int U_BOOK_ACTIVE = 237 , V_BOOK_ACTIVE = 174;
    protected static final int U_BOOK_NEUTRAL = 246;



    protected static final int U_REFUND_BTN = 200 ,V_REFUND_BTN = 173;
    protected static final int U_REFUND_BTN_HOVER = 209;

    protected final Skill skill;
    protected final ResourceLocation icon;
    protected int x, y;

    public SkillWidget(Skill skill){
        this.skill = skill;

        var candidate = ResourceLocation.tryParse(skill.getIcon());
        if(candidate != null && Minecraft.getInstance().getResourceManager().getResource(candidate).isPresent()){
            this.icon = candidate;
        }
        else this.icon = DEFAULT_ICON;
    }

    public void render(GuiGraphics gui, int x, int y, int mouseX, int mouseY, float partialTick){
        this.x = x;
        this.y = y;

        Font font = Minecraft.getInstance().font;

        int currentU = U_BACKGROUND;
        if(ClientCache.GetSkillLevel(skill.getID().toLowerCase()) >= skill.getMaximumLevel()){currentU = U_BACKGROUND_FINAL;}
        gui.blit(REF_TEXTURE, x, y, currentU, V_BACKGROUND, WIDTH, HEIGHT, TEXTURE_W, TEXTURE_H);

        gui.blit(REF_TEXTURE, x+49, y+17, U_BOOK_NEUTRAL, V_BOOK_ACTIVE, 9, 9, TEXTURE_W, TEXTURE_H);

        gui.blit(icon, x+7, y+7, 0, 0, 16, 16, 16, 16);

        MutableComponent count =  Component.translatable("gui.tyzs_skills.Lvl")
                .append(": " + ClientCache.GetSkillLevel(skill.getID()) + "/" + skill.getMaximumLevel());



        float scale = 0.58f;
        int fixedWidth = (int)(30 / scale);

        int textHeight = font.wordWrapHeight(count, fixedWidth);
        int padding = 3;



        gui.pose().pushPose();
        gui.pose().translate(x+29, y+8, 0);
        gui.pose().scale(scale, scale, 1f);

        renderBackdrop(gui, -padding, -padding, fixedWidth + (padding*2), textHeight + (padding*2), COLOR_BG);
        gui.drawWordWrap(font, count, 0, 1, fixedWidth, 0xFFFFFF);

        gui.pose().popPose();

        boolean isHoveringBookBtn = isMouseOver(mouseX, mouseY, x+49, y+17, BTN_W, BTN_H);
        if(isHoveringBookBtn) gui.blit(REF_TEXTURE, x+48, y+16, U_BOOK_BTN_HOVER, V_BOOK_BTN_HOVER, 11, 11, TEXTURE_W, TEXTURE_H);

       if(ClientCache.isSkillBookmarked(skill.getID().toLowerCase())){
            gui.blit(REF_TEXTURE, x+49, y+17, U_BOOK_ACTIVE, V_BOOK_ACTIVE, BTN_W, BTN_H, TEXTURE_W, TEXTURE_H);
        }


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

    public List<Component> getTooltip(int mouseX, int mouseY){
        List<Component> tooltip = new ArrayList<>();
        int currentLevel = ClientCache.GetSkillLevel(this.skill.getID().toLowerCase());
        int lvlToBuy = currentLevel +1;

        if(CanRefund(skill) && isMouseOver(mouseX, mouseY, x+27, y+17, BTN_W, BTN_H)) {
            tooltip.add(Component.translatable("gui.tyzs_skills.refund"));
            return tooltip;
        }

        if(skill.isPurchasable() && isMouseOver(mouseX, mouseY, x+38, y+17, BTN_W, BTN_H)){ //BUY

            tooltip.add(StringTools.getPriceTooltip(skill, lvlToBuy, CanBuy(skill)));
            tooltip.addAll(StringTools.getValuesTooltip(skill, lvlToBuy));
            return tooltip;
        }

        if(isMouseOver(mouseX, mouseY, x+4, y+4, 22, 22)) {
            tooltip.add(Component.translatable(skill.getDisplayName()).withStyle(ChatFormatting.DARK_PURPLE));
            tooltip.addAll(StringTools.getSkillDescription(skill));
            return tooltip;
        }

        return tooltip;
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return isMouseOver(mouseX, mouseY, x, y, WIDTH, HEIGHT);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button){
        if(isMouseOver((int)mouseX, (int)mouseY, x+38, y+17, BTN_W, BTN_H)){
            if(!CanBuy(skill)) return false;
            SoundPlayer.PlayUIClick();
            ClientCache.PredictBuy(skill);
            PacketDistributor.sendToServer(new CActionSkillPayload(skill.getID().toLowerCase(), 0));
            return true;
        }

        if(isMouseOver((int)mouseX, (int)mouseY, x+27, y+17, BTN_W, BTN_H)){
            if(!CanRefund(skill)) return false;
            SoundPlayer.PlayUIClick();
            ClientCache.PredictRefund(skill);
            PacketDistributor.sendToServer(new CActionSkillPayload(skill.getID().toLowerCase(), 1));
            return true;
        }

        if(isMouseOver((int)mouseX, (int)mouseY, x+49, y+17, BTN_W, BTN_H)) {
            SoundPlayer.PlayUIClick();
            ClientCache.PredictBookmark(skill);
            PacketDistributor.sendToServer(new CActionSkillPayload(skill.getID().toLowerCase(), 2));

            if (ClientCache.GetCategoryType() == Enums.CategoryType.BOOKMARKS) {
                if (Minecraft.getInstance().screen instanceof MainGUI gui) {
                    gui.refreshList();
                }

                return true;
            }
        }

        return false;
    }

    protected boolean CanBuy(Skill skill){
        LocalPlayer client = Minecraft.getInstance().player;
        if(client == null || skill == null) return false;

        if(!skill.isPurchasable() || !skill.isSkillActive()) return false;

        var currentLvl = ClientCache.GetSkillLevel(skill.getID());
        if(currentLvl >= skill.getMaximumLevel()) return false;

        var prices = skill.getPrices();
        if(currentLvl >= prices.size()) return false;
        int price = prices.get(currentLvl);

        return price <= ClientCache.GetSP();
    }

    protected boolean CanRefund(Skill skill){
        LocalPlayer client = Minecraft.getInstance().player;
        if(client == null || skill == null) return false;

        if(!ClientCache.GetConfigBool(Config.REFUND_SYSTEM_KEY, false) || !skill.isSkillActive()) return false;

        var currentLvl = ClientCache.GetSkillLevel(skill.getID());
        if(currentLvl > skill.getMaximumLevel() || currentLvl < 1) return false;

        if(this.skill.getType() == Enums.SkillType.GENERIC){
            String powerAttrId = AttributeRegistry.TRAIT_POWER.getId().toString();

            for(var modifier : this.skill.getModifiers()){
                if(modifier.attribute().equals(powerAttrId)){
                    var att = client.getAttribute(AttributeRegistry.TRAIT_POWER);
                    if(att == null) return false;

                    int max = (int)att.getValue();
                    int current = ClientCache.GetPower();

                    float currentValue = modifier.getValue(currentLvl);
                    float powerLoss = currentValue;

                    if (currentLvl > 1) {
                        float prevValue = modifier.getValue(currentLvl - 1);
                        powerLoss = currentValue - prevValue;
                    }

                    if(current > (max - (int)powerLoss)) return false;
                }
            }
        }

        var prices = skill.getPrices();
        return currentLvl <= prices.size();
    }

    protected boolean isMouseOver(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    //Utils
    protected static final int COLOR_BG = 0xD5000000;
    protected static final int COLOR_BORDER = 0xFFD6AD55;
    protected void renderBackdrop(GuiGraphics gui, int x, int y, int width, int height, int color) {
        // Fond
        gui.fill(x, y + 1, x + width, y + height - 1, color);
        gui.fill(x + 1, y, x + width - 1, y + 1, color);
        gui.fill(x + 1, y + height - 1, x + width - 1, y + height, color);

        // Bordure
        if(skill instanceof Trait) return;
        if(ClientCache.GetSkillLevel(skill.getID().toLowerCase()) < skill.getMaximumLevel()) return;
        gui.fill(x + 1, y, x + width - 1, y + 1, COLOR_BORDER); // Haut
        gui.fill(x + 1, y + height - 1, x + width - 1, y + height, COLOR_BORDER); // Bas
        gui.fill(x, y + 1, x + 1, y + height - 1, COLOR_BORDER); // Gauche
        gui.fill(x + width - 1, y + 1, x + width, y + height - 1, COLOR_BORDER); // Droite
    }
}
