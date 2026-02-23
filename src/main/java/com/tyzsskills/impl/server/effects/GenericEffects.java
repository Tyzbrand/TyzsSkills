package com.tyzsskills.impl.server.effects;

import com.tyzsskills.Tyzsskills;
import com.tyzsskills.api.Enums;
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
    public static void ApplyEffect(Skill skill, ServerPlayer player){

        ResourceLocation attributeID = ResourceLocation.tryParse(skill.getModifier());
        if(attributeID == null) return;

        Attribute attribute = BuiltInRegistries.ATTRIBUTE.get(attributeID);

        if(attribute == null) {return;}

        Optional<Holder.Reference<Attribute>> attributeHolderOpt = BuiltInRegistries.ATTRIBUTE.getHolder(attributeID);
        if(attributeHolderOpt.isEmpty()) return;

        AttributeInstance instance = player.getAttribute(attributeHolderOpt.get());
        if(instance == null) return;

        ResourceLocation modifierID = ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "skill_modifier_" + skill.getID());

        int currentLvl = player.getPersistentData().getInt(skill.getID() + "_lvl");
        if(currentLvl <= 0) return;

        int index = Math.min(currentLvl - 1, skill.getValues().size() - 1);
        float value = skill.getValues().get(index);
        if(skill.getModifierOperation() == AttributeModifier.Operation.ADD_MULTIPLIED_BASE ||
                skill.getModifierOperation() == AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL){
            value /= 100f;
        }

        var operation = skill.getModifierOperation();

        if(skill.getModifier().equals("minecraft:generic.oxygen_bonus")){
            operation = AttributeModifier.Operation.ADD_VALUE;
            value += .75f;
        }

        AttributeModifier modifier = new AttributeModifier(
                modifierID, value, operation);

        if (instance.hasModifier(modifierID)) {instance.removeModifier(modifierID);}

        instance.addPermanentModifier(modifier);
    }

    public static void RemoveEffect(Skill skill, ServerPlayer player){
        ResourceLocation attributeID = ResourceLocation.parse(skill.getModifier());
        Attribute attribute = BuiltInRegistries.ATTRIBUTE.get(attributeID);

        if(attribute == null) {return;}

        Optional<Holder.Reference<Attribute>> attributeHolderOpt = BuiltInRegistries.ATTRIBUTE.getHolder(attributeID);
        if(attributeHolderOpt.isEmpty()) return;

        AttributeInstance instance = player.getAttribute(attributeHolderOpt.get());
        if(instance == null) return;

        ResourceLocation modifierID = ResourceLocation.fromNamespaceAndPath(Tyzsskills.MODID, "skill_modifier_" + skill.getID());

        instance.removeModifier(modifierID);
    }

    public static void RestaureEffects(ServerPlayer newPlayer){
        for(var skill : SkillManager.Get().getAllSkills()){

            if(skill.getType() != Enums.SkillType.GENERIC){continue;}

            String key = skill.getID() + "_lvl";

            int value = newPlayer.getPersistentData().getInt(key);
            if(value <= 0) continue;

            ApplyEffect(skill, newPlayer);
        }
    }
}
