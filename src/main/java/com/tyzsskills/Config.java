package com.tyzsskills;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;


public class Config {

    //==================Common Config==================
    public static final ModConfigSpec COMMON_SPEC;

    public static final String REFUND_SYSTEM_KEY = "refund_system";
    public static final ModConfigSpec.BooleanValue REFUND_SYSTEM;

    public static final String REFUND_PERCENTAGE_KEY = "refund_percentage";
    public static final ModConfigSpec.DoubleValue REFUND_PERCENTAGE;

    public static final String TRAIT_UNLOCK_LEVEL_KEY = "trait_unlock";
    public static final ModConfigSpec.DoubleValue TRAIT_UNLOCK_LEVEL;

    public static final ModConfigSpec.BooleanValue PREVENT_PLACED_BLOCK_XP;

    public static final ModConfigSpec.BooleanValue EARN_XP_IN_CREATIVE;

    public static final ModConfigSpec.BooleanValue EARN_XP_BY_EATING;
    public static final ModConfigSpec.DoubleValue FISHING_XP_VALUE;
    public static final ModConfigSpec.DoubleValue SLEEPING_XP_VALUE;
    public static final ModConfigSpec.DoubleValue CRAFTING_XP_VALUE;
    public static final ModConfigSpec.DoubleValue BREEDING_XP_VALUE;
    public static final ModConfigSpec.DoubleValue CARVING_XP_VALUE;
    public static final ModConfigSpec.DoubleValue LANDSCAPING_XP_VALUE;
    public static final ModConfigSpec.DoubleValue ADVANCEMENT_TASK_XP_VALUE;
    public static final ModConfigSpec.DoubleValue ADVANCEMENT_GOAL_XP_VALUE;
    public static final ModConfigSpec.DoubleValue ADVANCEMENT_CHALLENGE_XP_VALUE;
    public static final ModConfigSpec.DoubleValue NEW_BIOME_XP_VALUE;
    public static final ModConfigSpec.DoubleValue NEW_DIMENSION_XP_VALUE;


    //==================Client Config==================
    public static final ModConfigSpec CLIENT_SPEC;

    public static final ModConfigSpec.BooleanValue SHOW_XP_OVERLAY;
    public static final ModConfigSpec.BooleanValue SHOW_LEVEL_OVERLAY;
    public static final ModConfigSpec.BooleanValue SHOW_SKILL_OVERLAY;
    public static final ModConfigSpec.BooleanValue SHOW_OVERLAYS_IN_CREATIVE;
    public static final ModConfigSpec.BooleanValue SHOW_DEBUG_MESSAGES;
    public static final ModConfigSpec.DoubleValue FOV_REDUCTION;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> FOV_AFFECTED_SKILLS;

    static {
        //Construction du COMMON
        ModConfigSpec.Builder commonBuilder = new ModConfigSpec.Builder();

        commonBuilder.comment("General Gameplay Settings").push("general");

        REFUND_SYSTEM = commonBuilder
                .comment("Play with the refund system")
                .define("refund_system", true);
        REFUND_PERCENTAGE = commonBuilder
                .comment("Percentage of the initial price refunded")
                .defineInRange("refund_percentage", 30.0, 1.0, 100.0);

        TRAIT_UNLOCK_LEVEL = commonBuilder
                .comment("Blocks access and use of traits below a certain level")
                .defineInRange("trait_unlock_level", 20.0, 1.0, Double.MAX_VALUE);

        PREVENT_PLACED_BLOCK_XP = commonBuilder
                .comment("Prevent manually placed block from providing xp")
                .define("prevent_placed_block_xp", true);

        commonBuilder.pop();





        commonBuilder.push("xp_values");

        EARN_XP_IN_CREATIVE = commonBuilder
                .comment("Earn xp in creative")
                .define("earn_xp_in_creative", false);

        EARN_XP_BY_EATING = commonBuilder
                .comment("Earn xp by eating (based on json values)")
                .define("eating_xp_earnings", true);

        SLEEPING_XP_VALUE = commonBuilder
                .comment("Earn xp by sleeping")
                .defineInRange("sleeping_gain", 25.0, .0, Double.MAX_VALUE);

        FISHING_XP_VALUE = commonBuilder
                .comment("Fishing xp earning value")
                .defineInRange("fishing_gain", 7.0, .0, Double.MAX_VALUE);

        CRAFTING_XP_VALUE = commonBuilder
                .comment("Crafting xp earning value")
                .defineInRange("crafting_gain", .2, .0, Double.MAX_VALUE);

        BREEDING_XP_VALUE = commonBuilder
                .comment("Breeding xp earning value")
                .defineInRange("breeding_gain", 15.0, .0, Double.MAX_VALUE);

        CARVING_XP_VALUE = commonBuilder
                .comment("Stripping xp earning value")
                .defineInRange("stripping__gain", 3.5, .0, Double.MAX_VALUE);

        LANDSCAPING_XP_VALUE = commonBuilder
                .comment("Pathing xp earning value")
                .defineInRange("pathing_gain", 1.5, .0, Double.MAX_VALUE);

        ADVANCEMENT_TASK_XP_VALUE = commonBuilder
                .comment("Xp gains for basic advancements (Tasks)")
                .defineInRange("task_gain", 25.0, .0, Double.MAX_VALUE);

        ADVANCEMENT_GOAL_XP_VALUE = commonBuilder
                .comment("Xp gains for rare advancements (Goals)")
                .defineInRange("goal_gain", 100.0, .0, Double.MAX_VALUE);

        ADVANCEMENT_CHALLENGE_XP_VALUE = commonBuilder
                .comment("Xp gains for epic advancements (Challenges)")
                .defineInRange("challenge_gain", 250.0, .0, Double.MAX_VALUE);

        NEW_BIOME_XP_VALUE = commonBuilder
                .comment("Xp gains for discovering new biome")
                .defineInRange("biome_gain", 45.0, .0, Double.MAX_VALUE);

        NEW_DIMENSION_XP_VALUE = commonBuilder
                .comment("Xp gains for discovering new dimension")
                .defineInRange("dimension_gain", 500.0, .0, Double.MAX_VALUE);

        commonBuilder.pop();
        COMMON_SPEC = commonBuilder.build();





        //Construction du CLIENT
        ModConfigSpec.Builder clientBuilder = new ModConfigSpec.Builder();

        clientBuilder.comment("Client Settings").push("overlays");

        SHOW_OVERLAYS_IN_CREATIVE = clientBuilder
                .comment("Display overlays in creative")
                .define("show_overlays_in_creative", false);

        SHOW_XP_OVERLAY = clientBuilder
                .comment("Display xp gain overlay")
                .define("show_xp_overlay", true);

        SHOW_LEVEL_OVERLAY = clientBuilder
                .comment("Display Level Up overlay")
                .define("show_level_overlay", true);

        SHOW_SKILL_OVERLAY = clientBuilder
                .comment("Display skill activation icons")
                .define("show_skill_overlay", true);

        clientBuilder.pop();






        clientBuilder.push("debug");

        SHOW_DEBUG_MESSAGES = clientBuilder
                .comment("Show debug messages")
                .define("show_debug_messages", false);

        clientBuilder.pop();






        clientBuilder.push("accessibility");

        FOV_REDUCTION = clientBuilder
                .comment("Reduces the FOV effect that can be caused by skills (0.0 = Normal Minecraft, 1.0 = No FOV change)")
                .defineInRange("speed_fov_reduction", 0.95f, 0f, 1f);

        FOV_AFFECTED_SKILLS = clientBuilder
                .comment("Defines skills that are affected by the fov reduction")
                .defineListAllowEmpty("fov_affected_skills",
                        () -> List.of("speed_boost"),
                        () -> "skill_id",
                        obj -> obj instanceof String);

        clientBuilder.pop();
        CLIENT_SPEC = clientBuilder.build();

    }


}
