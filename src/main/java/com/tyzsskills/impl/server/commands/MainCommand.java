package com.tyzsskills.impl.server.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class MainCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("skills")
                .then(XpCommand.xp().requires(src -> src.hasPermission(3)))
                .then(LevelCommand.level().requires(src -> src.hasPermission(3)))
                .then(SpCommand.sp().requires(src -> src.hasPermission(3)))
                .then(SkillCommand.skill().requires(src -> src.hasPermission(3)))
                .then(ReloadCommand.reload().requires(src -> src.hasPermission(3)))
                .then(ResetCommand.reset().requires(src -> src.hasPermission(3)))
                .then(PeekCommand.peek().requires(src -> src.hasPermission(3)))
                .then(DebugCommands.debug());
    }
}
