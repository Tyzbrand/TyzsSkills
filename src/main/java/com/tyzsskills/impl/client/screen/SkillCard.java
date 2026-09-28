package com.tyzsskills.impl.client.screen;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.client.ui.UIButton;
import com.tyzsskills.impl.client.ui.UIContainer;
import com.tyzsskills.impl.client.ui.UIImage;
import com.tyzsskills.impl.client.ui.UIStyleRegistries;
import com.tyzsskills.impl.server.skills.SkillRules;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SkillCard {
    protected int x, y;
    public static final int WIDTH = 28, HEIGHT = 28;
    protected final UIContainer skillCard;
    protected final ISkill skill;
    protected final ResourceLocation icon;
    protected final ClientCache cache;

    protected boolean isLocked;

    protected int latestCacheVersion = -1;

    public SkillCard(ISkill skill){
        this.skill = skill;
        this.cache = ClientCache.get();
        this.skillCard = new UIContainer(0, 0, WIDTH, HEIGHT);

        var candidate = ResourceLocation.tryParse(skill.getIcon());
        if(candidate != null && Minecraft.getInstance().getResourceManager().getResource(candidate).isPresent()){
            this.icon = candidate;
        }
        else this.icon = UIStyleRegistries.DEFAULT_SKILL_ICON;

        this.init();
    }

    private void init(){
        skillCard.addChild(new UIButton(0, 0, () -> UIStyleRegistries.CARD, () -> true)
                .withTooltip(() -> Component.translatable(skill.getDisplayName())));

        skillCard.addChild(new UIImage(6, 7, 16, 16, icon, 16));
    }

    public void render(GuiGraphics gui, int x, int y, int mouseX, int mouseY, float partialTick){
        this.x = x;
        this.y = y;

        skillCard.updatePosition(this.x, this.y);
        skillCard.draw(gui, mouseX, mouseY, partialTick);
    }

    public void drawTooltips(GuiGraphics gui, net.minecraft.client.gui.Font font, int mouseX, int mouseY) {
        skillCard.drawTooltips(gui, font, mouseX, mouseY);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button){
        return skillCard.handleClick((int)mouseX, (int)mouseY);
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x && mouseX < x + WIDTH && mouseY >= y && mouseY < y + HEIGHT;
    }

    private void updateData(){
        var currentCacheVersion = ClientCache.get().getVersion();
        if(latestCacheVersion == currentCacheVersion) return;
        else latestCacheVersion = currentCacheVersion;

        var sCtx = cache.getSkillContext(skill.getID());
        var pCtx = cache.getPlayerContext();

        this.isLocked = !SkillRules.isAvailable(sCtx, pCtx);
    }
}
