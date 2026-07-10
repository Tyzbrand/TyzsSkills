package com.tyzsskills.impl.server.skills;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.JsonOps;
import com.tyzsskills.Constants;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.model.SkillConfiguration;
import com.tyzsskills.impl.server.active.ErrorManager;
import com.tyzsskills.api.records.Modifier;
import com.tyzsskills.api.records.ValueSet;
import com.tyzsskills.impl.server.tools.JsonLoadTools;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@ApiStatus.Internal
public class SkillLoader {

    private final static Map<String, JsonObject> skillQueue = new HashMap<>();

    public static void preLoadSkill(JsonObject source, @NotNull String filename, boolean isDefault){
        if(source == null) {
            ErrorManager.registerSkillError(filename, "file is empty");
            return;
        }

        var id = JsonLoadTools.getSafeElement(source, "id", JsonPrimitive::getAsString);
        if(id == null || id.isBlank()) {ErrorManager.registerSkillError(filename, "invalid id"); return;}
        id = id.toLowerCase();

        applyTransitions(source, id);

        if(isDefault) skillQueue.putIfAbsent(id, source);
        else{
            var type = JsonLoadTools.getSafeElement(source, "type", JsonPrimitive::getAsString);
            boolean isCustom = type != null && type.equalsIgnoreCase(Enums.SkillType.CUSTOM.name());

            if(isCustom) {
                skillQueue.put(id, source);
                return;
            }

            if(skillQueue.containsKey(id)) skillQueue.put(id, source);
            else  ErrorManager.registerSkillError(filename, "unable to find the skill to overwrite");
        }
    }

    public static void finalizePreLoading() {
        for (var kvp : skillQueue.entrySet()) loadSkill(kvp.getKey(), kvp.getValue());
        skillQueue.clear();

        for(var skill : SkillManager.getAllSkills()){
            var incompatibilities = skill.getRawIncompatibilities();
            var skillID = skill.getID();

            for (var id : incompatibilities){
                if(!SkillManager.isSkillLoaded(id)){
                    ErrorManager.registerSkillError(skillID, "incompatibility : [" + id + "] does not exist.");
                    skill.removeIncompatibility(id);
                    continue;
                }

                var conflict = SkillManager.getSkill(id);
                if(conflict == null) continue;

                if(!conflict.isSkillIncompatible(skillID)) conflict.addIncompatibility(skillID);
            }

            var prerequisites = skill.getRawPrerequisites();
            for (var prerequisite : prerequisites.keySet()){
                if(!SkillManager.isSkillLoaded(prerequisite)) {
                    ErrorManager.registerSkillError(skillID, "prerequisite : [" + prerequisite + "] does not exist.");
                    skill.removePrerequisite(prerequisite);
                }
            }
        }
    }

    private static void loadSkill(String id, JsonObject source){
        Boolean state = JsonLoadTools.getSafeElement(source, "active", JsonPrimitive::getAsBoolean);
        if(state == null) state = true;
        if(!state) return; //Les skills désactivés ne sont pas chargés

        Integer maxLevel = JsonLoadTools.getSafeElement(source, "maximumLevel", JsonPrimitive::getAsInt);
        if(maxLevel == null) maxLevel = 1;
        maxLevel = Math.clamp(maxLevel, 1, Constants.SKILL_MAX_LEVEL);

        List<Integer> prices = JsonLoadTools.getSafeList(source, "prices", JsonElement::getAsInt);
        if(prices == null) {ErrorManager.registerSkillError(id, "invalid price list"); return;}
        if(maxLevel > prices.size()) {
            ErrorManager.registerSkillError(id, String.format("price set is too small, current : %d , expected : %d", prices.size(), maxLevel));
            return;
        }

        Enums.SkillType type = JsonLoadTools.getSafeEnum(source, "type", Enums.SkillType.class);
        if(type == null) {ErrorManager.registerSkillError(id, "invalid skill type"); return;}

        String category = JsonLoadTools.getSafeElement(source, "category", JsonPrimitive::getAsString);
        if(category == null) category = "";

        String icon = JsonLoadTools.getSafeElement(source, "icon", JsonPrimitive::getAsString);
        if(icon == null) icon = "tyzs_skills:textures/gui/skills/default.png";

        String displayName = JsonLoadTools.getSafeElement(source, "displayName", JsonPrimitive::getAsString);
        if(displayName == null) displayName = "Unknown skill";

        String description = JsonLoadTools.getSafeElement(source, "description", JsonPrimitive::getAsString);
        if(description == null) description = "Missing description";

        SkillConfiguration config = JsonLoadTools.getSafeObject(source, "config", SkillLoader::parseConfig);
        if(config == null) config = new SkillConfiguration();


        if(type == Enums.SkillType.CUSTOM || type == Enums.SkillType.GENERIC){
            if(!source.has("modifiers")) {
                ErrorManager.registerSkillError(id, "one modifier is required");
                return;
            }

            if(!source.get("modifiers").isJsonArray()) {ErrorManager.registerSkillError(id, "invalid modifier structure");return;}

            var modifiers = new ArrayList<Modifier>();
            var attributeArray = source.getAsJsonArray("modifiers");
            for(var element : attributeArray){
                if(!element.isJsonObject()) {ErrorManager.registerSkillError(id, "invalid modifier structure");return;}

                var obj = element.getAsJsonObject();

                var attribute = JsonLoadTools.getSafeElement(obj, "attribute", JsonPrimitive::getAsString);
                if(attribute == null){ErrorManager.registerSkillError(id, "invalid attribute"); return;}

                var operation = JsonLoadTools.getSafeEnum(obj, "operation", AttributeModifier.Operation.class);
                if(operation == null) operation = AttributeModifier.Operation.ADD_VALUE;

                var values = JsonLoadTools.getSafeList(obj, "values", JsonElement::getAsFloat);
                if(values == null) {ErrorManager.registerSkillError(id, "invalid values"); return;}
                if(values.size() < maxLevel) {
                    ErrorManager.registerSkillError(id, String.format("value set is too small, current : %d, expected : %d", values.size(), maxLevel));
                    return;
                }

                var unit = JsonLoadTools.getSafeElement(obj, "unit", JsonPrimitive::getAsString);
                if(unit == null) unit = "";

                modifiers.add(new Modifier(attribute, operation, values, unit));
            }

            if(modifiers.isEmpty()) {ErrorManager.registerSkillError(id, "one modifier is required"); return;}

            SkillManager.registerSkill(new Skill(true, id, maxLevel, prices, type, category,
                    icon, displayName, description, modifiers, null, config));
            return;

        }

        if(type == Enums.SkillType.IMMUTABLE){
            if(!source.has("customValues")) {ErrorManager.registerSkillError(id, "one value set is required");return;}
            if(!source.get("customValues").isJsonObject()) {ErrorManager.registerSkillError(id, "invalid value set structure");return;}

            var valueSet = new HashMap<String, ValueSet>();
            var obj = source.getAsJsonObject("customValues");
            for(var entry : obj.entrySet()){
                String key = entry.getKey();

                if(!entry.getValue().isJsonObject()){ErrorManager.registerSkillError(id, "invalid value set structure");return;}
                var iterationObj = entry.getValue().getAsJsonObject();

                var values = JsonLoadTools.getSafeList(iterationObj, "values", JsonElement::getAsFloat);
                if(values == null) {ErrorManager.registerSkillError(id, "invalid values"); return;}
                if(values.size() < maxLevel) {
                    ErrorManager.registerSkillError(id, String.format("value set is too small, current : %d, expected : %d", values.size(), maxLevel));
                    return;
                }

                var unit = JsonLoadTools.getSafeElement(iterationObj, "unit", JsonPrimitive::getAsString);
                if(unit == null) unit = "";

                valueSet.put(key, new ValueSet(values, unit));
            }

            if(valueSet.isEmpty()){ErrorManager.registerSkillError(id, "one value set is required");return;}

            SkillManager.registerSkill(new Skill(true, id, maxLevel, prices, type, category,
                    icon, displayName, description, null, valueSet, config));
        }
    }

    private static void applyTransitions(JsonObject source, String skillId){
        //MIGRATION FOR PREREQUISITES: List<String> -> Map<String, Integer> (ID + Level)
        var config = source.get("config");
        if(config instanceof JsonObject configObject){

            var prerequisite = configObject.get("skillPrerequisites");
            var newPrerequisites = new JsonObject();
            if(prerequisite instanceof JsonArray prerequisiteList){
                for (var id : prerequisiteList){
                    newPrerequisites.addProperty(id.getAsString(), 1);
                }

                configObject.remove("skillPrerequisites");
                configObject.add("skillPrerequisites", newPrerequisites);

                ErrorManager.registerLoadDeprecation(skillId, "\"skillPrerequisites\"", "{skillID, requiredLevel}");
            }
        }
    }


    //Utils
    private static final Set<String> GENERIC_PARAMETERS = Set.of("purchasable", "refundable", "visible", "levelRequirement", "incompatibleSkills", "skillPrerequisites");
    private static SkillConfiguration parseConfig(JsonObject obj){
        //Generic Parameters
        var purchasable = JsonLoadTools.getSafeElement(obj, "purchasable", JsonPrimitive::getAsBoolean);
        var refundable = JsonLoadTools.getSafeElement(obj, "refundable", JsonPrimitive::getAsBoolean);
        var visible = JsonLoadTools.getSafeElement(obj, "visible", JsonPrimitive::getAsBoolean);

        var levelRequirement = JsonLoadTools.getSafeElement(obj, "levelRequirement", JsonPrimitive::getAsInt);

        var incompatibleSkills = JsonLoadTools.getSafeList(obj, "incompatibleSkills", JsonElement::getAsString);
        var skillPrerequisites = JsonLoadTools.getSafeMap(obj, "skillPrerequisites", k -> k, JsonElement::getAsInt);

        //Specific Parameters
        var tempObj = new JsonObject();
        CompoundTag parameters = null;

        for(var kvp : obj.entrySet()){
            if(!GENERIC_PARAMETERS.contains(kvp.getKey())) tempObj.add(kvp.getKey(), kvp.getValue());
        }

        if(!tempObj.isEmpty()){
            if(JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, tempObj) instanceof CompoundTag tag){
                parameters = tag;
            }
        }

        return new SkillConfiguration(levelRequirement, incompatibleSkills, skillPrerequisites, refundable, purchasable, visible, parameters);
    }

}
