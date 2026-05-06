package com.tyzsskills.api.model;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SkillContext(
        @NotNull Player player,
        int skillLvl,
        int playerLvl,
        int playerSP,
        @NotNull List<String> ownedSkillIds
) { }
