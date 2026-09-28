package com.tyzsskills.impl.server.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tyzsskills.api.TyzsSkillsAPI;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;

public class SpCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> sp(){
        return Commands.literal("points")
                .then(Commands.literal("add")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(ctx ->{
                                            var players = EntityArgument.getPlayers(ctx, "targets");
                                            var amount = IntegerArgumentType.getInteger(ctx, "amount");
                                            var successCount = 0;

                                            for(var player : players) if (TyzsSkillsAPI.sp().tryAddSp(player, amount)) successCount++;

                                            var finalCount = successCount;
                                            if(successCount == 0) ctx.getSource().sendFailure(Component.literal(String.format("Failed to add point(s) to %d player(s)", players.size())));
                                            else ctx.getSource().sendSuccess(() -> Component.literal(String.format("%d point(s) added to %d player(s)", amount, finalCount)), true);
                                            return finalCount;}))))

                .then(Commands.literal("remove")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(ctx ->{
                                            var players = EntityArgument.getPlayers(ctx, "targets");
                                            var amount = IntegerArgumentType.getInteger(ctx, "amount");
                                            var successCount = 0;
                                            var total = 0;

                                            for(var player : players) {
                                                var toRemove = Math.clamp(TyzsSkillsAPI.sp().getSp(player), 0, amount);
                                                if(toRemove > 0 && TyzsSkillsAPI.sp().tryRemoveSp(player, toRemove)) {
                                                    total += toRemove;
                                                    successCount++;
                                                }
                                            }

                                            var finalCount = successCount;
                                            var finalTotal = total;
                                            if(successCount == 0) ctx.getSource().sendFailure(Component.literal(String.format("Failed to remove point(s) from %d player(s)", players.size())));
                                            else ctx.getSource().sendSuccess(() -> Component.literal(String.format("%d point(s) removed across %d player(s)", finalTotal, finalCount)), true);
                                            return finalCount;}))))

                .then(Commands.literal("set")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                        .executes(ctx ->{
                                            var players = EntityArgument.getPlayers(ctx, "targets");
                                            var amount = IntegerArgumentType.getInteger(ctx, "amount");

                                            for(var player : players) TyzsSkillsAPI.sp().setSp(player, amount);
                                            ctx.getSource().sendSuccess(() -> Component.literal(String.format("Points set to %d for %d player(s)", amount, players.size())), true);
                                            return players.size();}))));
    }
}
