package com.tyzsskills.impl.server.commands;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tyzsskills.api.TyzsSkillsAPI;
import com.tyzsskills.impl.client.tools.StringTools;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;

public class XpCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> xp(){
        return Commands.literal("xp")
                .then(Commands.literal("add")
                        .then(Commands.argument("targets", EntityArgument.players())
                            .then(Commands.argument("amount", FloatArgumentType.floatArg(0.1f))
                                        .executes(ctx ->{
                                            var players = EntityArgument.getPlayers(ctx, "targets");
                                            var amount = FloatArgumentType.getFloat(ctx, "amount");
                                            var successCount = 0;

                                            for(var player : players) if (TyzsSkillsAPI.xp().tryAddXp(player, amount)) successCount++;

                                            var finalCount = successCount;
                                            if(successCount == 0) ctx.getSource().sendFailure(Component.literal(String.format("Failed to add xp to %d player(s)", players.size())));
                                            else ctx.getSource().sendSuccess(() -> Component.literal(String.format("%s xp added to %d player(s)", StringTools.format(amount), finalCount)), true);
                                            return finalCount;}))))

                .then(Commands.literal("remove")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("amount", FloatArgumentType.floatArg(0.1f))
                                        .executes(ctx ->{
                                            var players = EntityArgument.getPlayers(ctx, "targets");
                                            var amount = FloatArgumentType.getFloat(ctx, "amount");
                                            var successCount = 0;
                                            var total = 0f;

                                            for(var player : players) {
                                                var toRemove = Math.min(TyzsSkillsAPI.xp().getXp(player), amount);
                                                if(TyzsSkillsAPI.xp().tryRemoveXp(player, toRemove)) {
                                                    total += toRemove;
                                                    successCount++;
                                                }
                                            }

                                            var finalCount = successCount;
                                            var finalTotal = total;
                                            if(successCount == 0) ctx.getSource().sendFailure(Component.literal(String.format("Failed to remove xp from %d player(s)", players.size())));
                                            else ctx.getSource().sendSuccess(() -> Component.literal(String.format("%s xp removed across %d player(s)", StringTools.format(finalTotal), finalCount)), true);
                                            return finalCount;}))))

                .then(Commands.literal("set")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("amount", FloatArgumentType.floatArg(0f))
                                        .executes(ctx ->{
                                            var players = EntityArgument.getPlayers(ctx, "targets");
                                            var amount = FloatArgumentType.getFloat(ctx, "amount");

                                            for(var player : players) TyzsSkillsAPI.xp().setXp(player, amount, false);
                                            ctx.getSource().sendSuccess(() -> Component.literal(String.format("Xp set to %s for %d player(s)", StringTools.format(amount), players.size())), true);
                                            return players.size();}))));
    }
}
