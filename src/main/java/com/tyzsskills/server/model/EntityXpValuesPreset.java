package com.tyzsskills.server.model;

public class EntityXpValuesPreset {
    public static String GetDefaultXpValues(){
        return """
        {
            "category_A": {
                "xp" : 2.5,
                "entities": [
                        "minecraft:chicken",
                        "minecraft:endermite",
                        "minecraft:pufferfish",
                        "minecraft:rabbit",
                        "minecraft:silverfish"
                ]
            },
            "category_B": {
                "xp" : 5,
                "entities": [
                        "minecraft:cod",
                        "minecraft:cow",
                        "minecraft:magma_cube",
                        "minecraft:mooshroom",
                        "minecraft:pig",
                        "minecraft:salmon",
                        "minecraft:sheep",
                        "minecraft:tropical_fish",
                        "minecraft:wolf"
                ]
            },
            "category_C": {
                "xp" : 12.5,
                "entities": [
                        "minecraft:bogged",
                        "minecraft:creeper",
                        "minecraft:drowned",
                        "minecraft:husk",
                        "minecraft:skeleton",
                        "minecraft:slime",
                        "minecraft:spider",
                        "minecraft:stray",
                        "minecraft:zoglin",
                        "minecraft:zombie",
                        "minecraft:zombie_horse",
                        "minecraft:zombie_villager",
                        "minecraft:zombified_piglin",
                        "#minecraft:skeletons",
                        "minecraft:cave_spider",
                        "#minecraft:zombies",
                        "#c:skeletons",
                        "#c:zombies"
                ]
            },
            "category_D": {
                "xp" : 17,
                "entities": [
                        "minecraft:hoglin",
                        "minecraft:piglin",
                        "minecraft:shulker",
                        "minecraft:vex",
                        "minecraft:witch"
                ]
            },
            "category_E": {
                "xp" : 20,
                "entities": [
                        "minecraft:blaze",
                        "minecraft:ghast",
                        "minecraft:pillager",
                        "minecraft:vindicator",
                        "#minecraft:illager",
                        "#minecraft:raiders"
                ]
            },
            "category_F": {
                "xp" : 25,
                "entities": [
                        "minecraft:breeze",
                        "minecraft:evoker",
                        "minecraft:guardian",
                        "minecraft:ravager"
                ]
            },
            "category_G": {
                "xp" : 45,
                "entities": [
                        "minecraft:enderman",
                        "minecraft:phantom",
                        "minecraft:piglin_brute",
                        "minecraft:skeleton_horse",
                        "minecraft:wither_skeleton"
                ]
            },
            "category_H": {
                "xp" : 250,
                "entities": [
                        "minecraft:elder_guardian"
                ]
            },
            "category_I": {
                "xp" : 750,
                "entities": [
                        "minecraft:ender_dragon",
                        "minecraft:wither",
                        "#c:bosses"
                ]
            },
            "category_J": {
                "xp" : 1000,
                "entities": [
                    "minecraft:warden"
                ]
            }
        }""";
    }
}
