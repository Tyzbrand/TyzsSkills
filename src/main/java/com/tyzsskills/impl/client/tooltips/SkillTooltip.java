package com.tyzsskills.impl.client.tooltips;

import com.tyzsskills.api.Enums;
import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.tools.ColorTools;
import com.tyzsskills.api.tools.FormatTools;
import com.tyzsskills.impl.client.ClientCache;
import com.tyzsskills.impl.client.Styles;
import com.tyzsskills.impl.server.skills.SkillRules;
import com.tyzsskills.integration.ui.models.*;
import com.tyzsskills.integration.ui.records.UIStyles;
import com.tyzsskills.integration.ui.records.UITexture;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;



public class SkillTooltip extends UITooltip {
    private final ClientCache cache;
    private final ISkill skill;

    private int latestCacheVersion = -1;
    private boolean lastShiftState;

    private final UITexture skillIcon;
    private final UIContainer tagContainer;
    private boolean isSkillAvailable;

    private MutableComponent title;
    private MutableComponent currentDescription;
    private MutableComponent currentRequirements;
    private MutableComponent fullBodyText;

    //TODO: translation
    private static final MutableComponent active = Component.literal("active");
    //TODO: translation
    private static final MutableComponent inactive = Component.literal("inactive");

    private static final UIStyles.BackgroundStyle activeStyle =
            new UIStyles.BackgroundStyle(0xFF367536, ColorTools.darken(0xFF367536), 4, 4);

    private static final UIStyles.BackgroundStyle inactiveStyle =
            new UIStyles.BackgroundStyle(0xFF753636, ColorTools.darken(0xFF753636), 4, 4);

    public SkillTooltip(ISkill skill){
        this.skill = skill;

        var icon = ResourceLocation.tryParse(skill.getIcon());
        if(icon == null) icon = Styles.DEFAULT_SKILL_ICON.texture();
        skillIcon = new UITexture(icon, 16);

        tagContainer = new UIContainer(25 ,14);
        this.addChild(tagContainer);

        cache = ClientCache.get();

        updateData();

        var imageScale = 1.1f;
        this.addChild(new UIBackground(0, 0, 20, 20, () -> Styles.DEFAULT_BACKGROUND)
                .withScale(imageScale, Enums.ScalePivot.CENTER));
        this.addChild(new UIImage(2, 2, 16, 16, () -> isSkillAvailable ? skillIcon : Styles.LOCKED_SKILL_ICON)
                .withScale(imageScale, Enums.ScalePivot.CENTER));

        this.addChild(new UIText(23, 1, () -> title));
        this.addChild(new UIText(0, 25, () -> fullBodyText).withWrapWidth(150));
    }

    @Override
    public void updatePosition(int parentX, int parentY) {
        updateData();
        super.updatePosition(parentX, parentY);
    }

    //UPDATES
    private void updateData(){
        var currentCacheVersion = cache.getVersion();
        var currentShiftState = Screen.hasShiftDown();

        var cacheChanged = currentCacheVersion != latestCacheVersion;
        var shiftChanged = currentShiftState != lastShiftState;

        if(!cacheChanged && !shiftChanged) return;

        if(cacheChanged){
            latestCacheVersion = currentCacheVersion;

            var sCtx = cache.getSkillContext(skill.getID());
            var pCtx = cache.getPlayerContext();
            var currentLvl = cache.getSkillLevel(skill.getID());

            isSkillAvailable = SkillRules.isAvailable(sCtx, pCtx);
            title = Component.translatable(skill.getDisplayName()).withStyle(ChatFormatting.DARK_PURPLE);

            updateDescription(currentLvl);
            updateTags();
        }

        lastShiftState = currentShiftState;

        updateRequirements();

        fullBodyText = currentRequirements == null
                ? currentDescription
                : Component.empty().append(currentDescription).append("\n\n").append(currentRequirements);
    }

    //IMPL
    private void updateDescription(int currentLvl){
        var rawDesc = Component.translatable(skill.getDescription()).getString();

        if(skill.getType() == Enums.SkillType.GENERIC){
            var modifiers = skill.getModifiers();
            for(int i = 0; i < modifiers.size(); i++){
                var targetText = i == 0 ? "{value}" : String.format("{value%d}", i + 1);

                if (rawDesc.contains(targetText)) {
                    var value = modifiers.get(i).getValue(currentLvl);
                    rawDesc = rawDesc.replace(targetText, getFormatedValue(value));
                }
            }
        }
        else{
            for(var entry : skill.getValues().entrySet()){
                String targetText = "{" + entry.getKey() + "}";

                if (rawDesc.contains(targetText)) {
                    var value = entry.getValue().getValue(currentLvl);
                    rawDesc = rawDesc.replace(targetText, getFormatedValue(value));
                }
            }
        }
        currentDescription = Component.literal(rawDesc).withStyle(ChatFormatting.GRAY);
    }

    private void updateTags(){
        tagContainer.clear();
        var tags = skill.getTags();
        var scale = .57f;

        var stateText = new UIText(0, 0, () -> cache.isSkillDeactivated(skill.getID()) ? inactive : active)
                .withBackground(() -> cache.isSkillDeactivated(skill.getID()) ? inactiveStyle : activeStyle, () -> true)
                .withScale(scale, Enums.ScalePivot.TOP_LEFT);

        tagContainer.addChild(stateText);

        var startX = Math.round(stateText.getVisualWidth() + 3);
        for (var tag : tags){
            var tagDisplay = Component.translatable(tag.displayName());
            var style = new UIStyles.BackgroundStyle(tag.color(), ColorTools.darken(tag.color()), 4, 4);


            var tagText = new UIText(startX, 0, () -> tagDisplay)
                    .withBackground(() -> style, () -> true)
                    .withScale(scale, Enums.ScalePivot.TOP_LEFT);

            tagContainer.addChild(tagText);
            startX += Math.round(tagText.getVisualWidth()) + 3;
        }
    }

    private void updateRequirements(){
        var requiredLvl = skill.getRequiredLevel();
        var incompatibilities = cache.GRAPH.getIncompatibilitiesFor(skill.getID());
        var prerequisites = cache.GRAPH.getPrerequisitesFor(skill.getID());

        var hasInfos = requiredLvl != 0 || !incompatibilities.isEmpty() || !prerequisites.isEmpty();
        if(!hasInfos) {currentRequirements = null; return;}

        if(!lastShiftState){
            currentRequirements = Component.translatable("gui.tyzs_skills.requirements").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC);
            return;
        }

        var tempComponent = Component.empty();

        if(requiredLvl != 0) {
            var color = requiredLvl <= cache.getLevel() ? ChatFormatting.GREEN : ChatFormatting.RED;
            appendLine(tempComponent, Component.empty()
                    .append(Component.literal("◆").withStyle(ChatFormatting.BLUE))
                    .append(Component.translatable("gui.tyzs_skills.level_lock").withStyle(ChatFormatting.BLUE))
                    .append(Component.literal(": ").withStyle(ChatFormatting.BLUE))
                    .append(Component.literal(String.valueOf(requiredLvl)).withStyle(color)));
        }

        if (!incompatibilities.isEmpty()) {
            appendLine(tempComponent, Component.empty()
                    .append(Component.literal("◆").withStyle(ChatFormatting.BLUE))
                    .append(Component.translatable("gui.tyzs_skills.incompatibilities"))
                    .append(Component.literal(":")).withStyle(ChatFormatting.BLUE));

            for (var conflict : incompatibilities) {
                var conflictSkill = cache.getSkill(conflict);
                if (conflictSkill == null) continue;

                var color = cache.getSkillLevel(conflict) > 0 ? ChatFormatting.RED : ChatFormatting.DARK_GRAY;
                appendLine(tempComponent, Component.empty()
                        .append(Component.literal("- ").withStyle(ChatFormatting.DARK_GRAY))
                        .append(Component.translatable(conflictSkill.getDisplayName()).withStyle(color)));
            }
        }
        if (!prerequisites.isEmpty()) {
            appendLine(tempComponent, Component.empty()
                    .append(Component.literal("◆").withStyle(ChatFormatting.BLUE))
                    .append(Component.translatable("gui.tyzs_skills.prerequisite"))
                    .append(Component.literal(":")).withStyle(ChatFormatting.BLUE));

            for (var prerequisite : prerequisites.entrySet()) {
                var id = prerequisite.getKey();
                var requiredLevel = prerequisite.getValue();
                var prerequisiteSkill = cache.getSkill(id);

                if (prerequisiteSkill == null) continue;

                var color = cache.getSkillLevel(id) < requiredLevel ? ChatFormatting.RED : ChatFormatting.GREEN;
                appendLine(tempComponent, Component.empty()
                        .append(Component.literal("- ").withStyle(ChatFormatting.DARK_GRAY))
                        .append(Component.translatable(prerequisiteSkill.getDisplayName()).withStyle(ChatFormatting.DARK_GRAY))
                        .append(Component.literal(", ").withStyle(ChatFormatting.DARK_GRAY))
                        .append(Component.translatable("gui.tyzs_skills.Lvl").withStyle(ChatFormatting.DARK_GRAY))
                        .append(Component.literal(" " + requiredLevel).withStyle(color)));
            }
        }
        currentRequirements = tempComponent;
    }

    //UTILS
    private String getFormatedValue(float value){
        var color = ChatFormatting.WHITE;
        if(value < 0){color = ChatFormatting.RED;}
        else if (value > 0){color = ChatFormatting.GREEN;}

        return color + FormatTools.bigFloat(value) + ChatFormatting.GRAY;
    }

    private void appendLine(MutableComponent parent, Component line) {
        if (!parent.getSiblings().isEmpty()) parent.append("\n");
        parent.append(line);
    }
}
