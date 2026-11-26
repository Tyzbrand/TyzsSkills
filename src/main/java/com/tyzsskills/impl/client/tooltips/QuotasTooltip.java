package com.tyzsskills.impl.client.tooltips;

import com.tyzsskills.Settings;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.tools.FormatTools;
import com.tyzsskills.impl.client.Styles;
import com.tyzsskills.impl.client.cache.ClientCache;
import com.tyzsskills.integration.ui.models.*;
import com.tyzsskills.integration.ui.records.UIStyles;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class QuotasTooltip extends UITooltip {
    private MutableComponent xpLine;
    private MutableComponent spLine;
    private MutableComponent timerLine;

    private int latestCacheVersion = -1;
    private long lastRemainingSec = -2L;

    private final static UIStyles.ImageStyle progress =
            new UIStyles.ImageStyle(62, 5, 137, 346, Styles.MAIN_TEXTURE);

    private final static UIStyles.ImageStyle progress_back =
            new UIStyles.ImageStyle(62, 5, 137, 341, Styles.MAIN_TEXTURE);

    private final ClientCache cache;

    public QuotasTooltip() {
        cache = ClientCache.get();

        int y = 0;

        //TODO: translation
        var headerMessage = Component.literal("Quotas");
        var header = new UIText(0, y, () -> headerMessage).withScale(1.3f, Enums.ScalePivot.TOP_LEFT);
        addChild(header);

        addChild(new UIText((int)header.getVisualWidth() + 5, y + 3, () -> timerLine));

        //XP
        if(Settings.Server.XP_LIMIT.get() > 0){
            var xpPanel = new UIContainer(0, y += 22);
            xpPanel.addChild(new UIText(1, 0, () -> xpLine).withScale(.87f, Enums.ScalePivot.TOP_LEFT));
            xpPanel.addChild(new UIImage(0, 8, () -> progress_back));
            xpPanel.addChild(new UIProgressBar(0, 8, () -> progress, this::getXpQuotaRatio));
            addChild(xpPanel);
        }

        //SP
        if(Settings.Server.SP_LIMIT.get() > 0) {
            var spPanel = new UIContainer(0, y + 22);
            spPanel.addChild(new UIText(1, 0, () -> spLine).withScale(.87f, Enums.ScalePivot.TOP_LEFT));
            spPanel.addChild(new UIImage(0, 8, () -> progress_back));
            spPanel.addChild(new UIProgressBar(0, 8, () -> progress, this::getSpQuotaRatio));
            addChild(spPanel);
        }

        updateData();
    }

    @Override
    public void updatePosition(int parentX, int parentY) {
        updateData();
        super.updatePosition(parentX, parentY);
    }

    //UPDATES
    private void updateData(){
        cache.METADATA.syncClientQuotas();
        updateTimer();

        var currentCacheVersion = cache.getVersion();
        if(currentCacheVersion == latestCacheVersion) return;
        latestCacheVersion = currentCacheVersion;

        updateLines();
    }

    private void updateLines(){
        //TODO: translation
        if(Settings.Server.XP_LIMIT.get() > 0){
            xpLine = Component.empty()
                    .append(Component.literal("XP- ").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(FormatTools.defaultFloat(getXpQuotaRatio() * 100f) + "%").withStyle(ChatFormatting.DARK_GRAY));
        }


        //TODO: translation
        if(Settings.Server.SP_LIMIT.get() > 0){
            spLine = Component.empty()
                    .append(Component.literal("SP- ").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(FormatTools.defaultFloat(getSpQuotaRatio() * 100f) + "%").withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    private void updateTimer(){
        var sec = cache.METADATA.getRemainingSeconds();
        if (sec == lastRemainingSec) return;
        lastRemainingSec = sec;

        //TODO: translation
        timerLine = Component.literal(FormatTools.timer(sec)).withStyle(ChatFormatting.DARK_GRAY);
    }

    //UTILS
    private float getXpQuotaRatio(){
        float limit = Settings.Server.XP_LIMIT.get();
        return limit <= 0f ? 0f : cache.METADATA.getXpQuota() / limit;
    }
    private float getSpQuotaRatio(){
        int limit = Settings.Server.SP_LIMIT.get();
        return limit <= 0 ? 0f : (float)cache.METADATA.getSpQuota() / limit;
    }
}
