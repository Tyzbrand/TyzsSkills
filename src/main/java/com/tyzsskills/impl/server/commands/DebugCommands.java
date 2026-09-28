package com.tyzsskills.impl.server.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tyzsskills.impl.server.active.ErrorManager;
import com.tyzsskills.impl.server.payloads.ExportPayload;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class DebugCommands {

    public static LiteralArgumentBuilder<CommandSourceStack> debug(){
        return Commands.literal("debug")
                .then(export())
                .then(errors().requires(src -> src.hasPermission(3)));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> export(){
        return Commands.literal("export")
                .executes(ctx -> {
                    var player = ctx.getSource().getPlayer();
                    if(player == null) {
                        ctx.getSource().sendFailure(Component.literal("Unable to find player."));
                        return 0;
                    }
                    PacketDistributor.sendToPlayer(player, new ExportPayload());
                    return 1;
                });
    }

    private static LiteralArgumentBuilder<CommandSourceStack> errors(){
        return Commands.literal("errors")
                .executes(ctx -> {
                    var player = ctx.getSource().getPlayer();
                    if(player == null) {
                        ctx.getSource().sendFailure(Component.literal("Unable to find player."));
                        return 0;
                    }
                    ErrorManager.printErrors(player);
                    return 1;
                });
    }


}
