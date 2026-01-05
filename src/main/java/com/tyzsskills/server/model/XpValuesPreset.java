package com.tyzsskills.server.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class XpValuesPreset {

    public static String GetDefaultXpValues(){
        return"""
        {
            "category_A": {
                "xp" : 0.5,
                "blocks": [
                "minecraft:dirt",
                "minecraft:cobblestone",
                "minecraft:gravel"
                ]
            },
            "category_B": {
                "xp" : 1,
                "blocks": [
                "minecraft:stone",
                ]
            }
        }""";
    }
}
