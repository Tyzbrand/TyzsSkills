package com.tyzsskills.api.records;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public record SkillContext(
        @NotNull Player player,
        int skillLvl,
        int playerLvl,
        int playerSP,
        @NotNull Map<String, Integer> ownedSkillIds
) { }
