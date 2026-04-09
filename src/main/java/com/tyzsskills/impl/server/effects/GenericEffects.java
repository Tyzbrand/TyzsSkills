package com.tyzsskills.impl.server.effects;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.api.Enums;
import com.tyzsskills.impl.server.attachments.PlayerData;
import com.tyzsskills.impl.server.skills.SkillManager;
import com.tyzsskills.impl.server.model.Skill;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;


@ApiStatus.Internal
public class GenericEffects {
    public static void applyEffects(Skill skill, ServerPlayer player){
        if(skill.getType() != Enums.SkillType.GENERIC && skill.getType() != Enums.SkillType.CUSTOM) return;

        for(var modifier : skill.getModifiers()){
            ResourceLocation attributeID = ResourceLocation.tryParse(modifier.attribute());
            if(attributeID == null) continue;

            Attribute attribute = BuiltInRegistries.ATTRIBUTE.get(attributeID);

            if(attribute == null) continue;

            Optional<Holder.Reference<Attribute>> attributeHolderOpt = BuiltInRegistries.ATTRIBUTE.getHolder(attributeID);
            if(attributeHolderOpt.isEmpty()) continue;

            AttributeInstance instance = player.getAttribute(attributeHolderOpt.get());
            if(instance == null) continue;

            String safeAttributeName = attributeID.getPath().replace(".", "_");
            ResourceLocation modifierID = ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "skill_modifier_" + skill.getID() + "_" + safeAttributeName);

            ResourceLocation legacyID = ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "skill_modifier_" + skill.getID());
            if (instance.hasModifier(legacyID)) {
                instance.removeModifier(legacyID);
            }

            int currentLvl = player.getData(PlayerData.DATA).getSkillLevel(skill.getID());
            if(currentLvl <= 0) continue;

            float value = modifier.getValue(currentLvl);
            if(modifier.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_BASE ||
                    modifier.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL){
                value /= 100f;
            }

            var operation = modifier.operation();

            if(modifier.attribute().equals("minecraft:generic.oxygen_bonus")){
                operation = AttributeModifier.Operation.ADD_VALUE;
                value += .75f;
            }

            AttributeModifier AtModifier = new AttributeModifier(
                    modifierID, value, operation);

            if (instance.hasModifier(modifierID)) {instance.removeModifier(modifierID);}

            instance.addPermanentModifier(AtModifier);
        }

    }

    public static void removeEffects(Skill skill, ServerPlayer player){
        if(skill.getType() != Enums.SkillType.GENERIC && skill.getType() != Enums.SkillType.CUSTOM) return;

        for(var modifier : skill.getModifiers()){
            ResourceLocation attributeID = ResourceLocation.tryParse(modifier.attribute());
            Attribute attribute = BuiltInRegistries.ATTRIBUTE.get(attributeID);

            if(attribute == null || attributeID == null) continue;

            Optional<Holder.Reference<Attribute>> attributeHolderOpt = BuiltInRegistries.ATTRIBUTE.getHolder(attributeID);
            if(attributeHolderOpt.isEmpty()) continue;

            AttributeInstance instance = player.getAttribute(attributeHolderOpt.get());
            if(instance == null) continue;

            String safeAttributeName = attributeID.getPath().replace(".", "_");
            ResourceLocation modifierID = ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "skill_modifier_" + skill.getID() + "_" + safeAttributeName);

            instance.removeModifier(modifierID);

            ResourceLocation legacyID = ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "skill_modifier_" + skill.getID());
            instance.removeModifier(legacyID);
        }
    }

    public static void restoreEffects(ServerPlayer newPlayer){
        for(var skill : SkillManager.get().getAllSkills()){

            if(skill.getType() != Enums.SkillType.GENERIC
                    && skill.getType() != Enums.SkillType.CUSTOM) continue;

            if(newPlayer.getData(PlayerData.DATA).getSkillLevel(skill.getID()) > 0) applyEffects(skill, newPlayer);
        }
    }
}
