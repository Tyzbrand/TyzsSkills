package com.tyzsskills.integration.ui.models;

import com.tyzsskills.api.Enums;

public class UIStackOverlay extends UIOverlay{
    private int spacing;

    public UIStackOverlay(int offsetX, int offsetY, float duration, int spacing) {
        super(offsetX, offsetY, duration);

        this.spacing = spacing;
    }

    public UIStackOverlay addOverlay(UIOverlay overlay) {
        if (overlay == null) return this;
        children.removeIf(c -> c instanceof UIOverlay o && o.getId().equals(overlay.getId()));
        addChild(overlay);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UIOverlay> T getOverlay(String overlayId, Class<T> type) {
        for (var child : children) {
            if (child instanceof UIOverlay overlay
                    && overlay.getId().equals(overlayId)
                    && !overlay.isExpired()
                    && type.isInstance(overlay)) {
                return (T) overlay;
            }
        }
        return null;
    }

    //Overrides
    @Override
    public boolean isExpired() {
        children.removeIf(c -> c instanceof UIOverlay o && o.isExpired());
        return children.isEmpty() || super.isExpired();
    }
    @Override
    public void updatePosition(int parentX, int parentY) {
        var currentY = 0;

        var bottomAnchored = anchor == Enums.OverlayAnchor.BOTTOM_LEFT || anchor == Enums.OverlayAnchor.BOTTOM_RIGHT;
        var start = bottomAnchored ? children.size() - 1 : 0;

        var end = bottomAnchored ? -1 : children.size();
        var step = bottomAnchored ? -1 : 1;

        for (int i = start; i != end; i += step) {
            var child = children.get(i);
            if (!child.isVisible()) continue;

            child.setOffsets(0, 0);
            child.updatePosition(0, 0);

            var shiftX = Math.round(child.getVisualX());
            var shiftY = Math.round(child.getVisualY());
            var visualH = Math.round(child.getVisualHeight());

            child.setOffsets(-shiftX, currentY - shiftY);
            currentY += visualH + spacing;
        }
        super.updatePosition(parentX, parentY);
    }
}
