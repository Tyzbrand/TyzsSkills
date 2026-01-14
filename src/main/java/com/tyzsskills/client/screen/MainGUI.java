package com.tyzsskills.client.screen;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.client.ClientCache;
import com.tyzsskills.client.models.CustomScrollView;
import com.tyzsskills.client.models.CustomTabButton;
import com.tyzsskills.client.models.SkillEntry;
import com.tyzsskills.client.models.SkillWidget;
import com.tyzsskills.server.model.Skill;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

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

    public MainGUI(){super(Component.translatable("gui.tyzs_skills.title"));}

    public enum ContainerType {SKILLS, QUESTS}
    public enum CategoryType {ALL, ABILITIES, FIGHT, MISC, BOOKMARKS}

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

        if (this.scrollView != null) {
            this.scrollView.visible = (ClientCache.GetContainerType() == ContainerType.SKILLS);
        }

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

        int color1 = isHovering(mouseX, mouseY, leftPos, topPos+81, 36, 12 )? 0xD6AD55 : 0x737373;
        MutableComponent lvlStat = Component.translatable("gui.tyzs_skills.Lvl").withStyle(Style.EMPTY.withFont(mainFont));
        gui.drawString(this.font, lvlStat, leftPos+7, topPos+84, color1, false);

        MutableComponent lvlValue = Component.literal(String.valueOf(ClientCache.GetLvl())).withStyle(Style.EMPTY.withFont(mainFont));
        int text1W = this.font.width(lvlValue);
        int rightLimit1 = leftPos+31;
        gui.drawString(this.font, lvlValue, rightLimit1 - text1W, topPos+84, color1, false);


        int color2 = isHovering(mouseX, mouseY, leftPos+37, topPos+81, 36, 12 )? 0xD6AD55 : 0x737373;
        MutableComponent spStat = Component.translatable("gui.tyzs_skills.SP").withStyle(Style.EMPTY.withFont(mainFont));
        gui.drawString(this.font, spStat, leftPos+46, topPos+84, color2, false);

        MutableComponent spValue = Component.literal(String.valueOf(ClientCache.GetSP())).withStyle(Style.EMPTY.withFont(mainFont));
        int text2W = this.font.width(spValue);
        int rightLimit2 = leftPos+69;
        gui.drawString(this.font, spValue, rightLimit2 -text2W, topPos+84, color2, false);

        if(ClientCache.GetCategoryType() == CategoryType.BOOKMARKS) return;
        String localizationKey = "gui.tyzs_skills.Tab." + ClientCache.GetCategoryType().toString().toLowerCase();
        MutableComponent enumDisplayName = Component.translatable(localizationKey);
        int text3W = this.font.width(enumDisplayName);
        int rightLimit3 = leftPos+295;
        gui.drawString(this.font, enumDisplayName, rightLimit3 -text3W, topPos+15, 0x000000, false);
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
            String xpTooltip = SmartFormat(ClientCache.GetXP()) + "/" + SmartFormat(ClientCache.GetXPGOAL()) ;
            gui.renderTooltip(this.font, Component.literal(xpTooltip), mouseX, mouseY);
        }

        if(isHovering(mouseX, mouseY, leftPos + 55, topPos + 6, 15, 15)){ //Skills button
            gui.renderTooltip(this.font, Component.translatable("gui.tyzs_skills.Skills"), mouseX, mouseY);
        }

        if(isHovering(mouseX, mouseY, leftPos + 55, topPos + 24, 15, 15)){ //Quests button
            gui.renderTooltip(this.font, Component.translatable("gui.tyzs_skills.Quests"), mouseX, mouseY);
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

        if(this.scrollView != null && this.scrollView.visible && this.scrollView.isMouseOver(mouseX, mouseY)){
            SkillWidget hoveredWidget = this.scrollView.getHoveredWidget(mouseX, mouseY);

            if(hoveredWidget != null){
                List<Component> lines = hoveredWidget.getTooltip(mouseX, mouseY);
                if(!lines.isEmpty()){
                    gui.renderComponentTooltip(this.font, lines, mouseX, mouseY);
                }
            }
        }
    }

    private void renderIcons(GuiGraphics gui){

    }



    private void addButtons(){
        CustomTabButton skillBtn = new CustomTabButton(
                leftPos + 55, topPos + 6,
                16, 16,
                82, 150,
                114, 150,
                98, 150,
                325, 325,
                () -> ClientCache.GetContainerType() == ContainerType.SKILLS,
                background,
                (b) -> ClientCache.SetContainerType(ContainerType.SKILLS));
        this.addRenderableWidget(skillBtn);

        CustomTabButton questBtn = new CustomTabButton(
                leftPos + 55, topPos + 24,
                16, 16,
                82, 167,
                114, 167,
                98, 167,
                325, 325,
                () -> ClientCache.GetContainerType() == ContainerType.QUESTS,
                background,
                (b) -> ClientCache.SetContainerType(ContainerType.QUESTS));
        this.addRenderableWidget(questBtn);

        CustomTabButton allBtn = new CustomTabButton(
                leftPos + 92, topPos + 7,
                29, 20,
                82, 187,
                82, 227,
                82, 207,
                325, 325,
                () -> ClientCache.GetCategoryType() == CategoryType.ALL,
                background,
                (b) -> {
                    ClientCache.SetCategoryType(CategoryType.ALL);
                    this.refreshList();
                });
        this.addRenderableWidget(allBtn);

        CustomTabButton abilitiesBtn = new CustomTabButton(
                leftPos + 123, topPos + 7,
                29, 20,
                140, 187,
                140, 227,
                140, 207,
                325, 325,
                () -> ClientCache.GetCategoryType() == CategoryType.ABILITIES,
                background,
                (b) -> {
                    ClientCache.SetCategoryType(CategoryType.ABILITIES);
                    this.refreshList();
                });
        this.addRenderableWidget(abilitiesBtn);

        CustomTabButton fightBtn = new CustomTabButton(
                leftPos + 154, topPos + 7,
                29, 20,
                111, 187,
                111, 227,
                111, 207,
                325, 325,
                () -> ClientCache.GetCategoryType() == CategoryType.FIGHT,
                background,
                (b) -> {
                    ClientCache.SetCategoryType(CategoryType.FIGHT);
                    this.refreshList();
                });
        this.addRenderableWidget(fightBtn);

        CustomTabButton miscBtn = new CustomTabButton(
                leftPos + 185, topPos + 7,
                29, 20,
                169, 187,
                169, 227,
                169, 207,
                325, 325,
                () -> ClientCache.GetCategoryType() == CategoryType.MISC,
                background,
                (b) -> {
                    ClientCache.SetCategoryType(CategoryType.MISC);
                    this.refreshList();
                });
        this.addRenderableWidget(miscBtn);

        CustomTabButton bookmarksBtn = new CustomTabButton(
                leftPos + 216, topPos + 7,
                29, 20,
                198, 187,
                198, 227,
                198, 207,
                325, 325,
                () -> ClientCache.GetCategoryType() == CategoryType.BOOKMARKS,
                background,
                (b) -> {
                    ClientCache.SetCategoryType(CategoryType.BOOKMARKS);
                    this.refreshList();
                });
        this.addRenderableWidget(bookmarksBtn);
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

        var categoryToLoad = ClientCache.GetCategoryType();

        scrollView.clearEntries();

        int maxPerLine = 3;

        SkillEntry currentRow = null;
        int countInRow = 0;

        for(Skill skill : ClientCache.GetAllSkills()){
            if(skill.GetCategory() != categoryToLoad &&
            categoryToLoad != CategoryType.ALL && categoryToLoad != CategoryType.BOOKMARKS) continue;

            if(categoryToLoad == CategoryType.BOOKMARKS && !ClientCache.GetBookmarkState(skill.GetID())) continue;

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
