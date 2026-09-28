package com.tyzsskills.impl.server.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tyzsskills.api.Enums;
import com.tyzsskills.impl.server.active.DebugManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;

public class ResetCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> reset(){
        return Commands.literal("reset")
                .then(Commands.literal("all")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(ctx -> {
                                    var players = EntityArgument.getPlayers(ctx, "targets");
                                    for(var player : players)
                                        DebugManager.RESET.reset(player, Enums.ResetType.ALL);
                                    return players.size();
                                })))
                .then(Commands.literal("skills")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(ctx -> {
                                    var players = EntityArgument.getPlayers(ctx, "targets");
                                    for(var player : players)
                                        DebugManager.RESET.reset(player, Enums.ResetType.SKILLS);
                                    return players.size();
                                })))
                .then(Commands.literal("metadata")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(ctx -> {
                                    var players = EntityArgument.getPlayers(ctx, "targets");
                                    for(var player : players)
                                        DebugManager.RESET.reset(player, Enums.ResetType.METADATA);
                                    return players.size();
                                })))
                .then(Commands.literal("stats")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(ctx -> {
                                    var players = EntityArgument.getPlayers(ctx, "targets");
                                    for(var player : players)
                                        DebugManager.RESET.reset(player, Enums.ResetType.STATS);
                                    return players.size();
                                })));
    }
}
