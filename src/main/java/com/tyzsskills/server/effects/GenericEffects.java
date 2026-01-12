package com.tyzsskills.server.effects;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.server.skills.SkillManager;
import com.tyzsskills.server.model.Skill;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.Optional;


public class GenericEffects {
    public static void ApplyEffect(Skill skill, ServerPlayer player){

        ResourceLocation attributeID = ResourceLocation.tryParse(skill.GetModifier());
        if(attributeID == null) return;

        Attribute attribute = BuiltInRegistries.ATTRIBUTE.get(attributeID);

        if(attribute == null) {return;}

        Optional<Holder.Reference<Attribute>> attributeHolderOpt = BuiltInRegistries.ATTRIBUTE.getHolder(attributeID);
        if(attributeHolderOpt.isEmpty()) return;

        AttributeInstance instance = player.getAttribute(attributeHolderOpt.get());
        if(instance == null) return;

        ResourceLocation modifierID = ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "skill_modifier_" + skill.GetID());

        int currentLvl = player.getPersistentData().getInt(skill.GetID() + "_lvl");
        if(currentLvl <= 0) return;

        int index = Math.min(currentLvl - 1, skill.GetValues().size() - 1);
        float value = skill.GetValues().get(index);
        if(skill.GetModifierOperation() == AttributeModifier.Operation.ADD_MULTIPLIED_BASE ||
                skill.GetModifierOperation() == AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL){
            value /= 100f;
        }

        AttributeModifier modifier = new AttributeModifier(
                modifierID, value, skill.GetModifierOperation()
        );

        if (instance.hasModifier(modifierID)) {instance.removeModifier(modifierID);}

        instance.addPermanentModifier(modifier);
    }

    public static void RemoveEffect(Skill skill, ServerPlayer player){
        ResourceLocation attributeID = ResourceLocation.parse(skill.GetModifier());
        Attribute attribute = BuiltInRegistries.ATTRIBUTE.get(attributeID);

        if(attribute == null) {return;}

        Optional<Holder.Reference<Attribute>> attributeHolderOpt = BuiltInRegistries.ATTRIBUTE.getHolder(attributeID);
        if(attributeHolderOpt.isEmpty()) return;

        AttributeInstance instance = player.getAttribute(attributeHolderOpt.get());
        if(instance == null) return;

        ResourceLocation modifierID = ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "skill_modifier_" + skill.GetID());

        instance.removeModifier(modifierID);
    }

    public static void RestaureEffects(ServerPlayer newPlayer){
        for(var skill : SkillManager.Get().GetAllSkills()){

            if(skill.GetType() != Skill.SkillType.GENERIC){continue;}

            String key = skill.GetID() + "_lvl";

            int value = newPlayer.getPersistentData().getInt(key);
            if(value <= 0) continue;

            ApplyEffect(skill, newPlayer);
        }
    }
}
