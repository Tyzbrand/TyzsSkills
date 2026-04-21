package com.tyzsskills.impl.server.skills;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.tyzsskills.Constants;
import com.tyzsskills.api.Enums;
import com.tyzsskills.impl.server.active.ErrorManager;
import com.tyzsskills.api.records.Modifier;
import com.tyzsskills.impl.server.model.Skill;
import com.tyzsskills.api.records.ValueSet;
import com.tyzsskills.impl.server.model.SkillBehavior;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@ApiStatus.Internal
public class SkillLoader {

    private static Map<String, JsonObject> skillQueue = new HashMap<>();

    public static void preLoadSkill(JsonObject source, boolean isDefault){
        if(source == null) return;

        var id = getSafeElement(source, "id", JsonPrimitive::getAsString);
        if(id == null || id.isBlank()) {ErrorManager.registerSkillError("Unknow", "invalid id"); return;}
        id = id.toLowerCase();

        if(isDefault) skillQueue.putIfAbsent(id, source);
        else{
            var type = getSafeElement(source, "type", JsonPrimitive::getAsString);
            boolean isCustom = type != null && type.equalsIgnoreCase(Enums.SkillType.CUSTOM.name());

            if(isCustom || skillQueue.containsKey(id)) skillQueue.put(id, source);
        }
    }

    public static void finalizePreLoading() {
        for (var kvp : skillQueue.entrySet()) loadSkill(kvp.getKey(), kvp.getValue());
        SkillManager.get().buildSortedBehaviors();
        skillQueue.clear();

        for(var skill : SkillManager.get().getAllSkills()){
            var incompatibilities = skill.getRawIncompatibilities();
            for (var id : incompatibilities){
                var conflict = SkillManager.get().getSkill(id);
                var sourceId = skill.getID();

                if(conflict == null) continue;

                if(!conflict.isSkillIncompatible(sourceId)) conflict.addIncompatibility(sourceId);
            }
        }
    }

    private static void loadSkill(String id, JsonObject source){
        Boolean state = getSafeElement(source, "active", JsonPrimitive::getAsBoolean);
        if(state == null) state = false;
        if(!state) return; //Les skills désactivés ne sont pas chargés

        Integer maxLevel = getSafeElement(source, "maximumLevel", JsonPrimitive::getAsInt);
        if(maxLevel == null) maxLevel = 1;
        maxLevel = Math.min(Math.max(maxLevel, 1), Constants.SKILL_MAX_LEVEL);

        List<Integer> prices = getSafeList(source, "prices", JsonElement::getAsInt);
        if(prices == null) {ErrorManager.registerSkillError(id, "invalid price list"); return;}
        if(maxLevel > prices.size()) {
            ErrorManager.registerSkillError(id, String.format("price set is too small, current : %d , expected : %d", prices.size(), maxLevel));
            return;
        }

        Enums.SkillType type = getSafeEnum(source, "type", Enums.SkillType.class);
        if(type == null) {ErrorManager.registerSkillError(id, "invalid skill type"); return;}

        Enums.CategoryType category = getSafeEnum(source, "category", Enums.CategoryType.class);
        if(category == null) category = Enums.CategoryType.MISC;

        Boolean purchasable = getSafeElement(source,"purchasable", JsonPrimitive::getAsBoolean);
        if(purchasable == null) purchasable = true;


        String icon = getSafeElement(source, "icon", JsonPrimitive::getAsString);
        if(icon == null) icon = "tyzs_skills:textures/gui/skills/default.png";

        String displayName = getSafeElement(source, "displayName", JsonPrimitive::getAsString);
        if(displayName == null) displayName = "Unknown skill";

        String description = getSafeElement(source, "description", JsonPrimitive::getAsString);
        if(description == null) description = "Missing description";

        Integer levelRequirement = getSafeElement(source, "levelRequirement", JsonPrimitive::getAsInt);
        if(levelRequirement == null) levelRequirement = -1;

        List<String> incompatibilities = getSafeList(source, "incompatibleSkills", JsonElement::getAsString);

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

                var attribute = getSafeElement(obj, "attribute", JsonPrimitive::getAsString);
                if(attribute == null){ErrorManager.registerSkillError(id, "invalid attribute"); return;}

                var operation = getSafeEnum(obj, "operation", AttributeModifier.Operation.class);
                if(operation == null) operation = AttributeModifier.Operation.ADD_VALUE;

                var values = getSafeList(obj, "values", JsonElement::getAsFloat);
                if(values == null) {ErrorManager.registerSkillError(id, "invalid values"); return;}
                if(values.size() < maxLevel) {
                    ErrorManager.registerSkillError(id, String.format("value set is too small, current : %d, expected : %d", values.size(), maxLevel));
                    return;
                }

                var unit = getSafeElement(obj, "unit", JsonPrimitive::getAsString);
                if(unit == null) unit = "";

                modifiers.add(new Modifier(attribute, operation, values, unit));
            }

            if(modifiers.isEmpty()) {ErrorManager.registerSkillError(id, "one modifier is required"); return;}

            SkillManager.get().registerSkill(new Skill(true, id, maxLevel, prices, type, category, purchasable,
                    icon, displayName, description, modifiers, null, levelRequirement, incompatibilities));
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

                var values = getSafeList(iterationObj, "values", JsonElement::getAsFloat);
                if(values == null) {ErrorManager.registerSkillError(id, "invalid values"); return;}
                if(values.size() < maxLevel) {
                    ErrorManager.registerSkillError(id, String.format("value set is too small, current : %d, expected : %d", values.size(), maxLevel));
                    return;
                }

                var unit = getSafeElement(iterationObj, "unit", JsonPrimitive::getAsString);
                if(unit == null) unit = "";

                valueSet.put(key, new ValueSet(values, unit));
            }

            if(valueSet.isEmpty()){ErrorManager.registerSkillError(id, "one value set is required");return;}

            SkillManager.get().registerSkill(new Skill(true, id, maxLevel, prices, type, category, purchasable,
                    icon, displayName, description, null, valueSet, levelRequirement, incompatibilities));
            return;
        }
    }


    //Verifications
    private static <T> T getSafeElement(JsonObject obj, String key, Function<JsonPrimitive, T> mapper){
        if(obj == null ||key == null) return null;

        var value = obj.get(key);
        if(value == null || !value.isJsonPrimitive()) return null;

        return mapper.apply(value.getAsJsonPrimitive());
    }

    private static <T> List<T> getSafeList(JsonObject obj, String key, Function<JsonElement, T> mapper){
        if (obj == null || key == null || !obj.has(key)) return null;

        var element = obj.get(key);
        if (!element.isJsonArray()) return null;

        var array = element.getAsJsonArray();
        List<T> list = new ArrayList<>();

        try {
            for (var item : array) {
                list.add(mapper.apply(item));
            }
        } catch (Exception e){return null;}

        return list;
    }

    private static <T extends Enum<T>> T getSafeEnum(JsonObject obj, String key, Class<T> enumClass){
        if (obj == null || key == null || !obj.has(key)) return null;

        var element = obj.get(key);
        if (!element.isJsonPrimitive()) return null;

        try {return Enum.valueOf(enumClass, element.getAsString().toUpperCase());}
        catch (IllegalArgumentException e) {return null;}
    }

}
