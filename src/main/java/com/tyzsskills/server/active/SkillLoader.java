package com.tyzsskills.server.active;

import com.google.gson.JsonObject;
import com.tyzsskills.server.model.Skill;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.ArrayList;
import java.util.List;

public class SkillLoader {

    public static void LoadSKill(JsonObject source){
        String id = GetSafeString(source, "id");
        if(id == null) {LogError("Unknow"); return;}

        Boolean state = GetSafeBool(source, "active");
        if(state == null) state = false;
        if(!state) return; //Les skills désactivés de sont pas chargés

        String displayName = GetSafeString(source, "name");
        if(displayName == null) displayName = "Custom Skill";

        Integer maxLevel = GetSafeInt(source, "maximumLevel");
        if(maxLevel == null) {LogError(id); return;}
        maxLevel = Math.min(Math.max(maxLevel, 1), 10);

        List<Integer> prices = GetSafeIntArray(source, "prices");
        if(prices == null || prices.size() < maxLevel) {LogError(id); return;}

        List<Float> values = GetSafeFloatArray(source, "values");
        if(values == null || values.size() != prices.size()) {LogError(id); return;}

        Skill.SkillType type = GetSafeType(source, "type");
        if(type == null) {LogError(id); return;}

        Skill.SkillCategory category = GetSafeCategory(source, "category");
        if(category == null) category = Skill.SkillCategory.MISC;

        Boolean purchasable = GetSafeBool(source,"purchasable");
        if(purchasable == null) purchasable = true;

        AttributeModifier.Operation operation = GetSafeOperation(source, "operation");
        if(operation == null) {LogError(id); return;}

        String modifier = GetSafeString(source, "modifier");


        SkillManager.Get().RegisterSKill(
                new Skill(state, id, maxLevel, prices, values, type,
                        category, modifier, operation, purchasable));
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

    private static Skill.SkillCategory GetSafeCategory(JsonObject obj, String key){
        if (obj == null || key == null) return null;

        var typeValue = obj.get(key);
        if(typeValue == null || !typeValue.isJsonPrimitive()) return null;

        var typeValueString = typeValue.getAsString();
        if(typeValueString == null) return null;

        Skill.SkillCategory category;
        try {category = Skill.SkillCategory.valueOf(typeValueString.toUpperCase());}
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

    //Utilitaire
    private static void LogError(String id){
        System.out.println("Unable to load Skill: " + id);
    }
}
