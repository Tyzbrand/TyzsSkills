package com.tyzsskills.integration.ui.renderers;

import com.tyzsskills.Settings;
import com.tyzsskills.impl.client.overlay.MetadataOverlay;
import com.tyzsskills.impl.client.overlay.SkillOverlay;
import com.tyzsskills.integration.ui.models.UIOverlay;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

public class UIOverlayRenderer implements LayeredDraw.Layer {

    private static final Map<String, UIOverlay> ACTIVE_OVERLAYS = new LinkedHashMap<>();

    @Override
    public void render(@NotNull GuiGraphics gui, @NotNull DeltaTracker deltaTracker) {
        if(ACTIVE_OVERLAYS.isEmpty()) return;

        var instance = Minecraft.getInstance();
        if(instance.player == null || instance.options.hideGui) return;
        if (instance.player.isCreative() && !Settings.Client.OVERLAY_CREATIVE.get()) return;

        ACTIVE_OVERLAYS.values().removeIf(UIOverlay::isExpired);
        if(ACTIVE_OVERLAYS.isEmpty()) return;

        var partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);
        var screenWidth = instance.getWindow().getGuiScaledWidth();
        var screenHeight = instance.getWindow().getGuiScaledHeight();

        for(var overlay : ACTIVE_OVERLAYS.values()){
            if(!overlay.isVisible()) continue;

            var ox = overlay.getOffsetX();
            var oy = overlay.getOffsetY();

            overlay.updatePosition(-ox, -oy);

            var visualW = Math.round(overlay.getVisualWidth());
            var visualH = Math.round(overlay.getVisualHeight());
            var shiftX = Math.round(overlay.getVisualX());
            var shiftY = Math.round(overlay.getVisualY());

            var targetX = switch (overlay.getAnchor()) {
                case TOP_LEFT, BOTTOM_LEFT -> ox;
                case TOP_RIGHT, BOTTOM_RIGHT -> screenWidth - visualW - ox;
                case CENTER -> (screenWidth - visualW) / 2 + ox;
            };
            var targetY = switch (overlay.getAnchor()) {
                case TOP_LEFT, TOP_RIGHT -> oy;
                case BOTTOM_LEFT, BOTTOM_RIGHT -> screenHeight - visualH - oy;
                case CENTER -> (screenHeight - visualH) / 2 + oy;
            };

            overlay.updatePosition(targetX - shiftX - ox, targetY - shiftY - oy);
            overlay.draw(gui, -1, -1, partialTick);
        }
    }

    public static void reloadOverlays(){
        remove(MetadataOverlay.ID);
        remove(SkillOverlay.ID);
    }

    @SuppressWarnings("unchecked")
    public static <T extends UIOverlay> T get(String overlayId, Class<T> type) {
        var overlay = ACTIVE_OVERLAYS.get(overlayId);
        if (overlay != null && !overlay.isExpired() && type.isInstance(overlay)) {
            return (T) overlay;
        }
        return null;
    }


    //Implementation
    public static void add(UIOverlay overlay){
        if(overlay == null) return;
        ACTIVE_OVERLAYS.put(overlay.getId(), overlay);
    }
    public static void remove(String overlayId){ACTIVE_OVERLAYS.remove(overlayId);}
    public static void clear(){ACTIVE_OVERLAYS.clear();}




}
