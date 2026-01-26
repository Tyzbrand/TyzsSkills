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
    public static final ModConfigSpec.IntValue TRAIT_UNLOCK_LEVEL;

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

    //Overlays
    public static final ModConfigSpec.BooleanValue SHOW_OVERLAYS_IN_CREATIVE;
    //===Xp overlay===
    public static final ModConfigSpec.BooleanValue SHOW_XP_OVERLAY;
    public static final ModConfigSpec.IntValue XP_OFFSET_X;
    public static final ModConfigSpec.IntValue XP_OFFSET_Y;
    public static final ModConfigSpec.DoubleValue XP_DURATION;
    public static final ModConfigSpec.ConfigValue<String> XP_BG_COLOR;
    public static final ModConfigSpec.ConfigValue<String> XP_BD_COLOR;
    public static final ModConfigSpec.ConfigValue<String> XP_TEXT_COLOR;
    //===Level up overlay===
    public static final ModConfigSpec.BooleanValue SHOW_LEVEL_OVERLAY;
    public static final ModConfigSpec.IntValue LEVEL_OFFSET_X;
    public static final ModConfigSpec.IntValue LEVEL_OFFSET_Y;
    public static final ModConfigSpec.DoubleValue LEVEL_DURATION;
    public static final ModConfigSpec.ConfigValue<String> LEVEL_BG_COLOR;
    public static final ModConfigSpec.ConfigValue<String> LEVEL_BD_COLOR;
    public static final ModConfigSpec.ConfigValue<String> LEVEL_TEXT_COLOR;
    public static final ModConfigSpec.ConfigValue<String> LEVEL_SCD_TEXT_COLOR;
    //===skill overlay===
    public static final ModConfigSpec.BooleanValue SHOW_SKILL_OVERLAY;
    public static final ModConfigSpec.IntValue SKILL_OFFSET_X;
    public static final ModConfigSpec.IntValue SKILL_OFFSET_Y;
    public static final ModConfigSpec.DoubleValue SKILL_DURATION;
    public static final ModConfigSpec.ConfigValue<String> SKILL_BG_COLOR;
    public static final ModConfigSpec.ConfigValue<String> SKILL_BD_COLOR;

    //Debug
    public static final ModConfigSpec.BooleanValue SHOW_DEBUG_MESSAGES;

    //Accessibility
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
                .defineInRange("trait_unlock_level", 20, 1, Integer.MAX_VALUE);

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

        java.util.function.Predicate<Object> colorValidator = o -> o instanceof String s && s.matches("^#?([0-9a-fA-F]{6}|[0-9a-fA-F]{8})$");

        clientBuilder.comment("Client Settings").push("overlays");
        SHOW_OVERLAYS_IN_CREATIVE = clientBuilder
                .comment("Display overlays in creative")
                .define("show_overlays_in_creative", false);

        clientBuilder.push("xp_overlay");
        SHOW_XP_OVERLAY = clientBuilder.comment("Display xp gain overlay").define("show_xp_overlay", true);
        XP_OFFSET_X = clientBuilder.comment("X offset").defineInRange("xp_x_offset", 0, -10000, 10000);
        XP_OFFSET_Y = clientBuilder.comment("Y offset").defineInRange("xp_y_offset", 0, -10000, 10000);
        XP_DURATION= clientBuilder.comment("Duration (sec)").defineInRange("xp_duration", 3D, 0.5D, 60D);
        XP_BG_COLOR = clientBuilder.comment("Background color (Hex)").define("xp_background_color", "#FF000000", colorValidator);
        XP_BD_COLOR = clientBuilder.comment("Border color (Hex)").define("xp_border_color", "#FFFFFFFF", colorValidator);
        XP_TEXT_COLOR = clientBuilder.comment("Text color (Hex)").define("xp_text_color", "#FFFFFFFF", colorValidator);
        clientBuilder.pop();

        clientBuilder.push("level_up_overlay");
        SHOW_LEVEL_OVERLAY = clientBuilder.comment("Display Level up overlay").define("show_level_overlay", true);
        LEVEL_OFFSET_X = clientBuilder.comment("X offset").defineInRange("level_x_offset", 0, -10000, 10000);
        LEVEL_OFFSET_Y = clientBuilder.comment("Y offset").defineInRange("level_y_offset", 0, -10000, 10000);
        LEVEL_DURATION= clientBuilder.comment("Duration (sec)").defineInRange("level_duration", 4D, 0.5D, 60D);
        LEVEL_BG_COLOR = clientBuilder.comment("Background color (Hex)").define("level_background_color", "#FF000000", colorValidator);
        LEVEL_BD_COLOR = clientBuilder.comment("Border color (Hex)").define("level_border_color", "#FFD6AD55", colorValidator);
        LEVEL_TEXT_COLOR = clientBuilder.comment("Text color (Hex)").define("level_text_color", "#FFFFFFFF", colorValidator);
        LEVEL_SCD_TEXT_COLOR = clientBuilder.comment("Text color (Hex)").define("level_second_text_color", "#FFD6AD55", colorValidator);
        clientBuilder.pop();

        clientBuilder.push("skill_overlay");
        SHOW_SKILL_OVERLAY = clientBuilder.comment("Display skill activation icons").define("show_skill_overlay", true);
        SKILL_OFFSET_X = clientBuilder.comment("X offset").defineInRange("skill_x_offset", 0, -10000, 10000);
        SKILL_OFFSET_Y = clientBuilder.comment("Y offset").defineInRange("skill_y_offset", 0, -10000, 10000);
        SKILL_DURATION= clientBuilder.comment("Duration (sec)").defineInRange("skill_duration", 2D, 0.5D, 60D);
        SKILL_BG_COLOR = clientBuilder.comment("Background color (Hex)").define("skill_background_color", "#FF000000", colorValidator);
        SKILL_BD_COLOR = clientBuilder.comment("Border color (Hex)").define("skill_border_color", "#FFD6AD55", colorValidator);
        clientBuilder.pop();

        clientBuilder.pop();












        clientBuilder.push("debug");

        SHOW_DEBUG_MESSAGES = clientBuilder
                .comment("Show debug messages")
                .define("show_debug_messages", false);

        clientBuilder.pop();
        clientBuilder.push("accessibility");

        FOV_REDUCTION = clientBuilder
                .comment("Reduces the FOV effect that can be caused by skills (0.0 = Normal Minecraft, 1.0 = No FOV change)")
                .defineInRange("speed_fov_reduction", 0.95, 0, 1);

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
