package com.tyzsskills.impl.client.screen;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.impl.client.active.SortingManager;
import com.tyzsskills.integration.ui.models.UIButton;
import com.tyzsskills.integration.ui.models.UIContainer;
import com.tyzsskills.integration.ui.models.UIImage;
import com.tyzsskills.impl.client.Styles;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SkillCard {

    public static UIContainer getCard(ISkill skill, int x, int y){
        ResourceLocation icon;
        var container = new UIContainer(x, y, 28, 28);

        var candidate = ResourceLocation.tryParse(skill.getIcon());
        if(candidate != null && Minecraft.getInstance().getResourceManager().getResource(candidate).isPresent()){
            icon = candidate;
        }
        else icon = Styles.DEFAULT_SKILL_ICON;

        container.addChild(new UIButton(0, 0,
                () -> Styles.CARD,
                () -> {
                    SortingManager.focusSkill(skill);
                    return true;
                })
                .withTooltip(() -> Component.translatable(skill.getDisplayName())));

        container.addChild(new UIImage(6, 7, 16, 16, icon, 16));

        return container;
    }
}
