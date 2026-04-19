package com.tyzsskills.impl.client.screen;

import com.tyzsskills.Config;
import com.tyzsskills.Tyzsskills;
import com.tyzsskills.api.Enums;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.client.key.MainKeybind;
import com.tyzsskills.impl.client.models.*;
import com.tyzsskills.impl.client.tools.SortTools;
import com.tyzsskills.impl.client.tools.StringTools;
import com.tyzsskills.impl.server.active.AttributeRegistry;
import com.tyzsskills.impl.server.model.Skill;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainGUI extends Screen {
    private static final ResourceLocation background = ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID,
            "textures/gui/background.png");

    private static final ResourceLocation mainFont = ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID,
            "main_font");

    private final int imageWidth = 301;
    private final int imageHeight = 139;

    private int leftPos;
    private int topPos;

    private CustomTabButton skillBtn;
    private CustomTabButton traitBtn;
    private CustomTabButton allBtn;
    private CustomTabButton abilitiesBtn;
    private CustomTabButton fightBtn;
    private CustomTabButton miscBtn;
    private CustomTabButton bookmarksBtn;


    public MainGUI(){super(Component.translatable("gui.tyzs_skills.title"));}

    private CustomScrollView scrollView;

    @Override
    protected void init(){
        super.init();
        this.leftPos = (this.width - this.imageWidth)/2;
        this.topPos = (this.height - this.imageHeight)/2;

        this.addButtons();
        this.addScrollView();

        renderList();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick){
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.blit(background, leftPos, topPos, 0, 0, imageWidth, imageHeight, 325, 325);

        this.renderStrings(guiGraphics, mouseX, mouseY);

        this.renderXpBar(guiGraphics);

        this.renderEntity(guiGraphics, 30, mouseX, mouseY );


        super.render(guiGraphics, mouseX, mouseY, partialTick);

        this.renderIcons(guiGraphics);

        this.renderTooltips(guiGraphics, mouseX, mouseY);
    }


    //helpers
    private void renderStrings(GuiGraphics gui, int mouseX, int mouseY){
        float scale = 0.6f;
        int yOffset = 1;

        int color1 = isHovering(mouseX, mouseY, leftPos, topPos + 81, 36, 12) ? 0xD6AD55 : 0xFFFFFFFF;
        MutableComponent lvlStat = Component.translatable("gui.tyzs_skills.Lvl");
        gui.pose().pushPose();
        gui.pose().translate(leftPos + 7, topPos + 84 + yOffset, 0);
        gui.pose().scale(scale, scale, 1.0f);
        gui.drawString(this.font, lvlStat, 0, 0, color1, false);
        gui.pose().popPose();

        MutableComponent lvlValue = Component.literal(String.valueOf(ClientCache.GetLvl()));
        int text1W = this.font.width(lvlValue);
        int rightLimit1 = leftPos + 31;
        gui.pose().pushPose();
        gui.pose().translate(rightLimit1, topPos + 84 + yOffset, 0);
        gui.pose().scale(scale, scale, 1.0f);
        gui.drawString(this.font, lvlValue, -text1W, 0, color1, false);
        gui.pose().popPose();



        int color2 = isHovering(mouseX, mouseY, leftPos + 37, topPos + 81, 36, 12) ? 0xD6AD55 : 0xFFFFFFFF;
        MutableComponent spStat = Component.translatable("gui.tyzs_skills.SP");
        gui.pose().pushPose();
        gui.pose().translate(leftPos + 46, topPos + 84 + yOffset, 0);
        gui.pose().scale(scale, scale, 1.0f);
        gui.drawString(this.font, spStat, 0, 0, color2, false);
        gui.pose().popPose();

        MutableComponent spValue = Component.literal(String.valueOf(StringTools.valueSmartFormat(ClientCache.GetSP())));
        int text2W = this.font.width(spValue);
        int rightLimit2 = leftPos + 69;
        gui.pose().pushPose();
        gui.pose().translate(rightLimit2, topPos + 84 + yOffset, 0);
        gui.pose().scale(scale, scale, 1.0f);
        gui.drawString(this.font, spValue, -text2W, 0, color2, false);
        gui.pose().popPose();


        if(SortTools.getCurrentSkillCategory() == Enums.CategoryType.BOOKMARKS) return;

        String localizationKey = "gui.tyzs_skills.Tab." + SortTools.getCurrentSkillCategory().toString().toLowerCase();
        MutableComponent enumDisplayName = Component.translatable(localizationKey);
        int text3W = this.font.width(enumDisplayName);
        int rightLimit3 = leftPos+293;
        int textW = font.width(enumDisplayName); int textH = font.lineHeight; int padding = 3;
        renderBackdrop(gui, (rightLimit3 -text3W) - padding, (topPos+12) - padding, textW + (padding*2), textH + (padding*2), 0xD5000000);
        gui.drawString(this.font, enumDisplayName, rightLimit3 -text3W, topPos+13, 0xFFFFFFFF, false);
    }

    private void renderXpBar(GuiGraphics gui){

        float xpStat = ClientCache.GetXP();
        float xpGoal = ClientCache.GetXPGOAL();

        if(xpGoal <= 0) return;

        float ratio = Math.min(1f, xpStat/xpGoal);
        int widthToDraw = (int)(ratio*70);

        if(widthToDraw > 0) {
            gui.blit(background, leftPos + 2, topPos + 98, 82, 142, widthToDraw, 5, 325, 325);
        }
    }


    private void renderEntity(GuiGraphics gui, int scale,  int mouseX, int mouseY){
        LivingEntity player = this.minecraft.player;
        if(player == null) return;

        int renderX = leftPos + 29;
        int renderY = topPos + 72;


        float mouseXOffset = (float)(renderX) - mouseX;
        float mouseYOffset = (float)(renderY - 50) - mouseY;

        float f = (float)Math.atan((double)mouseXOffset / 40f);
        float f1 = (float)Math.atan((double)mouseYOffset / 40f);

        Quaternionf quatF = (new Quaternionf()).rotateZ((float)Math.PI);
        Quaternionf quatF1 = (new Quaternionf()).rotateX(f1 * 20f * (float)Math.PI/180f);
        quatF.mul(quatF1);

        float f2 = player.yBodyRot;
        float f3 = player.getYRot();
        float f4 = player.getXRot();
        float f5 = player.yHeadRotO;
        float f6 = player.yHeadRot;

        player.yBodyRot = 180f + f * 20f;
        player.setYRot(180f + f * 40f);
        player.setXRot(-f1 * 20f);
        player.yHeadRot = player.getYRot();
        player.yHeadRotO = player.getYRot();

        InventoryScreen.renderEntityInInventory(
                gui,
                (float) renderX,
                (float) renderY,
                scale,
                new Vector3f(),
                quatF,
                null,
                player
        );

        player.yBodyRot = f2;
        player.setYRot(f3);
        player.setXRot(f4);
        player.yHeadRot = f5;
        player.yHeadRotO = f6;

    }

    private void renderTooltips(GuiGraphics gui, int mouseX, int mouseY){

        if(isHovering(mouseX, mouseY, leftPos, topPos+96, 75, 8)){ //Xp bar
            String xpTooltip = StringTools.valueSmartFormat(ClientCache.GetXP()) + "/" + StringTools.valueSmartFormat(ClientCache.GetXPGOAL()) ;

            MutableComponent finalText = Component.literal(xpTooltip)
                            .append(Component.literal(" [+" + StringTools.valueSmartFormat(ClientCache.GetReward()) + " ").withStyle(ChatFormatting.GREEN))
                            .append(Component.translatable("gui.tyzs_skills.SP").withStyle(ChatFormatting.GREEN))
                            .append(Component.literal("]").withStyle(ChatFormatting.GREEN));


            gui.renderTooltip(this.font, finalText, mouseX, mouseY);
        }

        if(this.scrollView != null && this.scrollView.visible && this.scrollView.isMouseOver(mouseX, mouseY)){
            SkillWidget hoveredWidget = this.scrollView.getHoveredWidget(mouseX, mouseY);

            if(hoveredWidget != null){
                List<Component> lines = hoveredWidget.getTooltip(mouseX, mouseY);
                if(!lines.isEmpty()){
                    gui.renderComponentTooltip(this.font, lines, mouseX, mouseY);
                }
            }
        }

        if(isHovering(mouseX, mouseY, leftPos+43, topPos+65, 8, 8)){ //Stats
            List<Component> tooltip = new ArrayList<>();

            tooltip.add(Component.translatable("gui.tyzs_skills.stats.all_time_xp").withStyle(ChatFormatting.BLUE)
                    .append(Component.literal(": "))
                    .append(Component.literal(StringTools.valueSmartFormat(ClientCache.GetAllTimeXp())).withStyle(ChatFormatting.GRAY)));

            tooltip.add(Component.translatable("gui.tyzs_skills.stats.session_xp").withStyle(ChatFormatting.BLUE)
                    .append(Component.literal(": "))
                    .append(Component.literal(StringTools.valueSmartFormat(ClientCache.GetSessionXp())).withStyle(ChatFormatting.GRAY)));


            tooltip.add(Component.translatable("gui.tyzs_skills.stats.sp_earned").withStyle(ChatFormatting.BLUE)
                    .append(Component.literal(": "))
                    .append(Component.literal(StringTools.valueSmartFormat(ClientCache.GetSpEarned())).withStyle(ChatFormatting.GRAY)));


            tooltip.add(Component.translatable("gui.tyzs_skills.stats.sp_spent").withStyle(ChatFormatting.BLUE)
                    .append(Component.literal(": "))
                    .append(Component.literal(StringTools.valueSmartFormat(ClientCache.GetSpSpent())).withStyle(ChatFormatting.GRAY)));


            tooltip.add(Component.translatable("gui.tyzs_skills.stats.skill_unlocked").withStyle(ChatFormatting.BLUE)
                    .append(Component.literal(": "))
                    .append(Component.literal(ClientCache.GetUnlockedSkills() + "/" + ClientCache.GetSkillCount())
                            .withStyle(ChatFormatting.GRAY)));

            gui.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
        }


        if(isHovering(mouseX, mouseY, leftPos + 92, topPos + 7, 29, 20)){ //All tab
            gui.renderTooltip(this.font, Component.translatable("gui.tyzs_skills.Tab.all"), mouseX, mouseY);
        }

        if(isHovering(mouseX, mouseY, leftPos + 123, topPos + 7, 29, 20)){ //Abilities tab
            gui.renderTooltip(this.font, Component.translatable("gui.tyzs_skills.Tab.abilities"), mouseX, mouseY);
        }

        if(isHovering(mouseX, mouseY, leftPos + 154, topPos + 7, 29, 20)){ //Fight tab
            gui.renderTooltip(this.font, Component.translatable("gui.tyzs_skills.Tab.fight"), mouseX, mouseY);
        }

        if(isHovering(mouseX, mouseY, leftPos + 185, topPos + 7, 29, 20)){ //Misc tab
            gui.renderTooltip(this.font, Component.translatable("gui.tyzs_skills.Tab.misc"), mouseX, mouseY);
        }

        if(isHovering(mouseX, mouseY, leftPos + 216, topPos + 7, 29, 20)){ //Bookmarks tab
            gui.renderTooltip(this.font, Component.translatable("gui.tyzs_skills.Tab.bookmarks"), mouseX, mouseY);
        }
    }

    private void addButtons(){
        this.allBtn = new CustomTabButton(
                leftPos + 92, topPos + 7,
                29, 20,
                82, 187,
                82, 227,
                82, 207,
                325, 325,
                () -> SortTools.getCurrentSkillCategory() == Enums.CategoryType.ALL,
                background,
                (b) -> {
                    SortTools.SetCategoryType(Enums.CategoryType.ALL);
                    SortTools.refreshList();
                    this.renderList();
                });
        this.addRenderableWidget(this.allBtn);

        this.abilitiesBtn = new CustomTabButton(
                leftPos + 123, topPos + 7,
                29, 20,
                140, 187,
                140, 227,
                140, 207,
                325, 325,
                () -> SortTools.getCurrentSkillCategory() == Enums.CategoryType.ABILITIES,
                background,
                (b) -> {
                    SortTools.SetCategoryType(Enums.CategoryType.ABILITIES);
                    SortTools.refreshList();
                    this.renderList();
                });
        this.addRenderableWidget(this.abilitiesBtn);

        this.fightBtn = new CustomTabButton(
                leftPos + 154, topPos + 7,
                29, 20,
                111, 187,
                111, 227,
                111, 207,
                325, 325,
                () -> SortTools.getCurrentSkillCategory() == Enums.CategoryType.FIGHT,
                background,
                (b) -> {
                    SortTools.SetCategoryType(Enums.CategoryType.FIGHT);
                    SortTools.refreshList();
                    this.renderList();
                });
        this.addRenderableWidget(this.fightBtn);

        this.miscBtn = new CustomTabButton(
                leftPos + 185, topPos + 7,
                29, 20,
                169, 187,
                169, 227,
                169, 207,
                325, 325,
                () -> SortTools.getCurrentSkillCategory() == Enums.CategoryType.MISC,
                background,
                (b) -> {
                    SortTools.SetCategoryType(Enums.CategoryType.MISC);
                    SortTools.refreshList();
                    this.renderList();
                });
        this.addRenderableWidget(this.miscBtn);

        this.bookmarksBtn = new CustomTabButton(
                leftPos + 216, topPos + 7,
                29, 20,
                198, 187,
                198, 227,
                198, 207,
                325, 325,
                () -> SortTools.getCurrentSkillCategory() == Enums.CategoryType.BOOKMARKS,
                background,
                (b) -> {
                    SortTools.SetCategoryType(Enums.CategoryType.BOOKMARKS);
                    SortTools.refreshList();
                    this.renderList();
                });
        this.addRenderableWidget(this.bookmarksBtn);
    }

    private void renderIcons(GuiGraphics gui){
        renderIcon(gui, .65f, 232, 190, 17, 18, leftPos+43, topPos+65, 8);
    }

    private void addScrollView(){
         scrollView = new CustomScrollView(
                this.minecraft,
                leftPos + 83, topPos + 27,
                213, 106, 32,
                background, 325, 325,
                142, 149, 149, 149,
                7, 16);

        this.addRenderableWidget(this.scrollView);
    }


    //Actifs
    public void renderList(){
        if(this.scrollView == null) return;
        scrollView.clearEntries();

        int maxPerLine = 3;

        SkillEntry currentRow = null;
        int countInRow = 0;

        for(var skill : SortTools.getCurrentSkillOrder()){
            if(currentRow == null || countInRow >= maxPerLine){
                currentRow = new SkillEntry();
                this.scrollView.AddEntry(currentRow);
                countInRow = 0;
            }
            currentRow.addWidget(new SkillWidget(skill));
            countInRow++;
        }
        this.scrollView.setScrollAmount(0);
    }

    //Uilitaires

    private boolean isHovering(int mouseX, int mouseY, int x, int y, int width, int height){
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    private void renderIcon(GuiGraphics gui, float scale, int u, int v, int w, int h, int btnX, int btnY, int btnS){

        float scaledSize = w * scale;
        float offset = (btnS -scaledSize) / 2f;

        float targetVisualX = btnX + offset;
        float targetVisualY = btnY + offset;

        int drawX = (int)(targetVisualX / scale);
        int drawY = (int)(targetVisualY / scale);


        gui.pose().pushPose();
        gui.pose().scale(scale, scale, 1.0f);
        gui.setColor(1.0f, 1.0f, 1.0f, 1f);


        gui.blit(background, drawX, drawY, u, v, w, h, 325, 325);

        gui.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        gui.pose().popPose();
    }

    private void renderBackdrop(GuiGraphics gui, int x, int y, int width, int height, int color) {
        gui.fill(x, y + 1, x + width, y + height - 1, color);
        gui.fill(x + 1, y, x + width - 1, y + 1, color);
        gui.fill(x + 1, y + height - 1, x + width - 1, y + height, color);

        gui.fill(x + 1, y, x + width - 1, y + 1, 0xFFFFFFFF); // Haut
        gui.fill(x + 1, y + height - 1, x + width - 1, y + height, 0xFFFFFFFF); // Bas
        gui.fill(x, y + 1, x + 1, y + height - 1, 0xFFFFFFFF); // Gauche
        gui.fill(x + width - 1, y + 1, x + width, y + height - 1, 0xFFFFFFFF);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (MainKeybind.OPEN_SKILL_KEY.matches(keyCode, scanCode)) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }


    //states
    @Override
    protected void renderBlurredBackground(float partialTick){}
    @Override
    public boolean isPauseScreen(){
        return false;
    }
    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

}
