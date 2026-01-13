package com.tyzsskills.server.commands;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tyzsskills.server.active.LevelManager;
import com.tyzsskills.server.skills.SkillManager;
import com.tyzsskills.server.active.SpManager;
import com.tyzsskills.server.xp.XpManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;



public class MainCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("skills")
                .requires(src -> src.hasPermission(4))
                .then(xp())
                .then(level())
                .then(sp());
    }

    private static LiteralArgumentBuilder<CommandSourceStack> xp(){
        return Commands.literal("xp")
                .then(Commands.argument("player", EntityArgument.player())

                        .then(Commands.literal("add")
                                .then(Commands.argument("amount", FloatArgumentType.floatArg(0.1f))
                                        .executes(ctx ->{
                                            var player = EntityArgument.getPlayer(ctx, "player");
                                            var amount = FloatArgumentType.getFloat(ctx, "amount");
                                            XpManager.AddXP(player, amount);
                                            return 1;})))

                        .then(Commands.literal("remove")
                                .then(Commands.argument("amount", FloatArgumentType.floatArg(0.1f))
                                        .executes(ctx ->{
                                            var player = EntityArgument.getPlayer(ctx, "player");
                                            var amount = FloatArgumentType.getFloat(ctx, "amount");
                                            XpManager.RemoveXP(player, amount);
                                            return 1;})))

                        .then(Commands.literal("set")
                                .then(Commands.argument("amount", FloatArgumentType.floatArg(0f))
                                        .executes(ctx ->{
                                            var player = EntityArgument.getPlayer(ctx, "player");
                                            var amount = FloatArgumentType.getFloat(ctx, "amount");
                                            XpManager.SetXP(player, amount);
                                            return 1;})))
                );

    }

    private static LiteralArgumentBuilder<CommandSourceStack> level(){
        return Commands.literal("level")
                .then(Commands.argument("player", EntityArgument.player())

                        .then(Commands.literal("add")
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(ctx ->{
                                            var player = EntityArgument.getPlayer(ctx, "player");
                                            var amount = IntegerArgumentType.getInteger(ctx, "amount");
                                            LevelManager.AddLevel(player, amount);
                                            return 1;})))

                        .then(Commands.literal("remove")
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(ctx ->{
                                            var player = EntityArgument.getPlayer(ctx, "player");
                                            var amount = IntegerArgumentType.getInteger(ctx, "amount");
                                            LevelManager.RemoveLevel(player, amount);
                                            return 1;})))

                        .then(Commands.literal("set")
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(ctx ->{
                                            var player = EntityArgument.getPlayer(ctx, "player");
                                            var amount = IntegerArgumentType.getInteger(ctx, "amount");
                                            LevelManager.SetLevel(player, amount);
                                            return 1;})))
                );

    }

    private static LiteralArgumentBuilder<CommandSourceStack> sp(){
        return Commands.literal("point")
                .then(Commands.argument("player", EntityArgument.player())

                        .then(Commands.literal("add")
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(ctx ->{
                                            var player = EntityArgument.getPlayer(ctx, "player");
                                            var amount = IntegerArgumentType.getInteger(ctx, "amount");
                                            SpManager.AddSP(player, amount);
                                            return 1;})))

                        .then(Commands.literal("remove")
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(ctx ->{
                                            var player = EntityArgument.getPlayer(ctx, "player");
                                            var amount = IntegerArgumentType.getInteger(ctx, "amount");
                                            SpManager.RemoveSP(player, amount);
                                            return 1;})))

                        .then(Commands.literal("set")
                                .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                        .executes(ctx ->{
                                            var player = EntityArgument.getPlayer(ctx, "player");
                                            var amount = IntegerArgumentType.getInteger(ctx, "amount");
                                            SpManager.SetSP(player, amount);
                                            return 1;})))
                );

    }
}
