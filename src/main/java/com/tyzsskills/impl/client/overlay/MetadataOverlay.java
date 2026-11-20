package com.tyzsskills.impl.client.overlay;

import com.tyzsskills.Settings;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.tools.FormatTools;
import com.tyzsskills.integration.ui.models.UIOverlay;
import com.tyzsskills.integration.ui.models.UIStackOverlay;
import com.tyzsskills.integration.ui.models.UIText;
import com.tyzsskills.integration.ui.records.UIStyles;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class MetadataOverlay extends UIStackOverlay {
    public static final String ID = "metadata_notification";

    public MetadataOverlay(int offsetX, int offsetY) {
        super(offsetX, offsetY, -1f, 2);
        this.withId(ID).withAnchor(Enums.OverlayAnchor.BOTTOM_LEFT);
        this.withScale(Settings.Stock.META_SCALE.get(), Enums.ScalePivot.BOTTOM_LEFT);
    }

    //region XP OVERLAY
    private float currentXpGains;
    private MutableComponent xpText;
    private static final String XP_ID = "xp_overlay";

    public void addXp(float gain){
        if(gain <= 0f || !Settings.Stock.XP_OVERLAY.get()) return;

        var existing = getOverlay(XP_ID, UIOverlay.class);
        if(existing != null) {
            currentXpGains += gain;
            updateXpText();
            existing.refresh();
            return;
        }

        currentXpGains = gain;
        updateXpText();

        var bgColor = Settings.Stock.XP_BG.getHex();
        var bdColor = Settings.Stock.XP_BD.getHex();
        var backgroundStyle = new UIStyles.BackgroundStyle(bgColor, Enums.BackgroundType.CUT)
                .withBorder(new UIStyles.BorderStyle(bdColor, Enums.BorderType.OUTER));

        var overlay = new UIOverlay(0, 0, Settings.Stock.XP_DURATION.get())
                .withId(XP_ID)
                .withAnchor(Enums.OverlayAnchor.BOTTOM_LEFT)
                .withFade(200L, 500L);

        overlay.addChild(new UIText(0, 0, () -> xpText)
                .withBackground(() -> backgroundStyle));

        this.addOverlay(overlay);
    }

    private void updateXpText(){
        xpText = Component.empty()
                .append(Component.literal("+" + FormatTools.defaultFloat(currentXpGains) + " "))
                .append(Component.translatable("gui.tyzs_skills.xp").getString().toLowerCase());
    }
    //endregion

    //region LEVEL OVERLAY
    private int currentSpGains;
    private MutableComponent levelText;
    private static final String LEVEL_ID = "level_overlay";

    public void addLevel(int currentLvl, int spGain){
        if(!Settings.Stock.LVL_OVERLAY.get()) return;

        var existing = getOverlay(LEVEL_ID, UIOverlay.class);
        if(existing != null) {
            currentSpGains += spGain;
            updateLevelText(currentLvl);
            existing.refresh();
            return;
        }

        currentSpGains += spGain;
        updateLevelText(currentLvl);

        var bgColor = Settings.Stock.LVL_BG.getHex();
        var bdColor = Settings.Stock.LVL_BD.getHex();
        var backgroundStyle = new UIStyles.BackgroundStyle(bgColor, Enums.BackgroundType.CUT)
                .withBorder(new UIStyles.BorderStyle(bdColor, Enums.BorderType.OUTER));

        var overlay = new UIOverlay(0, 0, Settings.Stock.LVL_DURATION.get())
                .withId(LEVEL_ID)
                .withAnchor(Enums.OverlayAnchor.BOTTOM_LEFT)
                .withFade(200L, 500L);

        overlay.addChild(new UIText(0, 0, () -> levelText)
                .withBackground(() -> backgroundStyle));

        this.addOverlay(overlay);
    }

    private void updateLevelText(int currentLvl){
        var spRaw = Component.translatable("gui.tyzs_skills.SP").getString().toUpperCase();
        levelText = Component.empty()
                .append(Component.translatable("overlay.tyzs_skills.level"))
                .append(Component.literal(" " + currentLvl))
                .append(Component.literal(" [+" + currentSpGains + " " + spRaw + "]").withStyle(ChatFormatting.GREEN));
    }
    //endregion
}
