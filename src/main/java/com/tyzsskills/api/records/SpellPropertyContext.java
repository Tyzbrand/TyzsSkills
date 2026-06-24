package com.tyzsskills.api.records;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public record SpellPropertyContext(@NotNull Player player,
                                   @NotNull String propertyKey,
                                   int propertyLevel,
                                   int playerSP) {
}
