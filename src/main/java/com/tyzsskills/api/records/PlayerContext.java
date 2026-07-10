package com.tyzsskills.api.records;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record PlayerContext(
        @NotNull Player player,
        int playerLvl,
        int playerSP,
        @NotNull Map<String, Integer> ownedSkillIds)
{


}
