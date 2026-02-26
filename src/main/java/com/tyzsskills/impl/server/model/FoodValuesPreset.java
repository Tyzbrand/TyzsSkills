package com.tyzsskills.impl.server.model;

public class FoodValuesPreset {

    public static String getDefaultXpValues(){
            return """
        {
            "category_A": {
                "xp" : 1,
                "food": [
                    "minecraft:rotten_flesh",
                    "minecraft:sweet_berries",
                    "minecraft:glow_berries"
                ]
            },
            "category_B": {
                "xp" : 2,
                "food": [
                    "minecraft:cookie",
                    "minecraft:melon_slice",
                    "minecraft:carrot",
                    "minecraft:potato",
                    "minecraft:baked_potato",
                    "minecraft:beetroot",
                    "minecraft:cookie",
                    "minecraft:melon_slice",
                    "minecraft:carrot",
                    "minecraft:potato",
                    "minecraft:baked_potato",
                    "minecraft:beetroot"
                ]
            },
            "category_C": {
                "xp" : 3,
                "food": [
                    "minecraft:porkchop",
                    "minecraft:cod",
                    "minecraft:salmon",
                    "minecraft:beef",
                    "minecraft:chicken",
                    "minecraft:rabbit",
                    "minecraft:mutton"
                ]
            },
            "category_D": {
                "xp" : 4,
                "food": [
                    "minecraft:milk_bucket"
                ]
            },
            "category_E": {
                "xp" : 5,
                "food": [
                    "minecraft:bread",
                    "minecraft:apple"
                ]
            },
            "category_F": {
                "xp" : 10,
                "food": [
                    "minecraft:mushroom_stew",
                    "minecraft:rabbit_stew",
                    "minecraft:beetroot_soup",
                    "minecraft:suspicious_stew"
                ]
            },
            "category_G": {
                "xp" : 15,
                "food": [
                    "minecraft:cooked_porkchop",
                    "minecraft:cooked_cod",
                    "minecraft:cooked_salmon",
                    "minecraft:cooked_beef",
                    "minecraft:cooked_chicken",
                    "minecraft:cooked_rabbit",
                    "minecraft:cooked_mutton"
                ]
            },
            "category_H": {
                "xp" : 25,
                "food": [
                    "minecraft:golden_apple",
                    "minecraft:golden_carrot",
                    "minecraft:chorus_fruit"
                ]
            },
            "category_I": {
                "xp" : 50,
                "food": [
                    "minecraft:pumpkin_pie"
                ]
            },
            "category_J": {
                "xp" : 75,
                "food": [
                    "minecraft:enchanted_golden_apple"
                ]
            }
        }""";
        }
    }


