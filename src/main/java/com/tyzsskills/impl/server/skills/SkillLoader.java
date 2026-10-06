package com.tyzsskills.impl.server.skills;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.JsonOps;
import com.tyzsskills.Constants;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.model.SkillConfiguration;
import com.tyzsskills.api.records.STag;
import com.tyzsskills.impl.server.active.ErrorManager;
import com.tyzsskills.api.records.Modifier;
import com.tyzsskills.api.records.ValueSet;
import com.tyzsskills.api.tools.JsonLoadTools;
import com.tyzsskills.impl.server.active.TagRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
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

        SkillManager.buildGraph();
    }



    //Utils
    private static void loadSkill(String id, JsonObject source){
        Boolean state = JsonLoadTools.getSafeElement(source, "active", JsonPrimitive::getAsBoolean);
        if(state == null) state = true;
        if(!state) return; //Les skills désactivés ne sont pas chargés

        List<Integer> prices = JsonLoadTools.getSafeList(source, "prices", JsonElement::getAsInt);
        if(prices == null || prices.isEmpty()) {ErrorManager.registerSkillError(id, "invalid price list"); return;}
        if(prices.size() > Constants.SKILL_MAX_LEVEL) {
            ErrorManager.registerSkillError(id, String.format("prices list is too long, current : %d, limit : %d", prices.size(), Constants.SKILL_MAX_LEVEL)); return;
        }

        Enums.SkillType type = JsonLoadTools.getSafeEnum(source, "type", Enums.SkillType.class);
        if(type == null) {ErrorManager.registerSkillError(id, "invalid skill type"); return;}

        String category = JsonLoadTools.getSafeElement(source, "category", JsonPrimitive::getAsString);
        if(category == null) {ErrorManager.registerSkillWarn(id, "invalid category"); category = "misc";}

        String icon = JsonLoadTools.getSafeElement(source, "icon", JsonPrimitive::getAsString);
        if(icon == null) {ErrorManager.registerSkillWarn(id, "invalid icon"); icon = "tyzs_skills:textures/gui/skills/default.png";}

        String displayName = JsonLoadTools.getSafeElement(source, "displayName", JsonPrimitive::getAsString);
        if(displayName == null) {ErrorManager.registerSkillWarn(id, "invalid display name"); displayName = "Unknown skill";}

        String description = JsonLoadTools.getSafeElement(source, "description", JsonPrimitive::getAsString);
        if(description == null) {ErrorManager.registerSkillWarn(id, "invalid description"); description = "Missing description";}

        SkillConfiguration config = JsonLoadTools.getSafeObject(source, "config", obj -> parseConfig(obj, id));
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
                if(values.size() < prices.size()) {
                    ErrorManager.registerSkillError(id, String.format("value set is too small, current : %d, expected : %d", values.size(), prices.size()));
                    return;
                }

                var unit = JsonLoadTools.getSafeElement(obj, "unit", JsonPrimitive::getAsString);
                if(unit == null) unit = "";

                modifiers.add(new Modifier(attribute, operation, values, unit));
            }

            if(modifiers.isEmpty()) {ErrorManager.registerSkillError(id, "one modifier is required"); return;}

            //Not to erase JSON tags
            var finalTags = new ArrayList<>(config.tags());
            finalTags.add(TagRegistry.ATTRIBUTE);
            config.withTags(finalTags);

            SkillManager.registerSkill(new Skill(true, id, prices, type, category,
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
                if(values.size() < prices.size()) {
                    ErrorManager.registerSkillError(id, String.format("value set is too small, current : %d, expected : %d", values.size(), prices.size()));
                    return;
                }

                var unit = JsonLoadTools.getSafeElement(iterationObj, "unit", JsonPrimitive::getAsString);
                if(unit == null) unit = "";

                valueSet.put(key, new ValueSet(values, unit));
            }

            if(valueSet.isEmpty()){ErrorManager.registerSkillError(id, "one value set is required");return;}

            SkillManager.registerSkill(new Skill(true, id, prices, type, category,
                    icon, displayName, description, null, valueSet, config));
        }
    }

    private static final Set<String> GENERIC_PARAMETERS =
            Set.of("purchasable", "refundable", "visible", "permanent", "levelRequirement", "incompatibleSkills", "skillPrerequisites", "tags");
    private static SkillConfiguration parseConfig(JsonObject obj, String skillId){
        var skillConfig = new SkillConfiguration();

        //Generic Parameters
        var purchasable = JsonLoadTools.getSafeElement(obj, "purchasable", JsonPrimitive::getAsBoolean);
        if(purchasable != null) skillConfig.withPurchasable(purchasable);

        var refundable = JsonLoadTools.getSafeElement(obj, "refundable", JsonPrimitive::getAsBoolean);
        if(refundable != null) skillConfig.withRefundable(refundable);

        var visible = JsonLoadTools.getSafeElement(obj, "visible", JsonPrimitive::getAsBoolean);
        if(visible != null) skillConfig.withVisible(visible);

        var permanent = JsonLoadTools.getSafeElement(obj, "permanent", JsonPrimitive::getAsBoolean);
        if(permanent != null) skillConfig.withPermanent(permanent);


        var levelRequirement = JsonLoadTools.getSafeElement(obj, "levelRequirement", JsonPrimitive::getAsInt);
        if(levelRequirement != null) skillConfig.withLevelRequirement(levelRequirement);

        var incompatibleSkills = JsonLoadTools.getSafeList(obj, "incompatibleSkills", JsonElement::getAsString);
        if(incompatibleSkills != null) skillConfig.withIncompatibilities(incompatibleSkills);

        var skillPrerequisites = JsonLoadTools.getSafeMap(obj, "skillPrerequisites", k -> k, JsonElement::getAsInt);
        if(skillPrerequisites != null) skillConfig.withPrerequisites(skillPrerequisites);

        var rawTags = JsonLoadTools.getSafeList(obj, "tags", JsonElement::getAsString);
        ArrayList<STag> tags = null;
        if(rawTags != null && !rawTags.isEmpty()){
            tags = new ArrayList<>();
            for(var id : rawTags){
                var tag = TagRegistry.getTag(id);
                if(tag != null) tags.add(tag);
                else ErrorManager.registerSkillWarn(skillId, String.format("unknown tag '%s'", id));
            }
        }
        if(tags != null && !tags.isEmpty()) skillConfig.withTags(tags);

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

        if(parameters != null) skillConfig.withParameters(parameters);

        return skillConfig;
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

                ErrorManager.registerSkillWarn(skillId, "the field 'skillPrerequisite' has a new format: {skillID, requiredLevel}");
            }
        }

        //MIGRATION FOR MAXIMUM LEVEL: Removal
        if(source.has("maximumLevel")){
            source.remove("maximumLevel");
            ErrorManager.registerSkillWarn(skillId, "the field 'maximumLevel' is obsolete");
        }
    }

}
