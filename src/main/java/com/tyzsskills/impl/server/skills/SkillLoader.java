package com.tyzsskills.impl.server.skills;

import com.google.gson.JsonObject;
import com.tyzsskills.Config;
import com.tyzsskills.Constants;
import com.tyzsskills.api.Enums;
import com.tyzsskills.impl.server.active.ErrorManager;
import com.tyzsskills.impl.server.model.Skill;
import com.tyzsskills.impl.server.model.Trait;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

@ApiStatus.Internal
public class SkillLoader {

    public static void loadSkill(JsonObject source){

        String id = getSafeString(source, "id");
        if(id == null || id.isBlank()) {ErrorManager.registerSkillError("Unknow", "invalid id"); return;}
        id = id.toLowerCase();

        Boolean state = getSafeBool(source, "active");
        if(state == null) state = false;
        if(!state) return; //Les skills désactivés de sont pas chargés

        List<Integer> prices = new ArrayList<>();
        List<Float> values = new ArrayList<>();

        Integer maxLevel = getSafeInt(source, "maximumLevel");
        if(maxLevel == null) maxLevel = 1;
        maxLevel = Math.min(Math.max(maxLevel, 1), Constants.SKILL_MAX_LEVEL);

        if(source.has("prices")){
            List<Integer> tempPrices = getSafeIntArray(source, "prices");
            if(tempPrices == null) {ErrorManager.registerSkillError(id, "invalid price list"); return;}
            prices = tempPrices;
        }

        if(source.has("values")){
            List<Float> tempValues = getSafeFloatArray(source, "values");
            if(tempValues == null) {ErrorManager.registerSkillError(id, "invalid value list"); return;}
            values = tempValues;
        }

        Enums.SkillType type = getSafeType(source, "type");
        if(type == null) {ErrorManager.registerSkillError(id, "invalid skill type"); return;}

        Enums.CategoryType category = getSafeCategory(source, "category");
        if(category == null) category = Enums.CategoryType.MISC;

        Boolean purchasable = getSafeBool(source,"purchasable");
        if(purchasable == null) purchasable = true;

        AttributeModifier.Operation operation = getSafeOperation(source, "operation");
        String modifier = getSafeString(source, "modifier");


        String icon = getSafeString(source, "icon");
        if(icon == null) icon = "tyzs_skills:textures/gui/skills/default.png";

        String displayName = getSafeString(source, "displayName");
        if(displayName == null) displayName = "Unknown skill";

        String description = getSafeString(source, "description");
        if(description == null) description = "Missing description";

        String unit = getSafeString(source, "unit");
        if(unit == null) unit = "";


        Integer powerWeight = getSafeInt(source, "powerWeight");
        if(powerWeight == null) powerWeight = 0;
        powerWeight = Math.max(0, powerWeight);


        if(type == Enums.SkillType.TRAIT || powerWeight > 0){

            int price = prices.isEmpty()? 0 : prices.getFirst();

            SkillManager.get().registerSKill(new Trait(
                    state, id, powerWeight, price, purchasable, icon, displayName, description)
            );
            return;
        }

        if(prices.size() < maxLevel) {
            ErrorManager.registerSkillError(id, "Not enough prices defined. Expected " + maxLevel + ", got " + prices.size());
            return;
        }

        if(!values.isEmpty() && prices.size() != values.size()) {
            ErrorManager.registerSkillError(id, "Array Size Mismatch: 'prices' and 'values' must have the same length.");
            return;
        }

        if(operation == null){
            if(type == Enums.SkillType.GENERIC || type == Enums.SkillType.CUSTOM) {ErrorManager.registerSkillError(id, "Missing 'operation' for GENERIC/CUSTOM skill."); return;}
            else operation = AttributeModifier.Operation.ADD_VALUE;
        }
        if(modifier == null && (type == Enums.SkillType.GENERIC || type == Enums.SkillType.CUSTOM)) {ErrorManager.registerSkillError(id, "Missing modifier"); return;}


        SkillManager.get().registerSKill(
                new Skill(state, id, maxLevel, prices, values, type,
                        category, modifier, operation, purchasable, icon, displayName, description, unit));
    }


    //Verifications
    private static String getSafeString(JsonObject obj, String key){
        if(obj == null || key == null) return null;

        var value = obj.get(key);
        if(value == null || !value.isJsonPrimitive()) return null;

        return value.getAsString();
    }


    private static Integer getSafeInt(JsonObject obj, String key){
        if(obj == null || key == null) return null;

        var value = obj.get(key);
        if(value == null || !value.isJsonPrimitive()) return null;

        return value.getAsInt();
    }

    private static Boolean getSafeBool(JsonObject obj, String key){
        if(obj == null || key == null) return null;

        var value = obj.get(key);
        if(value == null || !value.isJsonPrimitive()) return null;

        return value.getAsBoolean();
    }

    private static List<Integer> getSafeIntArray(JsonObject obj, String key){
        if(obj == null || key == null) return null;

        var arrayValue = obj.get(key);
        if(arrayValue == null || !arrayValue.isJsonArray()) return null;

        var array = arrayValue.getAsJsonArray();

        List<Integer> prices = new ArrayList<>();
        for (var iteration : array){prices.add(iteration.getAsInt());}

        return prices;
    }

    private static List<Float> getSafeFloatArray(JsonObject obj, String key){
        if(obj == null || key == null) return null;

        var arrayValue = obj.get(key);
        if(arrayValue == null || !arrayValue.isJsonArray()) return null;

        var array = arrayValue.getAsJsonArray();

        List<Float> values = new ArrayList<>();
        for (var iteration : array){values.add(iteration.getAsFloat());}

        return values;
    }

    private static Enums.SkillType getSafeType(JsonObject obj, String key){
        if (obj == null || key == null) return null;

        var typeValue = obj.get(key);
        if(typeValue == null || !typeValue.isJsonPrimitive()) return null;

        var typeValueString = typeValue.getAsString();
        if(typeValueString == null) return null;

        Enums.SkillType type;
        try {type = Enums.SkillType.valueOf(typeValueString.toUpperCase());}
        catch (IllegalArgumentException e) {return null;}

        return type;
    }

    private static Enums.CategoryType getSafeCategory(JsonObject obj, String key){
        if (obj == null || key == null) return null;

        var typeValue = obj.get(key);
        if(typeValue == null || !typeValue.isJsonPrimitive()) return null;

        var typeValueString = typeValue.getAsString();
        if(typeValueString == null) return null;

        Enums.CategoryType category;
        try {category = Enums.CategoryType.valueOf(typeValueString.toUpperCase());}
        catch (IllegalArgumentException e) {return null;}

        return category;
    }

    private static AttributeModifier.Operation getSafeOperation(JsonObject obj, String key){
        if (obj == null || key == null) return null;

        var typeValue = obj.get(key);
        if(typeValue == null || !typeValue.isJsonPrimitive()) return null;

        var typeValueString = typeValue.getAsString();
        if(typeValueString == null) return null;

        AttributeModifier.Operation operation;
        try {
            operation = AttributeModifier.Operation.valueOf(typeValueString.toUpperCase());}
        catch (IllegalArgumentException e) {return null;}

        return operation;
    }
}
