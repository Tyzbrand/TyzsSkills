package com.tyzsskills.impl.client.screen;

import com.tyzsskills.Config;
import com.tyzsskills.Tyzsskills;
import com.tyzsskills.api.Enums;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.client.models.*;
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

        refreshList();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick){
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.blit(background, leftPos, topPos, 0, 0, imageWidth, imageHeight, 325, 325);

        this.renderStrings(guiGraphics, mouseX, mouseY);

        this.renderXpBar(guiGraphics);
        if(ClientCache.GetContainerType() == Enums.ContainerType.TRAITS) renderPowerBar(guiGraphics);

        this.renderEntity(guiGraphics, 30, mouseX, mouseY );


        super.render(guiGraphics, mouseX, mouseY, partialTick);

        this.renderIcons(guiGraphics);

        updateButtonsVisibility();

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

        MutableComponent spValue = Component.literal(String.valueOf(ClientCache.GetSP()));
        int text2W = this.font.width(spValue);
        int rightLimit2 = leftPos + 69;
        gui.pose().pushPose();
        gui.pose().translate(rightLimit2, topPos + 84 + yOffset, 0);
        gui.pose().scale(scale, scale, 1.0f);
        gui.drawString(this.font, spValue, -text2W, 0, color2, false);
        gui.pose().popPose();

        if(ClientCache.GetContainerType() == Enums.ContainerType.TRAITS){
            MutableComponent text = Component.translatable("gui.tyzs_skills.traits");
            int textW = font.width(text); int textH = font.lineHeight; int padding = 3;
            renderBackdrop(gui, (leftPos+90) - padding, (topPos+12) - padding, textW + (padding*2), textH + (padding*2), 0xD5000000);
            gui.drawString(this.font, text, leftPos+90 , topPos+13, 0xFFFFFFFF, false);
        }
        else{
            if(ClientCache.GetCategoryType() == Enums.CategoryType.BOOKMARKS) return;
            String localizationKey = "gui.tyzs_skills.Tab." + ClientCache.GetCategoryType().toString().toLowerCase();
            MutableComponent enumDisplayName = Component.translatable(localizationKey);
            int text3W = this.font.width(enumDisplayName);
            int rightLimit3 = leftPos+293;
            int textW = font.width(enumDisplayName); int textH = font.lineHeight; int padding = 3;
            renderBackdrop(gui, (rightLimit3 -text3W) - padding, (topPos+12) - padding, textW + (padding*2), textH + (padding*2), 0xD5000000);
            gui.drawString(this.font, enumDisplayName, rightLimit3 -text3W, topPos+13, 0xFFFFFFFF, false);
        }

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

    private void renderPowerBar(GuiGraphics gui){
        var player = Minecraft.getInstance().player;
        if(player == null) return;

        var attr = player.getAttribute(AttributeRegistry.TRAIT_POWER);
        if (attr == null) return;

        double maxPower = attr.getValue();
        if (maxPower <= 0) maxPower = 1;

        int currentPower = ClientCache.GetPower();

        double ratio = Math.min(1.0, currentPower / maxPower);

        gui.blit(background, leftPos+173, topPos+17, 82, 285, 124, 7, 325, 325);

        boolean isFull = ratio >= 1.0;
        int currentV = isFull ? 297 : 292;
        int widthToDraw = (int)(ratio * 122);

        if (widthToDraw > 0) {
            gui.blit(background, leftPos+174, topPos+18, 83, currentV, widthToDraw, 5, 325, 325);
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
            String xpTooltip = SmartFormat(ClientCache.GetXP()) + "/" + SmartFormat(ClientCache.GetXPGOAL()) ;

            MutableComponent finalText = Component.literal(xpTooltip)
                            .append(Component.literal(" [+" + SmartFormat(ClientCache.GetReward()) + " ").withStyle(ChatFormatting.GREEN))
                            .append(Component.translatable("gui.tyzs_skills.SP").withStyle(ChatFormatting.GREEN))
                            .append(Component.literal("]").withStyle(ChatFormatting.GREEN));


            gui.renderTooltip(this.font, finalText, mouseX, mouseY);
        }

        if(ClientCache.GetContainerType() == Enums.ContainerType.TRAITS
                && isHovering(mouseX, mouseY, leftPos+173, topPos+17, 123, 6)){ //Power Bar
            String powerTooltip = SmartFormat(ClientCache.GetPower()) + "/" + SmartFormat(
                    (float)Minecraft.getInstance().player.getAttribute(AttributeRegistry.TRAIT_POWER).getValue()) ;

            MutableComponent finalText =  Component.translatable("gui.tyzs_skills.power")
                    .append(Component.literal(": "))
                    .append(Component.literal(powerTooltip));
            gui.renderTooltip(this.font, finalText, mouseX, mouseY);
        }


        if(isHovering(mouseX, mouseY, leftPos + 55, topPos + 6, 15, 15)){ //Skills button
            gui.renderTooltip(this.font, Component.translatable("gui.tyzs_skills.Skills"), mouseX, mouseY);
        }


        if (isHovering(mouseX, mouseY, leftPos + 55, topPos + 24, 15, 15)
            && ClientCache.GetConfigBool(Config.TRAIT_SYSTEM_KEY, true)){ //Traits button

            int traitLvl = ClientCache.GetConfigInt(Config.TRAIT_UNLOCK_LEVEL_KEY, 20);
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable("gui.tyzs_skills.traits"));

            if(ClientCache.GetLvl() < traitLvl){
              tooltip.add(Component.translatable("overlay.tyzs_skills.level").append(" " + traitLvl).withStyle(ChatFormatting.RED));
            }

            gui.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
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
                    .append(Component.literal(SmartFormat(ClientCache.GetAllTimeXp())).withStyle(ChatFormatting.GRAY)));

            tooltip.add(Component.translatable("gui.tyzs_skills.stats.session_xp").withStyle(ChatFormatting.BLUE)
                    .append(Component.literal(": "))
                    .append(Component.literal(SmartFormat(ClientCache.GetSessionXp())).withStyle(ChatFormatting.GRAY)));


            tooltip.add(Component.translatable("gui.tyzs_skills.stats.sp_earned").withStyle(ChatFormatting.BLUE)
                    .append(Component.literal(": "))
                    .append(Component.literal(String.valueOf(ClientCache.GetSpEarned())).withStyle(ChatFormatting.GRAY)));


            tooltip.add(Component.translatable("gui.tyzs_skills.stats.sp_spent").withStyle(ChatFormatting.BLUE)
                    .append(Component.literal(": "))
                    .append(Component.literal(String.valueOf(ClientCache.GetSpSpent())).withStyle(ChatFormatting.GRAY)));


            tooltip.add(Component.translatable("gui.tyzs_skills.stats.skill_unlocked").withStyle(ChatFormatting.BLUE)
                    .append(Component.literal(": "))
                    .append(Component.literal(ClientCache.GetUnlockedSkills() + "/" + ClientCache.GetSkillCount())
                            .withStyle(ChatFormatting.GRAY)));

            gui.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
        }

        if(ClientCache.GetContainerType() != Enums.ContainerType.SKILLS) return;

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
         this.skillBtn = new CustomTabButton(
                leftPos + 55, topPos + 6,
                14, 14,
                83, 154,
                97, 154,
                111, 154,
                325, 325,
                () -> ClientCache.GetContainerType() == Enums.ContainerType.SKILLS,
                background,
                (b) -> {
                    ClientCache.SetContainerType(Enums.ContainerType.SKILLS);
                    refreshList();
                });
        this.addRenderableWidget(this.skillBtn);

        this.traitBtn = new CustomTabButton(
                leftPos + 55, topPos + 21,
                14, 14,
                83, 168,
                97, 168,
                111, 168,
                325, 325,
                () -> ClientCache.GetContainerType() == Enums.ContainerType.TRAITS,
                background,
                (b) -> {
                    ClientCache.SetContainerType(Enums.ContainerType.TRAITS);
                    refreshList();
                });
        this.addRenderableWidget(this.traitBtn);

        this.allBtn = new CustomTabButton(
                leftPos + 92, topPos + 7,
                29, 20,
                82, 187,
                82, 227,
                82, 207,
                325, 325,
                () -> ClientCache.GetCategoryType() == Enums.CategoryType.ALL,
                background,
                (b) -> {
                    ClientCache.SetCategoryType(Enums.CategoryType.ALL);
                    this.refreshList();
                });
        this.addRenderableWidget(this.allBtn);

        this.abilitiesBtn = new CustomTabButton(
                leftPos + 123, topPos + 7,
                29, 20,
                140, 187,
                140, 227,
                140, 207,
                325, 325,
                () -> ClientCache.GetCategoryType() == Enums.CategoryType.ABILITIES,
                background,
                (b) -> {
                    ClientCache.SetCategoryType(Enums.CategoryType.ABILITIES);
                    this.refreshList();
                });
        this.addRenderableWidget(this.abilitiesBtn);

        this.fightBtn = new CustomTabButton(
                leftPos + 154, topPos + 7,
                29, 20,
                111, 187,
                111, 227,
                111, 207,
                325, 325,
                () -> ClientCache.GetCategoryType() == Enums.CategoryType.FIGHT,
                background,
                (b) -> {
                    ClientCache.SetCategoryType(Enums.CategoryType.FIGHT);
                    this.refreshList();
                });
        this.addRenderableWidget(this.fightBtn);

        this.miscBtn = new CustomTabButton(
                leftPos + 185, topPos + 7,
                29, 20,
                169, 187,
                169, 227,
                169, 207,
                325, 325,
                () -> ClientCache.GetCategoryType() == Enums.CategoryType.MISC,
                background,
                (b) -> {
                    ClientCache.SetCategoryType(Enums.CategoryType.MISC);
                    this.refreshList();
                });
        this.addRenderableWidget(this.miscBtn);

        this.bookmarksBtn = new CustomTabButton(
                leftPos + 216, topPos + 7,
                29, 20,
                198, 187,
                198, 227,
                198, 207,
                325, 325,
                () -> ClientCache.GetCategoryType() == Enums.CategoryType.BOOKMARKS,
                background,
                (b) -> {
                    ClientCache.SetCategoryType(Enums.CategoryType.BOOKMARKS);
                    this.refreshList();
                });
        this.addRenderableWidget(this.bookmarksBtn);
    }

    private void renderIcons(GuiGraphics gui){
        renderIcon(gui, .65f, 233, 191, 15, 16, leftPos+43, topPos+65, 8);
    }

    private void updateButtonsVisibility() {
        boolean isSkillMode = ClientCache.GetContainerType() == Enums.ContainerType.SKILLS;

        if (this.allBtn != null) this.allBtn.visible = isSkillMode;
        if (this.abilitiesBtn != null) this.abilitiesBtn.visible = isSkillMode;
        if (this.fightBtn != null) this.fightBtn.visible = isSkillMode;
        if (this.miscBtn != null) this.miscBtn.visible = isSkillMode;
        if (this.bookmarksBtn != null) this.bookmarksBtn.visible = isSkillMode;

        if(this.traitBtn != null) this.traitBtn.visible = ClientCache.GetConfigBool(Config.TRAIT_SYSTEM_KEY, true);
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
    public void refreshList(){
        if(this.scrollView == null) return;

        var containerType = ClientCache.GetContainerType();

        scrollView.clearEntries();

        int maxPerLine = 3;

        SkillEntry currentRow = null;
        int countInRow = 0;

        if(containerType == Enums.ContainerType.TRAITS){
            for(Skill skill : ClientCache.GetAllSkills()){
                if(skill.getCategory() != Enums.CategoryType.TRAITS) continue;

                if(currentRow == null || countInRow >= maxPerLine){
                    currentRow = new SkillEntry();
                    this.scrollView.AddEntry(currentRow);
                    countInRow = 0;
                }

                currentRow.addWidget(new TraitWidget(skill));
                countInRow++;
            }
            this.scrollView.setScrollAmount(0);
        }
        else {
            var categoryToLoad = ClientCache.GetCategoryType();
            for(Skill skill : ClientCache.GetAllSkills()){
                if(skill.getCategory() == Enums.CategoryType.TRAITS) continue;
                if(skill.getCategory() != categoryToLoad &&
                        categoryToLoad != Enums.CategoryType.ALL && categoryToLoad != Enums.CategoryType.BOOKMARKS) continue;

                if(categoryToLoad == Enums.CategoryType.BOOKMARKS && !ClientCache.isSkillBookmarked(skill.getID())) continue;

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

    }

    //Uilitaires
    public static String SmartFormat(float value){
        if(value == (long)value){
            return String.format("%d", (long)value);
        }
        else return String.format(Locale.US, "%.1f", value);
    }
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
