package com.tyzsskills.impl.server.debug;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.impl.server.Level.LevelManager;
import com.tyzsskills.impl.server.active.AttributeRegistry;
import com.tyzsskills.impl.server.active.ErrorManager;
import com.tyzsskills.impl.server.effects.GenericEffects;
import com.tyzsskills.impl.server.skills.SkillManager;
import com.tyzsskills.impl.server.skills.SkillRules;
import com.tyzsskills.impl.server.sp.SpManager;
import com.tyzsskills.impl.server.spells.SpellManager;
import com.tyzsskills.impl.server.xp.XpManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@ApiStatus.Internal
public class Repair {
    public static void repair(@NotNull ServerPlayer player){
        repairMetadataInconsistencies(player);

        repairSkillInconsistencies(player);
        repairSpellInconsistencies(player);
    }

    private static void repairMetadataInconsistencies(@NotNull ServerPlayer player){
        LevelManager.checkForLevelUp(player, XpManager.getXP(player));
    }

    private static final int STACK_PROTECTION = 20;
    private static void repairSkillInconsistencies(@NotNull ServerPlayer player) {
        GenericEffects.restoreEffects(player);

        var skillIdsSnapshot = List.copyOf(SkillManager.getPlayerSkillLevels(player).keySet());

        var rollCount = 0;
        var hasChanged = true;

        while (hasChanged && rollCount < STACK_PROTECTION) {
            hasChanged = false;
            rollCount++;

            var playerContext = SkillManager.getPlayerContext(player);

            for (var id : skillIdsSnapshot) {
                var skill = SkillManager.getSkill(id);
                if (skill == null) continue;

                if (skill.isPermanent() && SkillManager.isSkillDeactivated(player, skill))
                    SkillManager.tryToggleSkillDeactivation(player, skill);

                int lvl = SkillManager.getPlayerSkillLevel(player, skill);
                if (lvl <= 0) continue;

                if (skill.getRequiredLevel() > playerContext.playerLevel()) {
                    cleanRefund(0, lvl, player, skill);
                    hasChanged = true;
                    continue;
                }

                var skillContext = SkillManager.getSkillContext(player, skill);

                var incompatibilities = SkillRules.getActiveIncompatibilities(skillContext, playerContext);
                if (!incompatibilities.isEmpty()) {
                    cleanRefund(0, lvl, player, skill);

                    for (var conflict : incompatibilities) {
                        var conflictSkill = SkillManager.getSkill(conflict);
                        if (conflictSkill != null)
                            cleanRefund(0, SkillManager.getPlayerSkillLevel(player, conflictSkill), player, conflictSkill);
                    }
                    hasChanged = true;
                    continue;
                }

                var prerequisites = SkillRules.getActivePrerequisites(skillContext, playerContext);
                if (!prerequisites.isEmpty()) {
                    cleanRefund(0, lvl, player, skill);
                    hasChanged = true;
                    continue;
                }

                if (skill.getMaximumLevel() < lvl) {
                    cleanRefund(skill.getMaximumLevel(), lvl, player, skill);
                    hasChanged = true;
                    continue;
                }
            }

            if (rollCount == STACK_PROTECTION && hasChanged)
                ErrorManager.printDynamicError(player, Component.literal("Infinite loop detected in Skill rules. Check for circular dependencies or contradictory incompatibilities."));

        }
    }

    private static void repairSpellInconsistencies(@NotNull ServerPlayer player){
        var purchasedSpellsSnapshot = List.copyOf(SpellManager.getPlayerSpells(player));
        var purchasedPropertiesSnapshot = Map.copyOf(SpellManager.getPlayerProperties(player));
        var slotsSnapshot = Map.copyOf(SpellManager.getPlayerSlots(player));
        var cooldownsSnapshot = Map.copyOf(SpellManager.getPlayerCooldowns(player));

        //GHOST COOLDOWNS
        for(var kvp : cooldownsSnapshot.entrySet()){
            var cooldown = kvp.getValue();
            var spell = SpellManager.getSpell(kvp.getKey());

            if(spell == null) continue;
            if(cooldown.isCooldownOver(player.server.overworld().getGameTime())) SpellManager.removeCooldown(player, spell);
        }

        //BAD ASSIGNATION
        var attribute = player.getAttribute(AttributeRegistry.MAX_SPELL_SLOTS);
        if (attribute != null) {
            var maxSlots = attribute.getValue();
            for(var kvp : slotsSnapshot.entrySet()){
                int slot = kvp.getKey();
                var spellId = kvp.getValue();

                var spell = SpellManager.getSpell(spellId);
                if(slot <= 0 || slot > maxSlots || spell == null || !SpellManager.isSpellPurchased(player, spell)){
                    SpellManager.tryUnassignSpell(player, slot);
                }
            }
        }

        //PROPERTIES CONSISTENCY
        for(var spellId : purchasedSpellsSnapshot){
            var spell = SpellManager.getSpell(spellId);
            if(spell == null) continue;

            var properties = spell.getProperties();
            for(var property : properties){
                int currentLevel = purchasedPropertiesSnapshot.getOrDefault(property.getFullId(), 0);
                var maxLevel = property.getMaximumLevel();

                if(currentLevel > maxLevel) SpellManager.setPropertiesLevel(player, property, maxLevel);
                else if(!SpellManager.isSpellPurchased(player, spell) || currentLevel < 0) SpellManager.setPropertiesLevel(player, property, 0);
            }
        }
    }


    //Utils
    private static void cleanRefund(int targetLvl, int currentLvl, @NotNull ServerPlayer player, @NotNull ISkill skill) {
        if (currentLvl == targetLvl) return;

        var spToRefund = 0;
        for (int i = currentLvl - 1; i >= targetLvl; i--) {
            if (i >= skill.getPrices().size()) continue;
            spToRefund += skill.getPrices().get(i);
        }

        SkillManager.setSkillLevel(player, skill, targetLvl);

        if (spToRefund <= 0) return;
        SpManager.tryAddSp(player, spToRefund);
        player.sendSystemMessage(Component.literal("Skill §9[" + skill.getId() + "] §rrules changed. §6" + spToRefund + " §rSP refunded."));
    }
}
