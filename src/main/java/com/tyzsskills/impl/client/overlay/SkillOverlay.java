package com.tyzsskills.impl.client.overlay;

import com.tyzsskills.Settings;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.integration.ui.models.UIBackground;
import com.tyzsskills.integration.ui.models.UIImage;
import com.tyzsskills.integration.ui.models.UIOverlay;
import com.tyzsskills.integration.ui.models.UIStackOverlay;
import com.tyzsskills.integration.ui.records.UIStyles;
import com.tyzsskills.integration.ui.records.UITexture;

public class SkillOverlay extends UIStackOverlay {
    public static final String ID = "skill_notification";
    private final ISkill skill;

    public SkillOverlay(int offsetX, int offsetY, ISkill skill) {
        super(offsetX, offsetY, -1f, 4);
        this.skill = skill;

        this.withId(ID).withAnchor(Enums.OverlayAnchor.TOP_LEFT);

        addSkill(skill);
    }

    public void addSkill(ISkill skill){
        if(skill == null || !Settings.Client.SKILL_OVERLAY.get()) return;

        var existing = getOverlay(skill.getId(), UIOverlay.class);
        if(existing != null) {
            existing.refresh();
            return;
        }

        var bgColor = Settings.Client.SKILL_BG.getHex();
        var bdColor = Settings.Client.SKILL_BD.getHex();
        var backgroundStyle = new UIStyles.BackgroundStyle(bgColor, Enums.BackgroundType.CUT, 0, 0)
                .withBorder(new UIStyles.BorderStyle(bdColor, Enums.BorderType.OUTER));

        var overlay = new UIOverlay(0, 0, Settings.Client.SKILL_DURATION.get())
                .withId(skill.getId())
                .withAnchor(Enums.OverlayAnchor.TOP_LEFT)
                .withFade(200L, 500L);

        overlay.withScale(Settings.Client.SKILL_SCALE.get(), Enums.ScalePivot.TOP_LEFT);

        var icon = new UITexture(skill.getIcon(), 16, 16);
        overlay.addChild(new UIBackground(0, 0, 24, 24, () -> backgroundStyle));
        overlay.addChild(new UIImage(4, 4, 16, 16, () -> icon));

        this.addOverlay(overlay);
    }
}
