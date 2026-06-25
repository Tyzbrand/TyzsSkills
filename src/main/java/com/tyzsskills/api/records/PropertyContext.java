package com.tyzsskills.api.records;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public record PropertyContext(@NotNull Player player,
                              @NotNull String propertyKey,
                              int propertyLevel,
                              int playerSP) {
}
