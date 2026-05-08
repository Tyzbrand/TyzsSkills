package com.tyzsskills.impl.client.screen;

import com.mojang.datafixers.util.Either;
import com.tyzsskills.Config;
import com.tyzsskills.Tyzsskills;
import com.tyzsskills.api.Enums;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.client.SoundPlayer;
import com.tyzsskills.impl.client.key.MainKeybind;
import com.tyzsskills.impl.client.models.*;
import com.tyzsskills.impl.client.tools.SortingTools;
import com.tyzsskills.impl.client.tools.StringTools;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class MainGUI extends Screen {
    private static final ResourceLocation background = ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID,
            "textures/gui/background.png");

    private static final ResourceLocation mainFont = ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID,
            "main_font");

    private final int imageWidth = 301;
    private final int imageHeight = 142;

    private int leftPos;
    private int topPos;

    private CustomTabButton allBtn;
    private CustomTabButton abilitiesBtn;
    private CustomTabButton fightBtn;
    private CustomTabButton miscBtn;
    private CustomTabButton bookmarksBtn;


    public MainGUI(){super(Component.translatable("gui.tyzs_skills.title"));}

    private CustomScrollView scrollView;
    private EditBox searchBar;

    @Override
    protected void init(){
        super.init();
        this.leftPos = (this.width - this.imageWidth)/2;
        this.topPos = (this.height - this.imageHeight)/2;

        this.addButtons();
        this.addScrollView();
        this.addSearchBar();

        this.refreshList();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick){
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.blit(background, leftPos, topPos, 0, 0, imageWidth, imageHeight, 325, 325);

        this.renderStrings(guiGraphics, mouseX, mouseY);

        this.renderXpBar(guiGraphics);

        this.renderEntity(guiGraphics, 30, mouseX, mouseY );

        super.render(guiGraphics, mouseX, mouseY, partialTick);

        this.renderBacks(guiGraphics);

        this.renderCustomButtons(guiGraphics, mouseX, mouseY);

        this.renderIcons(guiGraphics, mouseX, mouseY);

        this.renderTooltips(guiGraphics, mouseX, mouseY);
    }


    //helpers
    private void renderStrings(GuiGraphics gui, int mouseX, int mouseY){
        float scale = 0.6f;
        int yOffset = 1;

        int color1 = isHovering(mouseX, mouseY, leftPos, topPos + 81, 37, 12) ? 0xD6AD55 : 0xFFFFFFFF;
        MutableComponent lvlStat = Component.translatable("gui.tyzs_skills.Lvl");
        gui.pose().pushPose();
        gui.pose().translate(leftPos + 6, topPos + 84 + yOffset, 0);
        gui.pose().scale(scale, scale, 1.0f);
        gui.drawString(this.font, lvlStat, 0, 0, color1, false);
        gui.pose().popPose();

        MutableComponent lvlValue = Component.literal(String.valueOf(ClientCache.getLvl()));
        int text1W = this.font.width(lvlValue);
        int rightLimit1 = leftPos + 32;
        gui.pose().pushPose();
        gui.pose().translate(rightLimit1, topPos + 84 + yOffset, 0);
        gui.pose().scale(scale, scale, 1.0f);
        gui.drawString(this.font, lvlValue, -text1W, 0, color1, false);
        gui.pose().popPose();



        int color2 = isHovering(mouseX, mouseY, leftPos + 39, topPos + 81, 37, 12) ? 0xD6AD55 : 0xFFFFFFFF;
        MutableComponent spStat = Component.translatable("gui.tyzs_skills.SP");
        gui.pose().pushPose();
        gui.pose().translate(leftPos + 45, topPos + 84 + yOffset, 0);
        gui.pose().scale(scale, scale, 1.0f);
        gui.drawString(this.font, spStat, 0, 0, color2, false);
        gui.pose().popPose();

        MutableComponent spValue = Component.literal(String.valueOf(StringTools.valueSmartFormat(ClientCache.getSP())));
        int text2W = this.font.width(spValue);
        int rightLimit2 = leftPos + 71;
        gui.pose().pushPose();
        gui.pose().translate(rightLimit2, topPos + 84 + yOffset, 0);
        gui.pose().scale(scale, scale, 1.0f);
        gui.drawString(this.font, spValue, -text2W, 0, color2, false);
        gui.pose().popPose();


        if(SortingTools.getCurrentSkillCategory() == Enums.CategoryType.BOOKMARKS) return;

        String localizationKey = "gui.tyzs_skills.Tab." + SortingTools.getCurrentSkillCategory().toString().toLowerCase();
        MutableComponent enumDisplayName = Component.translatable(localizationKey);
        int text3W = this.font.width(enumDisplayName);
        int rightLimit3 = leftPos+293;
        int textW = font.width(enumDisplayName); int textH = font.lineHeight; int padding = 3;
        renderBackdrop(gui, (rightLimit3 -text3W) - padding, (topPos+12) - padding, textW + (padding*2), textH + (padding*2), 0xD5000000);
        gui.drawString(this.font, enumDisplayName, rightLimit3 -text3W, topPos+13, 0xFFFFFFFF, false);
    }

    private void renderXpBar(GuiGraphics gui){

        float xpStat = ClientCache.getXP();
        float xpGoal = ClientCache.getXpGoal();

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

        if(isHovering(mouseX, mouseY, leftPos, topPos+96, 76, 9)){ //Xp bar
            String xpTooltip = StringTools.valueSmartFormat(ClientCache.getXP()) + "/" + StringTools.valueSmartFormat(ClientCache.getXpGoal()) ;

            MutableComponent finalText = Component.literal(xpTooltip)
                            .append(Component.literal(" [+" + StringTools.valueSmartFormat(ClientCache.getReward()) + " ").withStyle(ChatFormatting.GREEN))
                            .append(Component.translatable("gui.tyzs_skills.SP").withStyle(ChatFormatting.GREEN))
                            .append(Component.literal("]").withStyle(ChatFormatting.GREEN));


            gui.renderTooltip(this.font, finalText, mouseX, mouseY);
        }

        if(this.scrollView != null && this.scrollView.visible && this.scrollView.isMouseOver(mouseX, mouseY)) {
            SkillWidget hoveredWidget = this.scrollView.getHoveredWidget(mouseX, mouseY);

            if (hoveredWidget != null) {
                List<Either<FormattedText, TooltipComponent>> lines = hoveredWidget.getTooltip(mouseX, mouseY);
                    if (!lines.isEmpty()) {
                        gui.renderComponentTooltipFromElements(this.font, lines, mouseX, mouseY, ItemStack.EMPTY);
                    }
            }

        }

        if(isHovering(mouseX, mouseY, leftPos + 56, topPos + 58, 12, 14)){ //Stats
            List<Component> tooltip = new ArrayList<>();

            tooltip.add(Component.empty()
                    .append(Component.translatable("gui.tyzs_skills.stats.all_time_xp").withStyle(ChatFormatting.BLUE))
                    .append(Component.literal(": ").withStyle(ChatFormatting.BLUE))
                    .append(Component.literal(StringTools.valueSmartFormat(ClientCache.getAllTimeXp())).withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(" (" + StringTools.valueSmartFormat(ClientCache.getTotalXpPerHour()))
                            .append(Component.translatable("gui.tyzs_skills.stats.xp_per_hour"))
                            .append(Component.literal(")"))).withStyle(ChatFormatting.DARK_GRAY));



            tooltip.add(Component.empty()
                    .append(Component.translatable("gui.tyzs_skills.stats.session_xp").withStyle(ChatFormatting.BLUE))
                    .append(Component.literal(": ").withStyle(ChatFormatting.BLUE))
                    .append(Component.literal(StringTools.valueSmartFormat(ClientCache.getSessionXp())).withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(" (" + StringTools.valueSmartFormat(ClientCache.getSessionXpPerHour()))
                            .append(Component.translatable("gui.tyzs_skills.stats.xp_per_hour"))
                            .append(Component.literal(")"))).withStyle(ChatFormatting.DARK_GRAY));


            tooltip.add(Component.translatable("gui.tyzs_skills.stats.sp_earned").withStyle(ChatFormatting.BLUE)
                    .append(Component.literal(": "))
                    .append(Component.literal(StringTools.valueSmartFormat(ClientCache.getSpEarned())).withStyle(ChatFormatting.GRAY)));


            tooltip.add(Component.translatable("gui.tyzs_skills.stats.sp_spent").withStyle(ChatFormatting.BLUE)
                    .append(Component.literal(": "))
                    .append(Component.literal(StringTools.valueSmartFormat(ClientCache.getSpSpent())).withStyle(ChatFormatting.GRAY)));


            tooltip.add(Component.translatable("gui.tyzs_skills.stats.skill_unlocked").withStyle(ChatFormatting.BLUE)
                    .append(Component.literal(": "))
                    .append(Component.literal(ClientCache.getUnlockedSkills() + "/" + ClientCache.getSkillCount())
                            .withStyle(ChatFormatting.GRAY)));


            gui.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
        }

        if(isHovering(mouseX, mouseY, leftPos + 56, topPos + 44, 12, 12)){ //Config
            gui.renderTooltip(this.font, Component.translatable("button.tyzs_skills.config_btn"), mouseX, mouseY);
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

        if(isHovering(mouseX, mouseY,leftPos + 251, topPos - 10, 15, 9)){ //Sort direction
            var message = SortingTools.getCurrentSortingDirection() == Enums.SortingDirection.ASCENDING ?
                    Component.translatable("gui.tyzs_skills.sorting_type.ascending")
                    : Component.translatable("gui.tyzs_skills.sorting_type.descending");

            gui.renderTooltip(this.font, message, mouseX, mouseY);
        }

        if(isHovering(mouseX, mouseY,leftPos + 267, topPos - 10, 15, 9)){ //Sort type
            gui.renderTooltip(this.font, Component.translatable(SortingTools.getCurrentSortType().name()), mouseX, mouseY);
        }

        if(isHovering(mouseX, mouseY, leftPos + 101, topPos - 9, 13, 7)){ //Affordable switch
            var state = SortingTools.getShowUnbuyableState();
            var color = state ? ChatFormatting.GREEN : ChatFormatting.RED;
            var message = Component.empty()
                    .append(Component.translatable("gui.tyzs_skills.sorting_switch.show_unaffordable"))
                    .append(Component.literal(" [")
                            .append(Component.literal(String.valueOf(state)).withStyle(color))
                            .append(Component.literal("]")));

            gui.renderTooltip(this.font, message, mouseX, mouseY);
        }

        if(isHovering(mouseX, mouseY, leftPos + 118, topPos - 9, 13, 7)){ //Maxed switch
            var state = SortingTools.getShowMaxedState();
            var color = state ? ChatFormatting.GREEN : ChatFormatting.RED;
            var message = Component.empty()
                    .append(Component.translatable("gui.tyzs_skills.sorting_switch.show_maxed"))
                    .append(Component.literal(" [")
                            .append(Component.literal(String.valueOf(state)).withStyle(color))
                            .append(Component.literal("]")));

            gui.renderTooltip(this.font, message, mouseX, mouseY);
        }

        if(isHovering(mouseX, mouseY, leftPos + 144, topPos - 9,  78, 11) && this.searchBar.getValue().isBlank()){ //Search bar
            List<Component> tooltip = new ArrayList<>();

            tooltip.add(Component.translatable("gui.tuzs_skills.sorting_tooltip.header").withStyle(ChatFormatting.BLUE));
            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("gui.tuzs_skills.sorting_tooltip.body").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.literal("@ ").append(Component.translatable("gui.tuzs_skills.sorting_tooltip.prefix_description"))
                    .withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.DARK_GRAY));
            tooltip.add(Component.literal("# ").append(Component.translatable("gui.tuzs_skills.sorting_tooltip.prefix_id"))
                    .withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.DARK_GRAY));

            gui.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
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
                () -> SortingTools.getCurrentSkillCategory() == Enums.CategoryType.ALL,
                background,
                (b) -> {
                    if(SortingTools.getCurrentSkillCategory() == Enums.CategoryType.ALL){
                        this.scrollView.setScrollAmount(0);
                        return;
                    }

                    SortingTools.SetCategoryType(Enums.CategoryType.ALL);
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
                () -> SortingTools.getCurrentSkillCategory() == Enums.CategoryType.ABILITIES,
                background,
                (b) -> {
                    if(SortingTools.getCurrentSkillCategory() == Enums.CategoryType.ABILITIES){
                        this.scrollView.setScrollAmount(0);
                        return;
                    }

                    SortingTools.SetCategoryType(Enums.CategoryType.ABILITIES);
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
                () -> SortingTools.getCurrentSkillCategory() == Enums.CategoryType.FIGHT,
                background,
                (b) -> {
                    if(SortingTools.getCurrentSkillCategory() == Enums.CategoryType.FIGHT){
                        this.scrollView.setScrollAmount(0);
                        return;
                    }

                    SortingTools.SetCategoryType(Enums.CategoryType.FIGHT);
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
                () -> SortingTools.getCurrentSkillCategory() == Enums.CategoryType.MISC,
                background,
                (b) -> {
                    if(SortingTools.getCurrentSkillCategory() == Enums.CategoryType.MISC){
                        this.scrollView.setScrollAmount(0);
                        return;
                    }

                    SortingTools.SetCategoryType(Enums.CategoryType.MISC);
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
                () -> SortingTools.getCurrentSkillCategory() == Enums.CategoryType.BOOKMARKS,
                background,
                (b) -> {
                    if(SortingTools.getCurrentSkillCategory() == Enums.CategoryType.BOOKMARKS){
                        this.scrollView.setScrollAmount(0);
                        return;
                    }

                    SortingTools.SetCategoryType(Enums.CategoryType.BOOKMARKS);
                    this.refreshList();
                });
        this.addRenderableWidget(this.bookmarksBtn);
    }

    private void renderIcons(GuiGraphics gui, int mouseX, int mouseY){
        var statsU = isHovering(mouseX, mouseY, leftPos + 56, topPos + 58, 12, 14) ? 247 : 232;
        renderIcon(gui, .80f, statsU, 190, 14, 18, leftPos + 59, topPos + 61, 8);

        var gearU = isHovering(mouseX, mouseY, leftPos + 56, topPos + 44, 12, 12) ? 286 : 271;
        renderIcon(gui, .80f, gearU, 192, 14, 14, leftPos + 59, topPos + 46, 8);
    }

    private void renderCustomButtons(GuiGraphics gui, int mouseX, int mouseY){

        //ASCENT - DESCENT
        int currentDirectionV = isHovering(mouseX, mouseY,leftPos + 251, topPos - 10, 15, 9) ? 181 : 170;
        int currentDirectionU = SortingTools.getCurrentSortingDirection() == Enums.SortingDirection.ASCENDING ? 42 : 59;

        gui.blit(background, leftPos + 251, topPos - 10, currentDirectionU, currentDirectionV, 15, 9, 325, 325);

        //SORT TYPE
        gui.blit(background, leftPos + 267, topPos - 10, 76, 170, 15, 9, 325, 325);

        int iconU = 0;
        int iconV = 0;
        var currentSortType = SortingTools.getCurrentSortType();

        if(currentSortType != null){
            if(isHovering(mouseX, mouseY,leftPos + 267, topPos - 10, 15, 9)){
                iconU = currentSortType.uHover();
                iconV = currentSortType.vHover();
            }
            else{
                iconU = currentSortType.u();
                iconV = currentSortType.v();
            }
        }

        renderIcon(gui, .45f, iconU, iconV, 13, 13, leftPos + 268 , topPos - 12, 13);

        //SWITCH UNBUYABLE
        boolean isHoverUnbuyable = isHovering(mouseX, mouseY, leftPos + 101, topPos - 9, 13, 7);
        if(SortingTools.getShowUnbuyableState()){
            if(isHoverUnbuyable) gui.blit(background, leftPos + 100, topPos - 10, 43, 158, 15, 9, 325, 325);
            else gui.blit(background, leftPos + 101, topPos - 9, 44, 150, 13, 7, 325, 325);
        }
        else{
            if(isHoverUnbuyable) gui.blit(background, leftPos + 100, topPos - 10, 58, 158, 15, 9, 325, 325);
            else gui.blit(background, leftPos + 101, topPos - 9, 59, 150, 13, 7, 325, 325);
        }

        //SWITCH MAXED
        boolean isHoverMaxed = isHovering(mouseX, mouseY, leftPos + 118, topPos - 9, 13, 7);
        if(SortingTools.getShowMaxedState()){
            if(isHoverMaxed) gui.blit(background, leftPos + 117, topPos - 10, 43, 158, 15, 9, 325, 325);
            else gui.blit(background, leftPos + 118, topPos - 9, 44, 150, 13, 7, 325, 325);
        }
        else{
            if(isHoverMaxed) gui.blit(background, leftPos + 117, topPos - 10, 58, 158, 15, 9, 325, 325);
            else gui.blit(background, leftPos + 118, topPos - 9, 59, 150, 13, 7, 325, 325);
        }
    }

    private void renderBacks(GuiGraphics gui){
        gui.blit(background, leftPos + 97, topPos - 17, 83, 256, 189, 17, 325, 325);
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

    private void addSearchBar(){
        this.searchBar = new EditBox(this.font, leftPos + 144, topPos - 9,  78, 11, Component.literal("Search"));

        this.searchBar.setBordered(false);
        this.searchBar.setTextColor(0xFFFFFF);

        if(Config.KEEP_SEARCH_QUERY.getAsBoolean()) this.searchBar.setValue(SortingTools.getCurrentSearchQuery());

        this.searchBar.setResponder((s) -> {
            SortingTools.setSearchQuery(s);
            this.refreshList();
        });

        this.addRenderableWidget(this.searchBar);
    }


    //Actifs
    public void refreshList(){
        if(this.scrollView == null) return;
        scrollView.clearEntries();

        SortingTools.refreshList();

        int maxPerLine = 3;

        SkillEntry currentRow = null;
        int countInRow = 0;

        for(var skill : SortingTools.getCurrentSkillOrder()){
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
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }


    private void renderIcon(GuiGraphics gui, float scale, int u, int v, int w, int h, int btnX, int btnY, int btnS){

        float scaledSize = w * scale;
        float offset = (btnS - scaledSize) / 2f;

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
    public boolean mouseClicked(double mouseX, double mouseY, int button){
        int mX = (int) mouseX;
        int mY = (int) mouseY;

        //Config Button
        if(button == 0 && isHovering(mX, mY, leftPos + 56, topPos + 44, 11, 11)){
            var container = ModList.get().getModContainerById(Tyzsskills.MODID).orElseThrow();
            container.getCustomExtension(IConfigScreenFactory.class).ifPresent(factory -> {
                if(this.minecraft != null) this.minecraft.setScreen(factory.createScreen(container, this));
            });

            var player = Minecraft.getInstance().player;
            if(player != null) SoundPlayer.PlayUIClick();

            return true;
        }

        //Sort direction Button
        if(button == 0 && isHovering(mX, mY, leftPos + 251, topPos - 10, 15, 8)){
            SortingTools.CycleSortDirection();
            this.refreshList();

            var player = Minecraft.getInstance().player;
            if(player != null) SoundPlayer.PlayUIClick();
        }

        //Sort type Button
        if(button == 0 && isHovering(mX, mY,leftPos + 267, topPos - 10, 15, 8)){
            SortingTools.CycleSortType();
            this.refreshList();

            var player = Minecraft.getInstance().player;
            if(player != null) SoundPlayer.PlayUIClick();
        }

        //Sort switch unbuyable
        if(button == 0 && isHovering(mX, mY,leftPos + 101, topPos - 9, 13, 7)){
            SortingTools.toggleShowUnbuyable();
            this.refreshList();

            var player = Minecraft.getInstance().player;
            if(player != null) SoundPlayer.PlayUIClick();
        }

        //Sort switch maxed
        if(button == 0 && isHovering(mX, mY,leftPos + 118, topPos - 9, 13, 7)){
            SortingTools.toggleShowMaxed();
            this.refreshList();

            var player = Minecraft.getInstance().player;
            if(player != null) SoundPlayer.PlayUIClick();
        }


        if(button == 1){
            if(this.searchBar.isFocused()) this.searchBar.setFocused(false);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

        if((keyCode == GLFW.GLFW_KEY_ESCAPE) && this.searchBar.isFocused()){ //Unfocus search bar
            this.searchBar.setFocused(false);
            return true;
        }

        if (this.searchBar.isFocused()) { //Allow I and some other "button" to be used by the search bar
            return this.searchBar.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
        }

        if(keyCode == GLFW.GLFW_KEY_HOME){
            this.scrollView.setScrollAmount(0);
            return true;
        }

        if(keyCode == GLFW.GLFW_KEY_END){
            this.scrollView.setScrollAmount(this.scrollView.getMaxScroll());
            return true;
        }

        if (MainKeybind.OPEN_SKILL_KEY.matches(keyCode, scanCode)) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void removed() {
        if(!Config.KEEP_SEARCH_QUERY.getAsBoolean()) SortingTools.setSearchQuery("");
        super.removed();
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
