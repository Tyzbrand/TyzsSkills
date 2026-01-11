package com.tyzsskills.client.models;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.server.model.Skill;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

import java.awt.*;

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

        MutableComponent count = Component.translatable(skill.GetDisplayName());
        gui.drawWordWrap(font, count, x+27, y+7, 30, 0x000000);


        if(skill.IsPurchasable()) {
            boolean isHoverBuyBtn = isMouseOver(mouseX, mouseY, x+27, y+17, BTN_W, BTN_H);
            boolean isHoverRefundBtn = isMouseOver(mouseX, mouseY, x+38, y+17, BTN_W, BTN_H);

            int currentBuyU = U_BUY_BTN;
            int currentRefundU = U_REFUND_BTN;

            if(isHoverBuyBtn){currentBuyU = U_BUY_BTN_HOVER;}
            if(isHoverRefundBtn){currentRefundU = U_REFUND_BTN_HOVER;}

            gui.blit(REF_TEXTURE, x+27, y+17, currentBuyU, V_BUY_BTN, BTN_W, BTN_H, TEXTURE_W, TEXTURE_H);
            gui.blit(REF_TEXTURE, x+38, y+17, currentRefundU, V_REFUND_BTN, BTN_W, BTN_H, TEXTURE_W, TEXTURE_H);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button){
        if(isMouseOver((int)mouseX, (int)mouseY, x+27, y+17, BTN_W, BTN_H)){
            if(!skill.IsPurchasable()) return false;
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            System.out.println("ACHAT");
            return true;
        }

        if(isMouseOver((int)mouseX, (int)mouseY, x+38, y+17, BTN_W, BTN_H)){
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            System.out.println("REMBOURSEMENT");
            return true;
        }

        return false;
    }

    private boolean isMouseOver(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
}
