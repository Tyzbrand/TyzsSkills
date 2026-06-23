package com.tyzsskills.api.records;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SpellPropertyContext(@NotNull Player player,
                                   @NotNull String propertyKey,
                                   int propertyLevel,
                                   int playerLvl,
                                   int playerSP) {
}
