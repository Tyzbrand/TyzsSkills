package com.tyzsskills;

import com.tyzsskills.api.Enums;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;


public class Config {

    //==================Common Config==================
    public static final ModConfigSpec COMMON_SPEC;

    public static final String REFUND_SYSTEM_KEY = "refund_system";
    public static final ModConfigSpec.BooleanValue REFUND_SYSTEM;

    public static final String REFUND_PERCENTAGE_KEY = "refund_percentage";
    public static final ModConfigSpec.DoubleValue REFUND_PERCENTAGE;

    public static final ModConfigSpec.BooleanValue PREVENT_PLACED_BLOCK_XP;


    //Limits
    public static final ModConfigSpec.IntValue MAX_LEVEL;
    public static final ModConfigSpec.IntValue MAX_SP;
    public static final ModConfigSpec.IntValue MAX_SP_GAIN;

    public static final String MAX_XP_KEY = "max_xp";
    public static final ModConfigSpec.DoubleValue XP_LIMIT;


    public static final ModConfigSpec.LongValue CYCLE_DURATION;
    public static final ModConfigSpec.EnumValue<Enums.LimitType> LIMIT_TYPE;

    //Death penalties
    public static final ModConfigSpec.BooleanValue DEATH_PENALTIES;
    public static final ModConfigSpec.DoubleValue XP_LOSS;
    public static final ModConfigSpec.IntValue LVL_LOSS;
    public static final ModConfigSpec.DoubleValue SP_LOSS;
    public static final ModConfigSpec.DoubleValue SKILL_LOSS;


    //Xp values
    public static final ModConfigSpec.BooleanValue EARN_XP_IN_CREATIVE;

    public static final ModConfigSpec.BooleanValue EARN_XP_BY_EATING;
    public static final ModConfigSpec.BooleanValue EARN_XP_BY_KILLING;
    public static final ModConfigSpec.BooleanValue EARN_XP_BY_MINING;
    public static final ModConfigSpec.DoubleValue FISHING_XP_VALUE;
    public static final ModConfigSpec.DoubleValue SLEEPING_XP_VALUE;
    public static final ModConfigSpec.DoubleValue CRAFTING_XP_VALUE;
    public static final ModConfigSpec.DoubleValue BREEDING_XP_VALUE;
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

    //Button
    public static final ModConfigSpec.BooleanValue SHOW_SKILL_BUTTON;
    public static final ModConfigSpec.IntValue INVENTORY_BUTTON_X;
    public static final ModConfigSpec.IntValue INVENTORY_BUTTON_Y;

    //Debug
    public static final ModConfigSpec.BooleanValue SHOW_DEBUG_MESSAGES;

    //Accessibility
    public static final ModConfigSpec.DoubleValue FOV_REDUCTION;

    static {
        //Construction du COMMON
        ModConfigSpec.Builder commonBuilder = new ModConfigSpec.Builder();

        commonBuilder.comment("General Gameplay Settings").push("general");

        REFUND_SYSTEM = commonBuilder
                .comment("Enable or disable the skill refund system")
                .translation("config.common.tyzs_skills.refund_system")
                .define("refund_system", true);

        REFUND_PERCENTAGE = commonBuilder
                .comment("Percentage of the initial skill point cost refunded")
                .translation("config.common.tyzs_skills.refund_system_per")
                .defineInRange("refund_percentage", 30.0, 1.0, 100.0);


        PREVENT_PLACED_BLOCK_XP = commonBuilder
                .comment("Prevent manually placed block from providing xp")
                .translation("config.common.tyzs_skills.prevent_placed_blocks_xp")
                .define("prevent_placed_block_xp", true);


        commonBuilder.pop();
        commonBuilder.push("Limits");

        MAX_LEVEL = commonBuilder
                .comment("Maximum level a player can reach (-1 to disable limit)")
                .translation("config.common.tyzs_skills.max_lvl")
                .defineInRange("max_lvl", -1, -1, Integer.MAX_VALUE);

        MAX_SP = commonBuilder
                .comment("Maximum SP a player can hold (-1 to disable limit)")
                .translation("config.common.tyzs_skills.max_sp")
                .defineInRange("max_sp", -1, -1, Integer.MAX_VALUE);

        MAX_SP_GAIN = commonBuilder
                .comment("Maximum SP a player can earn (-1 to disable limit)")
                .translation("config.common.tyzs_skills.max_sp_gain")
                .defineInRange("max_sp_gain", -1, -1, Integer.MAX_VALUE);

        XP_LIMIT = commonBuilder
                .comment("Maximum XP a player can earn within a defined period of time (-1 to disable limit)")
                .translation("config.common.tyzs_skills.xp_daily")
                .defineInRange("xp_daily", -1, -1, Double.MAX_VALUE);

        CYCLE_DURATION = commonBuilder
                .comment("Time that must elapse before the XP limit reset (in sec)")
                .translation("config.common.tyzs_skills.cycle_duration")
                .defineInRange("cycle_duration", 1200, 1, Long.MAX_VALUE);

        LIMIT_TYPE = commonBuilder
                .comment("Change de limit type (FIXED = literal limit of xp, PERCENTAGE = percentage of the xp goal for the current level)")
                .translation("config.common.tyzs_skills.limit_type")
                .defineEnum("limit_type", Enums.LimitType.FIXED);



        commonBuilder.pop();

        commonBuilder.push("Death_penalties");

        DEATH_PENALTIES = commonBuilder
                .comment("Apply death penalties")
                .translation("config.common.tyzs_skills.death_penatlies")
                .define("death_penalties", false);

        XP_LOSS = commonBuilder
                .comment("Percentage of XP deducted")
                .translation("config.common.tyzs_skills.xp_loss")
                .defineInRange("xp_loss", 0.0, 0.0, 100.0);

        SP_LOSS = commonBuilder
                .comment("Percentage of SP deducted")
                .translation("config.common.tyzs_skills.sp_loss")
                .defineInRange("sp_loss", 0.0, 0.0, 100.0);

        LVL_LOSS = commonBuilder
                .comment("Number of LVL deducted")
                .translation("config.common.tyzs_skills.lvl_loss")
                .defineInRange("lvl_loss", 0, 0, Integer.MAX_VALUE);

        SKILL_LOSS = commonBuilder
                .comment("Percentage of chance to lose one level on each skill")
                .translation("config.common.tyzs_skills.skill_loss")
                .defineInRange("skill_loss", 0.0, 0.0, 100.0);

        commonBuilder.pop();
        commonBuilder.push("xp_values");

        EARN_XP_IN_CREATIVE = commonBuilder
                .comment("Enable XP gain while in Creative Mode")
                .translation("config.common.tyzs_skills.earn_creative_xp")
                .define("earn_xp_in_creative", false);

        EARN_XP_BY_EATING = commonBuilder
                .comment("Enable XP gain from eating food (values defined in JSON)")
                .translation("config.common.tyzs_skills.eating_xp_earnings")
                .define("eating_xp_earnings", true);

        EARN_XP_BY_KILLING = commonBuilder
                .comment("Enable XP gain from killing entities (values defined in JSON)")
                .translation("config.common.tyzs_skills.killing_xp_earnings")
                .define("killing_xp_earnings", true);

        EARN_XP_BY_MINING = commonBuilder
                .comment("Enable XP gain from breaking blocks (values defined in JSON)")
                .translation("config.common.tyzs_skills.mining_xp_earnings")
                .define("mining_xp_earnings", true);

        SLEEPING_XP_VALUE = commonBuilder
                .comment("XP gained from sleeping")
                .translation("config.common.tyzs_skills.sleeping_gain")
                .defineInRange("sleeping_gain", 25.0, .0, Double.MAX_VALUE);

        FISHING_XP_VALUE = commonBuilder
                .comment("XP gained from fishing")
                .translation("config.common.tyzs_skills.fishing_gain")
                .defineInRange("fishing_gain", 7.0, .0, Double.MAX_VALUE);

        CRAFTING_XP_VALUE = commonBuilder
                .comment("XP gained from crafting items")
                .translation("config.common.tyzs_skills.crafting_gain")
                .defineInRange("crafting_gain", .2, .0, Double.MAX_VALUE);

        BREEDING_XP_VALUE = commonBuilder
                .comment("XP gained from breeding animals")
                .translation("config.common.tyzs_skills.breeding_gain")
                .defineInRange("breeding_gain", 15.0, .0, Double.MAX_VALUE);


        ADVANCEMENT_TASK_XP_VALUE = commonBuilder
                .comment("XP gain for basic advancements (Tasks)")
                .translation("config.common.tyzs_skills.advancement_task_gain")
                .defineInRange("task_gain", 25.0, .0, Double.MAX_VALUE);

        ADVANCEMENT_GOAL_XP_VALUE = commonBuilder
                .comment("XP gain for rare advancements (Goals)")
                .translation("config.common.tyzs_skills.advancement_goal_gain")
                .defineInRange("goal_gain", 100.0, .0, Double.MAX_VALUE);

        ADVANCEMENT_CHALLENGE_XP_VALUE = commonBuilder
                .comment("XP gain for epic advancements (Challenges)")
                .translation("config.common.tyzs_skills.advancement_challenge_gain")
                .defineInRange("challenge_gain", 250.0, .0, Double.MAX_VALUE);

        NEW_BIOME_XP_VALUE = commonBuilder
                .comment("XP gain for discovering a new biome")
                .translation("config.common.tyzs_skills.biome_gain")
                .defineInRange("biome_gain", 45.0, .0, Double.MAX_VALUE);

        NEW_DIMENSION_XP_VALUE = commonBuilder
                .comment("XP gain for entering a new dimension")
                .translation("config.common.tyzs_skills.dimension_gain")
                .defineInRange("dimension_gain", 500.0, .0, Double.MAX_VALUE);

        commonBuilder.pop();
        COMMON_SPEC = commonBuilder.build();





        //Construction du CLIENT
        ModConfigSpec.Builder clientBuilder = new ModConfigSpec.Builder();

        java.util.function.Predicate<Object> colorValidator = o -> o instanceof String s && s.matches("^#?([0-9a-fA-F]{6}|[0-9a-fA-F]{8})$");

        clientBuilder.comment("Client Settings").push("overlays");
        SHOW_OVERLAYS_IN_CREATIVE = clientBuilder
                .comment("Display HUD overlays while in Creative Mode")
                .translation("config.client.tyzs_skills.creative_overlays")
                .define("show_overlays_in_creative", false);

        clientBuilder.push("xp_overlay");
        SHOW_XP_OVERLAY = clientBuilder.comment("Display xp gain overlay").translation("config.client.tyzs_skills.xp_overlay").define("show_xp_overlay", true);
        XP_OFFSET_X = clientBuilder.comment("X offset").translation("config.client.tyzs_skills.x_offset").defineInRange("xp_x_offset", 0, -10000, 10000);
        XP_OFFSET_Y = clientBuilder.comment("Y offset").translation("config.client.tyzs_skills.y_offset").defineInRange("xp_y_offset", 0, -10000, 10000);
        XP_DURATION= clientBuilder.comment("Duration (sec)").translation("config.client.tyzs_skills.duration").defineInRange("xp_duration", 3D, 0.5D, 60D);
        XP_BG_COLOR = clientBuilder.comment("Background color (Hex)").translation("config.client.tyzs_skills.bg_color").define("xp_background_color", "#FF000000", colorValidator);
        XP_BD_COLOR = clientBuilder.comment("Border color (Hex)").translation("config.client.tyzs_skills.bd_color").define("xp_border_color", "#FFFFFFFF", colorValidator);
        XP_TEXT_COLOR = clientBuilder.comment("Text color (Hex)").translation("config.client.tyzs_skills.text_color").define("xp_text_color", "#FFFFFFFF", colorValidator);
        clientBuilder.pop();

        clientBuilder.push("level_up_overlay");
        SHOW_LEVEL_OVERLAY = clientBuilder.comment("Display Level up overlay").translation("config.client.tyzs_skills.level_overlay").define("show_level_overlay", true);
        LEVEL_OFFSET_X = clientBuilder.comment("X offset").translation("config.client.tyzs_skills.x_offset").defineInRange("level_x_offset", 0, -10000, 10000);
        LEVEL_OFFSET_Y = clientBuilder.comment("Y offset").translation("config.client.tyzs_skills.y_offset").defineInRange("level_y_offset", 0, -10000, 10000);
        LEVEL_DURATION= clientBuilder.comment("Duration (sec)").translation("config.client.tyzs_skills.duration").defineInRange("level_duration", 4D, 0.5D, 60D);
        LEVEL_BG_COLOR = clientBuilder.comment("Background color (Hex)").translation("config.client.tyzs_skills.bg_color").define("level_background_color", "#FF000000", colorValidator);
        LEVEL_BD_COLOR = clientBuilder.comment("Border color (Hex)").translation("config.client.tyzs_skills.bd_color").define("level_border_color", "#FFD6AD55", colorValidator);
        LEVEL_TEXT_COLOR = clientBuilder.comment("Text color (Hex)").translation("config.client.tyzs_skills.text_color").define("level_text_color", "#FFFFFFFF", colorValidator);
        LEVEL_SCD_TEXT_COLOR = clientBuilder.comment("Secondary text color (Hex)").translation("config.client.tyzs_skills.scd_text_color").define("level_second_text_color", "#FFD6AD55", colorValidator);
        clientBuilder.pop();

        clientBuilder.push("skill_overlay");
        SHOW_SKILL_OVERLAY = clientBuilder.comment("Display skill activation icons").translation("config.client.tyzs_skills.skill_overlay").define("show_skill_overlay", true);
        SKILL_OFFSET_X = clientBuilder.comment("X offset").translation("config.client.tyzs_skills.x_offset").defineInRange("skill_x_offset", 0, -10000, 10000);
        SKILL_OFFSET_Y = clientBuilder.comment("Y offset").translation("config.client.tyzs_skills.y_offset").defineInRange("skill_y_offset", 0, -10000, 10000);
        SKILL_DURATION= clientBuilder.comment("Duration (sec)").translation("config.client.tyzs_skills.duration").defineInRange("skill_duration", 2D, 0.5D, 60D);
        SKILL_BG_COLOR = clientBuilder.comment("Background color (Hex)").translation("config.client.tyzs_skills.bg_color").define("skill_background_color", "#FF000000", colorValidator);
        SKILL_BD_COLOR = clientBuilder.comment("Border color (Hex)").translation("config.client.tyzs_skills.bd_color").define("skill_border_color", "#FFD6AD55", colorValidator);
        clientBuilder.pop();
        clientBuilder.pop();

        clientBuilder.push("inventory_button");
        SHOW_SKILL_BUTTON = clientBuilder
                .comment("Display skill button in the inventory")
                .translation("config.client.tyzs_skills.inventory_btn_show")
                .define("skill_btn", true);

        INVENTORY_BUTTON_X = clientBuilder.comment("X Position of the skill inventory button")
                .translation("config.client.tyzs_skills.inventory_btn_x")
                .defineInRange("inventory_btn_x", 3, -10000, 10000);

        INVENTORY_BUTTON_Y = clientBuilder.comment("Y Position of the skill inventory button")
                .translation("config.client.tyzs_skills.inventory_btn_y")
                .defineInRange("inventory_btn_y", 3, -10000, 10000);
        clientBuilder.pop();

        clientBuilder.push("debug");
        SHOW_DEBUG_MESSAGES = clientBuilder
                .comment("Enable debug messages in chat")
                .translation("config.client.tyzs_skills.debug_msg")
                .define("show_debug_messages", false);
        clientBuilder.pop();

        clientBuilder.push("accessibility");
        FOV_REDUCTION = clientBuilder
                .comment("Reduces FOV changes caused by skills (0.0 = Normal Minecraft, 1.0 = No FOV change)")
                .translation("config.client.tyzs_skills.speed_fov_reduction")
                .defineInRange("speed_fov_reduction", 0.95, 0, 1);

        clientBuilder.pop();
        CLIENT_SPEC = clientBuilder.build();
    }


}
