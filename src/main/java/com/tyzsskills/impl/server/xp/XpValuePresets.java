package com.tyzsskills.impl.server.xp;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class XpValuePresets {

    public record XpCategory(float xp, @SerializedName("id") List<String> ids){}

    @NotNull
    public static Map<String, XpCategory> getBlockValuesPreset(){
        var map = new LinkedHashMap<String, XpCategory>();

        map.put("category_A", new XpCategory(0.1f, List.of(
                "minecraft:cobblestone",
                "minecraft:cobbled_deepslate",
                "minecraft:moss_block",
                "minecraft:netherrack",
                "minecraft:stone",
                "minecraft:deepslate"
        )));

        map.put("category_B", new XpCategory(0.3f, List.of(
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
        )));

        map.put("category_C", new XpCategory(0.5f, List.of(
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
        )));

        map.put("category_D", new XpCategory(1f, List.of(
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
        )));

        map.put("category_E", new XpCategory(1.5f, List.of(
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
        )));

        map.put("category_F", new XpCategory(2f, List.of(
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
        )));

        map.put("category_G", new XpCategory(3f, List.of(
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
        )));

        map.put("category_H", new XpCategory(5f, List.of(
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
        )));

        map.put("category_I", new XpCategory(7f, List.of(
                "minecraft:gold_ore",
                "minecraft:deepslate_gold_ore",
                "minecraft:deepslate_redstone_ore",
                "minecraft:lapis_ore",
                "minecraft:deepslate_lapis_ore"
        )));

        map.put("category_J", new XpCategory(10f, List.of(
                "minecraft:emerald_ore"
        )));

        map.put("category_K", new XpCategory(15f, List.of(
                "minecraft:deepslate_coal_ore",
                "minecraft:deepslate_emerald_ore",
                "minecraft:obsidian"
        )));

        map.put("category_L", new XpCategory(30f, List.of(
                "minecraft:diamond_ore",
                "minecraft:deepslate_diamond_ore",
                "minecraft:crying_obsidian"
        )));

        map.put("category_M", new XpCategory(150f, List.of(
                "minecraft:ancient_debris",
                "minecraft:spawner"
        )));

        return map;
    }

    @NotNull
    public static Map<String, XpCategory> getEntityValuesPreset(){
        var map = new LinkedHashMap<String, XpCategory>();

        map.put("category_A", new XpCategory(2.5f, List.of(
                "minecraft:chicken",
                "minecraft:endermite",
                "minecraft:pufferfish",
                "minecraft:rabbit",
                "minecraft:silverfish"
        )));

        map.put("category_B", new XpCategory(5f, List.of(
                "minecraft:cod",
                "minecraft:cow",
                "minecraft:magma_cube",
                "minecraft:mooshroom",
                "minecraft:pig",
                "minecraft:salmon",
                "minecraft:sheep",
                "minecraft:tropical_fish",
                "minecraft:wolf"
        )));

        map.put("category_C", new XpCategory(12.5f, List.of(
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
        )));

        map.put("category_D", new XpCategory(17f, List.of(
                "minecraft:hoglin",
                "minecraft:piglin",
                "minecraft:shulker",
                "minecraft:vex",
                "minecraft:witch"
        )));

        map.put("category_E", new XpCategory(20f, List.of(
                "minecraft:blaze",
                "minecraft:ghast",
                "minecraft:pillager",
                "minecraft:vindicator",
                "#minecraft:illager",
                "#minecraft:raiders"
        )));

        map.put("category_F", new XpCategory(25f, List.of(
                "minecraft:breeze",
                "minecraft:evoker",
                "minecraft:guardian",
                "minecraft:ravager"
        )));

        map.put("category_G", new XpCategory(45f, List.of(
                "minecraft:enderman",
                "minecraft:phantom",
                "minecraft:piglin_brute",
                "minecraft:skeleton_horse",
                "minecraft:wither_skeleton"
        )));

        map.put("category_H", new XpCategory(250f, List.of(
                "minecraft:elder_guardian"
        )));

        map.put("category_I", new XpCategory(750f, List.of(
                "minecraft:ender_dragon",
                "minecraft:wither",
                "#c:bosses"
        )));

        map.put("category_J", new XpCategory(1000f, List.of(
                "minecraft:warden"
        )));

        return map;
    }

    @NotNull
    public static Map<String, XpCategory> getFoodValuesPreset(){
        var map = new LinkedHashMap<String, XpCategory>();

        map.put("category_A", new XpCategory(1f, List.of(
                "minecraft:rotten_flesh",
                "minecraft:sweet_berries",
                "minecraft:glow_berries"
        )));

        map.put("category_B", new XpCategory(2f, List.of(
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
        )));

        map.put("category_C", new XpCategory(3f, List.of(
                "minecraft:porkchop",
                "minecraft:cod",
                "minecraft:salmon",
                "minecraft:beef",
                "minecraft:chicken",
                "minecraft:rabbit",
                "minecraft:mutton"
        )));

        map.put("category_D", new XpCategory(4f, List.of(
                "minecraft:milk_bucket"
        )));

        map.put("category_E", new XpCategory(5f, List.of(
                "minecraft:bread",
                "minecraft:apple"
        )));

        map.put("category_F", new XpCategory(10f, List.of(
                "minecraft:mushroom_stew",
                "minecraft:rabbit_stew",
                "minecraft:beetroot_soup",
                "minecraft:suspicious_stew"
        )));

        map.put("category_G", new XpCategory(15f, List.of(
                "minecraft:cooked_porkchop",
                "minecraft:cooked_cod",
                "minecraft:cooked_salmon",
                "minecraft:cooked_beef",
                "minecraft:cooked_chicken",
                "minecraft:cooked_rabbit",
                "minecraft:cooked_mutton"
        )));

        map.put("category_H", new XpCategory(25f, List.of(
                "minecraft:golden_apple",
                "minecraft:golden_carrot",
                "minecraft:chorus_fruit"
        )));

        map.put("category_I", new XpCategory(50f, List.of(
                "minecraft:pumpkin_pie"
        )));

        map.put("category_J", new XpCategory(75f, List.of(
                "minecraft:enchanted_golden_apple"
        )));

        return map;
    }
}
