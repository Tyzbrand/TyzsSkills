package com.tyzsskills.impl.server.skills;

import com.google.gson.JsonObject;
import com.tyzsskills.Config;
import com.tyzsskills.impl.server.active.ErrorManager;
import com.tyzsskills.impl.server.model.Skill;
import com.tyzsskills.impl.server.model.Trait;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

@ApiStatus.Internal
public class SkillLoader {

    public static void LoadSKill(JsonObject source){
        String id = GetSafeString(source, "id");
        if(id == null || id.isBlank()) {ErrorManager.RegisterSkillError("Unknow", "invalid id"); return;}
        id = id.toLowerCase();

        Boolean state = GetSafeBool(source, "active");
        if(state == null) state = false;
        if(!state) return; //Les skills désactivés de sont pas chargés

        List<Integer> prices = new ArrayList<>();
        List<Float> values = new ArrayList<>();

        Integer maxLevel = GetSafeInt(source, "maximumLevel");
        if(maxLevel == null) maxLevel = 1;
        maxLevel = Math.min(Math.max(maxLevel, 1), 10);

        if(source.has("prices")){
            List<Integer> tempPrices = GetSafeIntArray(source, "prices");
            if(tempPrices == null) {ErrorManager.RegisterSkillError(id, "invalid price list"); return;}
            prices = tempPrices;
        }

        if(source.has("values")){
            List<Float> tempValues = GetSafeFloatArray(source, "values");
            if(tempValues == null) {ErrorManager.RegisterSkillError(id, "invalid value list"); return;}
            values = tempValues;
        }

        Skill.SkillType type = GetSafeType(source, "type");
        if(type == null) {ErrorManager.RegisterSkillError(id, "invalid skill type"); return;}

        Skill.CategoryType category = GetSafeCategory(source, "category");
        if(category == null) category = Skill.CategoryType.MISC;

        Boolean purchasable = GetSafeBool(source,"purchasable");
        if(purchasable == null) purchasable = true;

        AttributeModifier.Operation operation = GetSafeOperation(source, "operation");
        String modifier = GetSafeString(source, "modifier");


        String icon = GetSafeString(source, "icon");
        if(icon == null) icon = "tyzs_skills:textures/gui/skills/default.png";

        String displayName = GetSafeString(source, "displayName");
        if(displayName == null) displayName = "Unknown skill";

        String description = GetSafeString(source, "description");
        if(description == null) description = "Missing description";

        String unit = GetSafeString(source, "unit");
        if(unit == null) unit = "";


        Integer powerWeight = GetSafeInt(source, "powerWeight");
        if(powerWeight == null) powerWeight = 0;
        powerWeight = Math.max(0, powerWeight);


        if(type == Skill.SkillType.TRAIT || powerWeight > 0){

            if(!Config.TRAIT_SYSTEM.get()) return;

            int price = prices.isEmpty()? 0 : prices.getFirst();
            SkillManager.Get().registerSKill(new Trait(
                    state, id, powerWeight, price, purchasable, icon, displayName, description)
            );
            return;
        }

        if(prices.size() < maxLevel) {
            ErrorManager.RegisterSkillError(id, "Not enough prices defined. Expected " + maxLevel + ", got " + prices.size());
            return;
        }

        if(!values.isEmpty() && prices.size() != values.size()) {
            ErrorManager.RegisterSkillError(id, "Array Size Mismatch: 'prices' and 'values' must have the same length.");
            return;
        }

        if(operation == null){
            if(type == Skill.SkillType.GENERIC || type == Skill.SkillType.CUSTOM) {ErrorManager.RegisterSkillError(id, "Missing 'operation' for GENERIC/CUSTOM skill."); return;}
            else operation = AttributeModifier.Operation.ADD_VALUE;
        }
        if(modifier == null && (type == Skill.SkillType.GENERIC || type == Skill.SkillType.CUSTOM)) {ErrorManager.RegisterSkillError(id, "Missing modifier"); return;}


        SkillManager.Get().registerSKill(
                new Skill(state, id, maxLevel, prices, values, type,
                        category, modifier, operation, purchasable, icon, displayName, description, unit));
    }


    //Verifications
    private static String GetSafeString(JsonObject obj, String key){
        if(obj == null || key == null) return null;

        var value = obj.get(key);
        if(value == null || !value.isJsonPrimitive()) return null;

        return value.getAsString();
    }


    private static Integer GetSafeInt(JsonObject obj, String key){
        if(obj == null || key == null) return null;

        var value = obj.get(key);
        if(value == null || !value.isJsonPrimitive()) return null;

        return value.getAsInt();
    }

    private static Boolean GetSafeBool(JsonObject obj, String key){
        if(obj == null || key == null) return null;

        var value = obj.get(key);
        if(value == null || !value.isJsonPrimitive()) return null;

        return value.getAsBoolean();
    }

    private static List<Integer> GetSafeIntArray(JsonObject obj, String key){
        if(obj == null || key == null) return null;

        var arrayValue = obj.get(key);
        if(arrayValue == null || !arrayValue.isJsonArray()) return null;

        var array = arrayValue.getAsJsonArray();

        List<Integer> prices = new ArrayList<>();
        for (var iteration : array){prices.add(iteration.getAsInt());}

        return prices;
    }

    private static List<Float> GetSafeFloatArray(JsonObject obj, String key){
        if(obj == null || key == null) return null;

        var arrayValue = obj.get(key);
        if(arrayValue == null || !arrayValue.isJsonArray()) return null;

        var array = arrayValue.getAsJsonArray();

        List<Float> values = new ArrayList<>();
        for (var iteration : array){values.add(iteration.getAsFloat());}

        return values;
    }

    private static Skill.SkillType GetSafeType(JsonObject obj, String key){
        if (obj == null || key == null) return null;

        var typeValue = obj.get(key);
        if(typeValue == null || !typeValue.isJsonPrimitive()) return null;

        var typeValueString = typeValue.getAsString();
        if(typeValueString == null) return null;

        Skill.SkillType type;
        try {type = Skill.SkillType.valueOf(typeValueString.toUpperCase());}
        catch (IllegalArgumentException e) {return null;}

        return type;
    }

    private static Skill.CategoryType GetSafeCategory(JsonObject obj, String key){
        if (obj == null || key == null) return null;

        var typeValue = obj.get(key);
        if(typeValue == null || !typeValue.isJsonPrimitive()) return null;

        var typeValueString = typeValue.getAsString();
        if(typeValueString == null) return null;

        Skill.CategoryType category;
        try {category = Skill.CategoryType.valueOf(typeValueString.toUpperCase());}
        catch (IllegalArgumentException e) {return null;}

        return category;
    }

    private static AttributeModifier.Operation GetSafeOperation(JsonObject obj, String key){
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
