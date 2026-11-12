package com.tyzsskills.impl.server.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tyzsskills.impl.server.debug.Reload;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.io.IOException;

public class ReloadCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> reload(){
        return Commands.literal("reload")
                .executes(ctx -> {
                    try {
                        Reload.reload(ctx.getSource().getServer());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return 1;
                });
    }
}
