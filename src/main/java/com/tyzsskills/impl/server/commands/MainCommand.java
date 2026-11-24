package com.tyzsskills.impl.server.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tyzsskills.Settings;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class MainCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("skills")
                .then(XpCommand.xp().requires(src -> src.hasPermission(Settings.Server.DATA_CHANGES_PERM.get().level())))
                .then(LevelCommand.level().requires(src -> src.hasPermission(Settings.Server.DATA_CHANGES_PERM.get().level())))
                .then(SpCommand.sp().requires(src -> src.hasPermission(Settings.Server.DATA_CHANGES_PERM.get().level())))
                .then(SkillCommand.skill().requires(src -> src.hasPermission(Settings.Server.DATA_CHANGES_PERM.get().level())))
                .then(ReloadCommand.reload())
                .then(ResetCommand.reset().requires(src -> src.hasPermission(Settings.Server.RESET_PERM.get().level())))
                .then(PeekCommand.peek().requires(src -> src.hasPermission(Settings.Server.PEEK_PERM.get().level())))
                .then(DebugCommands.debug());
    }
}
