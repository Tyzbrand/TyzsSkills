package com.tyzsskills;

import net.neoforged.neoforge.common.ModConfigSpec;



public class Config {

    //==================Common Config==================
    public static final ModConfigSpec COMMON_SPEC;

    public static final String REFUND_SYSTEM_KEY = "refund_system";
    public static final ModConfigSpec.BooleanValue REFUND_SYSTEM;

    public static final String REFUND_PERCENTAGE_KEY = "refund_percentage";
    public static final ModConfigSpec.DoubleValue REFUND_PERCENTAGE;

    public static final ModConfigSpec.BooleanValue PREVENT_PLACED_BLOCK_XP;


    //==================Client Config==================
    public static final ModConfigSpec CLIENT_SPEC;

    public static final ModConfigSpec.BooleanValue SHOW_XP_OVERLAY;
    public static final ModConfigSpec.BooleanValue SHOW_LEVEL_OVERLAY;
    public static final ModConfigSpec.BooleanValue SHOW_SKILL_OVERLAY;
    public static final ModConfigSpec.BooleanValue SHOW_OVERLAYS_IN_CREATIVE;

    static {
        //Construction du COMMON
        ModConfigSpec.Builder commonBuilder = new ModConfigSpec.Builder();

        commonBuilder.comment("General Gameplay Settings").push("general");

        REFUND_SYSTEM = commonBuilder
                .comment("Play with the refund system")
                .define("refund System", true);

        REFUND_PERCENTAGE = commonBuilder
                .comment("Percentage of the initial price refunded")
                .defineInRange("refund percentage", 30.0, 1.0, 100.0);

        PREVENT_PLACED_BLOCK_XP = commonBuilder
                .comment("Prevent manually placed block from providing xp")
                .define("prevent placed block xp", true);

        commonBuilder.pop();
        COMMON_SPEC = commonBuilder.build();



        //Construction du CLIENT
        ModConfigSpec.Builder clientBuilder = new ModConfigSpec.Builder();

        clientBuilder.comment("Visual Settings").push("overlays");

        SHOW_XP_OVERLAY = clientBuilder
                .comment("Display xp gain overlay")
                .define("show_xp_overlay", true);

        SHOW_LEVEL_OVERLAY = clientBuilder
                .comment("Display Level Up overlay")
                .define("show_level_overlay", true);

        SHOW_SKILL_OVERLAY = clientBuilder
                .comment("Display skill activation icons")
                .define("show_skill_overlay", true);

        SHOW_OVERLAYS_IN_CREATIVE = clientBuilder
                .comment("Display overlays in creative")
                .define("show_overlays_in_creative", false);

        clientBuilder.pop();
        CLIENT_SPEC = clientBuilder.build();

    }


}
