package com.tyzsskills.integration.graph_settings;

import com.tyzsskills.Settings;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.tools.ColorTools;
import com.tyzsskills.integration.graph_settings.models.ClientSettingsPanel;
import com.tyzsskills.integration.graph_settings.models.ServerSettingsPanel;
import com.tyzsskills.integration.ui.models.UIBackground;
import com.tyzsskills.integration.ui.models.UIBackgroundButton;
import com.tyzsskills.integration.ui.models.UIContainer;
import com.tyzsskills.integration.ui.models.UIText;
import com.tyzsskills.integration.ui.records.UIStyles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class SettingMenu extends Screen {
    public static final int WIDTH = 300, HEIGHT = 200;
    private int leftPos, topPos;

    private UIContainer mainPanel;
    private UIContainer header;
    private UIContainer body;

    private UIContainer homePanel;
    private ServerSettingsPanel serverPanel;
    private ClientSettingsPanel clientPanel;

    private final Screen parentScreen;

    private Enums.SettingsMode currentMode = Enums.SettingsMode.MAIN;

    //region STYLES
    public static final UIStyles.BackgroundStyle MAIN_STYLE =
            new UIStyles.BackgroundStyle(0xB3000000, Enums.BackgroundType.FULL);

    public static final UIStyles.BackgroundStyle MAIN_STYLE_HOVER =
            MAIN_STYLE.withBorder(new UIStyles.BorderStyle(0xFFFFFFFF, Enums.BorderType.OUTER, .5f));

    public static final UIStyles.BackgroundStyle SECONDARY_STYLE =
            new UIStyles.BackgroundStyle(0x80000000, Enums.BackgroundType.FULL);

    public static final UIStyles.BackgroundStyle SECONDARY_STYLE_HOVER =
            SECONDARY_STYLE.withBorder(new UIStyles.BorderStyle(0xFFFFFFFF, Enums.BorderType.OUTER, .5f));

    private static final UIStyles.BackgroundStyle SERVER_LABEL =
            new UIStyles.BackgroundStyle(0xFFAD4BA3, Enums.BackgroundType.CUT);

    private static final UIStyles.BackgroundStyle CLIENT_LABEL =
            new UIStyles.BackgroundStyle(0xFF4BAD85, Enums.BackgroundType.CUT);

    //TODO: translation
    private static final MutableComponent MAIN_HEADER = Component.literal("Tyz's Skills Settings");

    //TODO: translation
    private static final MutableComponent CLIENT = Component.literal("Client");
    //TODO: translation
    private static final MutableComponent SERVER = Component.literal("Server");

    //TODO: translation
    private static final MutableComponent CLIENT_COLORED = Component.literal("Client").withColor(ColorTools.darken(0xFF4BAD85, .5f));
    //TODO: translation
    private static final MutableComponent SERVER_COLORED = Component.literal("Server").withColor(ColorTools.darken(0xFFAD4BA3, .5f));
    //endregion




    public SettingMenu() {
        //TODO: translation
        super(Component.literal("Settings"));

        this.parentScreen = Minecraft.getInstance().screen;
    }

    @Override
    protected void init() {
        super.init();

        leftPos = (width - WIDTH) / 2;
        topPos = (height - HEIGHT) / 2;

        mainPanel = new UIContainer(0, 0);
        header = new UIContainer(0, 0);
        body = new UIContainer(0, 17);


        homePanel = new UIContainer(0, 0);
        clientPanel = new ClientSettingsPanel(0, 0);
        serverPanel = new ServerSettingsPanel(0, 0);

        mainPanel.updatePosition(leftPos, topPos);
        mainPanel.addChild(header);
        mainPanel.addChild(body);

        body.addChild(homePanel);
        body.addChild(serverPanel);
        body.addChild(clientPanel);

        homePanel.withVisibility(() -> currentMode == Enums.SettingsMode.MAIN);
        clientPanel.withVisibility(() -> currentMode == Enums.SettingsMode.CLIENT);
        serverPanel.withVisibility(() -> currentMode == Enums.SettingsMode.SERVER);

        buildHeader();
        buildHome();
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        super.render(gui, mouseX, mouseY, partialTick);
        mainPanel.draw(gui, mouseX, mouseY, partialTick);
    }

    //IMPLEMENTATION
    private void buildHeader(){
        //BANDEAU
        header.addChild(new UIBackground(0, 0, WIDTH, 15, () -> MAIN_STYLE));

        var mainText = new UIText(3, 3, () -> MAIN_HEADER);
        header.addChild(mainText);

        var labelsX = (int)mainText.getVisualWidth() + mainText.getOffsetX() + 8; var labelsY = 7; var labelsScale = .59f;
        header.addChild(new UIText(labelsX, labelsY, () -> CLIENT_COLORED, false)
                .withBackground(() -> CLIENT_LABEL)
                .withScale(labelsScale, Enums.ScalePivot.TOP_LEFT)
                .withVisibility(() -> currentMode == Enums.SettingsMode.CLIENT));

        header.addChild(new UIText(labelsX, labelsY, () -> SERVER_COLORED, false)
                .withBackground(() -> SERVER_LABEL)
                .withScale(labelsScale, Enums.ScalePivot.TOP_LEFT)
                .withVisibility(() -> currentMode == Enums.SettingsMode.SERVER));
    }
    private void buildHome(){
        var isOp = isOp();
        var width = isOp ? WIDTH / 2 - 2: WIDTH;
        var height = HEIGHT - 17;

        var firstBtn = new UIBackgroundButton(0, 0, width, height, this::handleClientClick, () -> MAIN_STYLE, () -> MAIN_STYLE_HOVER);
        var firstTxtX = (int)(firstBtn.getOffsetX() + firstBtn.getVisualWidth() / 2);
        var firstTxtY = (int)(firstBtn.getOffsetY() + firstBtn.getVisualHeight() / 2);

        homePanel.addChild(firstBtn);
        homePanel.addChild(new UIText(firstTxtX, firstTxtY, () -> CLIENT).withAlignment(Enums.TextAlignment.CENTER));

        if(isOp) {
            var sndBtn = new UIBackgroundButton(width + 4, 0, width, height, this::handleServerClick, () -> MAIN_STYLE, () -> MAIN_STYLE_HOVER);
            var sndTxtX = (int)(sndBtn.getOffsetX() + sndBtn.getVisualWidth() / 2);
            var sndTxtY = (int)(sndBtn.getOffsetY() + sndBtn.getVisualHeight() / 2);

            homePanel.addChild(sndBtn);
            homePanel.addChild(new UIText(sndTxtX, sndTxtY, () -> SERVER).withAlignment(Enums.TextAlignment.CENTER));
        }
    }

    //region EXTRACTED LOGIC
    private boolean isOp(){
        var player = Minecraft.getInstance().player;
        return player != null && player.hasPermissions(Settings.Server.SERVER_SETTINGS_PERM.get().level());
    }

    private boolean handleServerClick(){
        currentMode = Enums.SettingsMode.SERVER;
        return true;
    }

    private boolean handleClientClick(){
        currentMode = Enums.SettingsMode.CLIENT;
        return true;
    }
    //endregion

    //REACT
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button){
        int mX = (int) mouseX;
        int mY = (int) mouseY;

        if(button != 0) return false;
        if(mainPanel.handleClick(mX, mY)) return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE && currentMode != Enums.SettingsMode.MAIN) {
            currentMode = Enums.SettingsMode.MAIN;
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
