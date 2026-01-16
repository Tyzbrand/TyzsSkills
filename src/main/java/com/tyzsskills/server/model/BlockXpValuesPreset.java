package com.tyzsskills.server.model;

public class BlockXpValuesPreset {

    public static String GetDefaultXpValues(){
        return"""
        {
            "category_A": {
                "xp" : 0.1,
                "blocks": [
                        "minecraft:cobblestone",
                        "minecraft:cobbled_deepslate",
                        "minecraft:moss_block",
                        "minecraft:netherrack",
                        "minecraft:stone",
                        "minecraft:deepslate"
                ]
            },
            "category_B": {
                "xp": 0.3,
                "blocks":[
                        "minecraft:dirt",
                        "minecraft:sand",
                        "minecraft:red_sand",
                        "minecraft:gravel",
                        "minecraft:kelp",
                        "minecraft:clay",
                        "minecraft:soul_sand",
                        "minecraft:soul_soil",
                        "minecraft:basalt",
                        "#minecraft:dirt",
                        "#minecraft:sand",
                        "minecraft:sculk"
                ]
            },
            "category_C": {
                "xp": 0.5,
                "blocks":[
                        "minecraft:oak_sapling",
                        "minecraft:spruce_sapling",
                        "minecraft:birch_sapling",
                        "minecraft:jungle_sapling",
                        "minecraft:acacia_sapling",
                        "minecraft:dark_oak_sapling",
                        "minecraft:cherry_sapling",
                        "minecraft:mangrove_propagule",
                        "minecraft:tuff",
                        "minecraft:mud",
                        "minecraft:crimson_nylium",
                        "minecraft:warped_nylium",
                        "minecraft:oak_leaves",
                        "minecraft:spruce_leaves",
                        "minecraft:birch_leaves",
                        "minecraft:jungle_leaves",
                        "minecraft:acacia_leaves",
                        "minecraft:cherry_leaves",
                        "minecraft:dark_oak_leaves",
                        "minecraft:mangrove_leaves",
                        "minecraft:azalea_leaves",
                        "minecraft:short_grass",
                        "minecraft:fern",
                        "minecraft:azalea",
                        "minecraft:flowering_azalea",
                        "minecraft:dead_bush",
                        "minecraft:seagrass",
                        "minecraft:sea_pickle",
                        "minecraft:dandelion",
                        "minecraft:poppy",
                        "minecraft:blue_orchid",
                        "minecraft:allium",
                        "minecraft:azure_bluet",
                        "minecraft:red_tulip",
                        "minecraft:orange_tulip",
                        "minecraft:white_tulip",
                        "minecraft:pink_tulip",
                        "minecraft:oxeye_daisy",
                        "minecraft:cornflower",
                        "minecraft:lily_of_the_valley",
                        "minecraft:crimson_roots",
                        "minecraft:warped_roots",
                        "minecraft:nether_sprouts",
                        "minecraft:weeping_vines",
                        "minecraft:twisting_vines",
                        "minecraft:pink_petals",
                        "minecraft:hanging_roots",
                        "minecraft:big_dripleaf",
                        "minecraft:small_dripleaf",
                        "minecraft:mossy_cobblestone",
                        "minecraft:chorus_plant",
                        "minecraft:chorus_flower",
                        "minecraft:ice",
                        "minecraft:snow_block",
                        "minecraft:brown_mushroom_block",
                        "minecraft:red_mushroom_block",
                        "minecraft:mushroom_stem",
                        "minecraft:vine",
                        "minecraft:glow_lichen",
                        "minecraft:sculk_vein",
                        "minecraft:dirt_path",
                        "minecraft:sunflower",
                        "minecraft:lilac",
                        "minecraft:rose_bush",
                        "minecraft:peony",
                        "minecraft:tall_grass",
                        "minecraft:large_fern",
                        "#minecraft:flowers",
                        "#minecraft:small_flowers",
                        "#minecraft:ice",
                        "#minecraft:leaves",
                        "#minecraft:saplings",
                        "#minecraft:snow",
                        "#c:leaves"
                ]
            },
            "category_D": {
                "xp": 1,
                "blocks":[
                        "minecraft:grass_block",
                        "minecraft:coarse_dirt",
                        "minecraft:podzol",
                        "minecraft:calcite",
                        "minecraft:flowering_azalea_leaves",
                        "minecraft:sponge",
                        "minecraft:cobweb",
                        "minecraft:wither_rose",
                        "minecraft:spore_blossom",
                        "minecraft:brown_mushroom",
                        "minecraft:red_mushroom",
                        "minecraft:crimson_fungus",
                        "minecraft:warped_fungus",
                        "minecraft:mycelium"
                ]
            },
            "category_E": {
                "xp": 1.5,
                "blocks":[
                        "minecraft:sandstone",
                        "minecraft:granite",
                        "minecraft:diorite",
                        "minecraft:andesite",
                        "minecraft:dripstone_block",
                        "minecraft:rooted_dirt",
                        "minecraft:muddy_mangrove_roots",
                        "minecraft:nether_quartz_ore",
                        "minecraft:end_stone",
                        "#minecraft:base_stone_overworld",
                        "minecraft:blackstone"
                ]
            },
            "category_F": {
                "xp": 2,
                "blocks":[
                        "minecraft:copper_ore",
                        "minecraft:amethyst_block",
                        "minecraft:mangrove_roots",
                        "minecraft:prismarine",
                        "minecraft:dark_prismarine",
                        "minecraft:magma_block",
                        "minecraft:dead_brain_coral",
                        "minecraft:dead_bubble_coral",
                        "minecraft:dead_fire_coral",
                        "minecraft:dead_horn_coral",
                        "minecraft:dead_tube_coral",
                        "minecraft:dead_tube_coral_block",
                        "minecraft:dead_brain_coral_block",
                        "minecraft:dead_bubble_coral_block",
                        "minecraft:dead_fire_coral_block",
                        "minecraft:dead_horn_coral_block",
                        "minecraft:dead_tube_coral_fan",
                        "minecraft:dead_brain_coral_fan",
                        "minecraft:dead_bubble_coral_fan",
                        "minecraft:dead_fire_coral_fan",
                        "minecraft:dead_horn_coral_fan",
                        "minecraft:pointed_dripstone",
                        "minecraft:coal_ore"
                ]
            },
            "category_G": {
                "xp": 3,
                "blocks":[
                        "minecraft:deepslate_copper_ore",
                        "minecraft:nether_gold_ore",
                        "minecraft:pumpkin",
                        "minecraft:melon",
                        "minecraft:hay_block",
                        "minecraft:nether_wart_block",
                        "minecraft:warped_wart_block",
                        "minecraft:sugar_cane",
                        "minecraft:bamboo",
                        "minecraft:cactus",
                        "minecraft:oak_log",
                        "minecraft:birch_log",
                        "minecraft:spruce_log",
                        "minecraft:jungle_log",
                        "minecraft:acacia_log",
                        "minecraft:cherry_log",
                        "minecraft:dark_oak_log",
                        "minecraft:mangrove_log",
                        "minecraft:warped_stem",
                        "minecraft:crimson_stem",
                        "#minecraft:crops",
                        "#minecraft:logs_that_burn",
                        "#c:ores",
                        "#c:logs",
                        "#c:crops",
                        "minecraft:sweet_berries",
                        "minecraft:glow_berries",
                        "minecraft:amethyst_cluster"
                ]
            },
            "category_H": {
                "xp": 5,
                "blocks":[
                        "minecraft:iron_ore",
                        "minecraft:deepslate_iron_ore",
                        "minecraft:redstone_ore",
                        "minecraft:budding_amethyst",
                        "minecraft:sculk_catalyst",
                        "minecraft:sculk_shrieker",
                        "minecraft:sculk_sensor",
                        "minecraft:tube_coral_block",
                        "minecraft:brain_coral_block",
                        "minecraft:bubble_coral_block",
                        "minecraft:fire_coral_block",
                        "minecraft:horn_coral_block",
                        "minecraft:tube_coral",
                        "minecraft:brain_coral",
                        "minecraft:bubble_coral",
                        "minecraft:fire_coral",
                        "minecraft:horn_coral",
                        "minecraft:tube_coral_fan",
                        "minecraft:brain_coral_fan",
                        "minecraft:bubble_coral_fan",
                        "minecraft:fire_coral_fan",
                        "minecraft:horn_coral_fan",
                        "#minecraft:corals",
                        "#minecraft:coral_blocks",
                        "#minecraft:coral_plants"
                ]
            },
            "category_I": {
                "xp": 7,
                "blocks":[
                        "minecraft:gold_ore",
                        "minecraft:deepslate_gold_ore",
                        "minecraft:deepslate_redstone_ore",
                        "minecraft:lapis_ore",
                        "minecraft:deepslate_lapis_ore"
                ]
            },
            "category_J": {
                "xp": 10,
                "blocks":[
                    "minecraft:emerald_ore"
                ]
            },
            "category_K": {
                "xp": 15,
                "blocks":[
                        "minecraft:deepslate_coal_ore",
                        "minecraft:deepslate_emerald_ore",
                        "minecraft:obsidian"
                ]
            },
            "category_L": {
                "xp": 30,
                "blocks":[
                        "minecraft:diamond_ore",
                        "minecraft:deepslate_diamond_ore",
                        "minecraft:crying_obsidian"
                ]
            },
            "category_M": {
                "xp": 150,
                "blocks":[
                        "minecraft:ancient_debris",
                        "minecraft:spawner"
                ]
            }
        }""";
    }
}
