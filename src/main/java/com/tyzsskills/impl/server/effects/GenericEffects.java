package com.tyzsskills.impl.server.effects;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.api.Enums;
import com.tyzsskills.impl.server.skills.SkillManager;
import com.tyzsskills.impl.server.skills.Skill;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashSet;


@ApiStatus.Internal
public class GenericEffects {
    public static void applyEffects(Skill skill, ServerPlayer player){
        if(skill.getType() != Enums.SkillType.GENERIC && skill.getType() != Enums.SkillType.CUSTOM) return;

        var healthSnapshot = player.getHealth();
        var attributeChanged = false;
        var currentLvl = SkillManager.getPlayerSkillLevel(player, skill.getID());

        var expectedAttributes = new HashSet<>();
        if (currentLvl > 0) {
            for (var modifier : skill.getModifiers()) {
                ResourceLocation id = ResourceLocation.tryParse(modifier.attribute());
                if (id != null) expectedAttributes.add(id);
            }
        }

        for (var instance : player.getAttributes().getSyncableAttributes()) {
            var attributeKey = instance.getAttribute().getKey();
            if(attributeKey == null) continue;

            var attributeID = attributeKey.location();
            var safeAttributeName = attributeID.getPath().replace(".", "_");

            var modifierID = ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "skill_modifier_" + skill.getID() + "_" + safeAttributeName);
            var legacyID = ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "skill_modifier_" + skill.getID());

            if (instance.hasModifier(legacyID)) {
                instance.removeModifier(legacyID);
                attributeChanged = true;
            }

            if (instance.hasModifier(modifierID) && !expectedAttributes.contains(attributeID)) {
                instance.removeModifier(modifierID);
                attributeChanged = true;
            }
        }

            if(currentLvl <= 0){
                if(attributeChanged && healthSnapshot > player.getHealth()) player.setHealth(healthSnapshot);
                return;
            }

            for(var modifier : skill.getModifiers()){
                ResourceLocation attributeID = ResourceLocation.tryParse(modifier.attribute());
                if(attributeID == null) continue;

                var attribute = BuiltInRegistries.ATTRIBUTE.get(attributeID);
                if(attribute == null) continue;

                var attributeHolderOpt = BuiltInRegistries.ATTRIBUTE.getHolder(attributeID);
                if(attributeHolderOpt.isEmpty()) continue;

                var instance = player.getAttribute(attributeHolderOpt.get());
                if(instance == null) continue;

                var safeAttributeName = attributeID.getPath().replace(".", "_");
                var modifierID = ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "skill_modifier_" + skill.getID() + "_" + safeAttributeName);

                float expectedValue = modifier.getValue(currentLvl);
                if(modifier.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_BASE ||
                        modifier.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL){
                    expectedValue /= 100f;
                }

                var expectedOperation = modifier.operation();

                if(modifier.attribute().equals("minecraft:generic.oxygen_bonus")){
                    expectedOperation = AttributeModifier.Operation.ADD_VALUE;
                    expectedValue += .75f;
                }

                if (instance.hasModifier(modifierID)) {
                    AttributeModifier existing = instance.getModifier(modifierID);
                    if (existing != null && existing.amount() == expectedValue && existing.operation() == expectedOperation) {
                        continue;
                    }
                    instance.removeModifier(modifierID);
                }

                var AtModifier = new AttributeModifier(modifierID, expectedValue, expectedOperation);
                instance.addPermanentModifier(AtModifier);
                attributeChanged = true;
            }

            if (attributeChanged && healthSnapshot > player.getHealth()) player.setHealth(Math.min(healthSnapshot, player.getMaxHealth()));

    }


    public static void restoreEffects(ServerPlayer newPlayer){
        for(var skill : SkillManager.getAllSkills()){
            if(skill.getType() != Enums.SkillType.GENERIC && skill.getType() != Enums.SkillType.CUSTOM) continue;
            applyEffects(skill, newPlayer);
        }
    }
}
