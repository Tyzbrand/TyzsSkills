package com.tyzsskills.impl.server.spells;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.tyzsskills.api.records.SpellProperty;
import com.tyzsskills.impl.server.tools.JsonLoadTools;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpellLoader {
    private final static Map<String, JsonObject> spellQueue = new HashMap<>();

    public static void preloadSpell(JsonObject source, @NotNull String filename, boolean isDefault){
        if(source == null) {
            //ErrorManager.registerSkillError(filename, "file is empty");
            return;
        }

        var id = JsonLoadTools.getSafeElement(source, "id", JsonPrimitive::getAsString);
        if(id == null || id.isBlank()) {/*ErrorManager.registerSkillError(filename, "invalid id");*/ return;}
        id = id.toLowerCase();

        if(isDefault) spellQueue.putIfAbsent(id, source);
        else if(spellQueue.containsKey(id)) spellQueue.put(id, source);
        else  return;//ErrorManager.registerSkillError(filename, "unable to find the spell to overwrite");
    }

    public static void finalizePreloading() {
        for (var kvp : spellQueue.entrySet()) loadSpell(kvp.getKey(), kvp.getValue());
        spellQueue.clear();
    }

    private static void loadSpell(String id, JsonObject source){
        Boolean state = JsonLoadTools.getSafeElement(source, "active", JsonPrimitive::getAsBoolean);
        if(state == null) state = true;
        else if (!state) return;

        String icon = JsonLoadTools.getSafeElement(source, "icon", JsonPrimitive::getAsString);
        if(icon == null) icon = "tyzs_skills:textures/gui/spells/default.png";

        String displayName = JsonLoadTools.getSafeElement(source, "displayName", JsonPrimitive::getAsString);
        if(displayName == null) displayName = "Unknown spell";

        String description = JsonLoadTools.getSafeElement(source, "description", JsonPrimitive::getAsString);
        if(description == null) description = "Missing description";


        if(!source.get("properties").isJsonArray()) {
            /*ErrorManager.registerSkillError(id, "invalid modifier structure");*/return;}

        var properties = new ArrayList<SpellProperty>();
        boolean hasCooldown = false;

        var propertiesArray = source.getAsJsonArray("properties");
        for(var property : propertiesArray){
            if(!property.isJsonObject()) {/*ErrorManager.registerSkillError(id, "invalid modifier structure");*/continue;}

            var obj = property.getAsJsonObject();

            String key = JsonLoadTools.getSafeElement(obj, "key", JsonPrimitive::getAsString);
            if(key == null) /*REGISTER AN ERROR*/continue;
            if(key.equals("cooldown")) hasCooldown = true;

            List<Integer> prices = JsonLoadTools.getSafeList(obj, "prices", JsonElement::getAsInt);
            if(prices == null || prices.isEmpty()) /*REGISTER AN ERROR*/continue;

            List<Float> values = JsonLoadTools.getSafeList(obj, "values", JsonElement::getAsFloat);
            if(values == null || values.isEmpty()) /*REGISTER AN ERROR*/continue;

            String propDisplayName = JsonLoadTools.getSafeElement(obj, "displayName", JsonPrimitive::getAsString);
            if(propDisplayName == null) propDisplayName = "Unknown property";

            String propDescription = JsonLoadTools.getSafeElement(obj, "description", JsonPrimitive::getAsString);
            if(propDescription == null) propDescription = "Missing description";

            properties.add(new SpellProperty(key, prices, values, propDisplayName, propDescription));
        }
        if(!hasCooldown) /*REGISTER AN ERROR*/return;
        SpellManager.registerSpell(new Spell(true, id, icon, displayName, description, properties));
    }
}
