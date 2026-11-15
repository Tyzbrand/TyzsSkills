package com.tyzsskills.integration.ui.models;

import com.tyzsskills.api.Enums;

import java.util.UUID;

public class UIOverlay extends UIContainer{
    protected final long creationTime;
    protected long lastUpdateTime;

    protected final float duration;
    protected long fadeIn = 0L;
    protected long fadeOut = 0L;

    protected Enums.OverlayAnchor anchor = Enums.OverlayAnchor.TOP_LEFT;
    public Enums.OverlayAnchor getAnchor() { return anchor; }

    private String id;
    public String getId(){
        if(id == null) id = UUID.randomUUID().toString();
        return id;
    }

    public long getIdleTime() { return System.currentTimeMillis() - lastUpdateTime; }
    public long getDurationMs() { return (long)(duration * 1000); }
    public long getAge() { return System.currentTimeMillis() - creationTime; }

    //Fluents
    public UIOverlay withAnchor(Enums.OverlayAnchor anchor){this.anchor = anchor; return this;}
    public UIOverlay withId(String overlayId){this.id = overlayId; return this;}
    public UIOverlay withFade(long fadeInMs, long fadeOutMs){
        fadeIn = fadeInMs;
        fadeOut = fadeOutMs;
        return this;
    }

    //Parent
    public UIOverlay(int offsetX, int offsetY, float duration) {
        this(offsetX, offsetY, 0, 0, duration);
    }

    public UIOverlay(int offsetX, int offsetY, int width, int height, float duration) {
        super(offsetX, offsetY, width, height);

        creationTime = System.currentTimeMillis();
        lastUpdateTime = creationTime;

        this.withInteraction(() -> false);
        this.withAlpha(this::computeAlpha);
        this.duration = duration;
    }

    //Implementation
    public void refresh(){lastUpdateTime = System.currentTimeMillis();}
    public boolean isExpired(){
        var durationMs = getDurationMs();
        return durationMs > 0 && getIdleTime() > durationMs;
    }
    protected float computeAlpha() {
        var durationMs = getDurationMs();
        var age = getAge();
        var idle = getIdleTime();

        if (fadeIn > 0 && age < fadeIn) return Math.clamp((float) age / fadeIn, 0f, 1f);
        if (durationMs > 0 && fadeOut > 0 && idle > (durationMs - fadeOut)) return Math.clamp((float) (durationMs - idle) / fadeOut, 0f, 1f);
        return 1f;
    }
}
