package com.tyzsskills;

import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue REFUND_SYSTEM = BUILDER
            .comment("Play with the refund system")
            .define("Refund system", true);

    public static final ModConfigSpec.DoubleValue REFUND_PERCENTAGE = BUILDER
            .comment("A magic number")
            .defineInRange("Refund percentage", 30.0, 1.0, 100.0);


    static final ModConfigSpec SPEC = BUILDER.build();
}
