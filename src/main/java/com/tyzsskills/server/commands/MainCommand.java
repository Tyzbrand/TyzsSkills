package com.tyzsskills.server.commands;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tyzsskills.server.active.DebugManager;
import com.tyzsskills.server.active.LevelManager;
import com.tyzsskills.server.model.Skill;
import com.tyzsskills.server.skills.SkillManager;
import com.tyzsskills.server.active.SpManager;
import com.tyzsskills.server.xp.XpManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;

import java.io.IOException;


public class MainCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("skills")
                .requires(src -> src.hasPermission(3))
                .then(xp())
                .then(level())
                .then(sp())
                .then(skill())
                .then(reload())
                .then(reset());
    }

    private static LiteralArgumentBuilder<CommandSourceStack> xp(){
        return Commands.literal("xp")
                .then(Commands.argument("player", EntityArgument.player())

                        .then(Commands.literal("add")
                                .then(Commands.argument("amount", FloatArgumentType.floatArg(0.1f))
                                        .executes(ctx ->{
                                            var player = EntityArgument.getPlayer(ctx, "player");
                                            var amount = FloatArgumentType.getFloat(ctx, "amount");
                                            XpManager.addXP(player, amount);
                                            return 1;})))

                        .then(Commands.literal("remove")
                                .then(Commands.argument("amount", FloatArgumentType.floatArg(0.1f))
                                        .executes(ctx ->{
                                            var player = EntityArgument.getPlayer(ctx, "player");
                                            var amount = FloatArgumentType.getFloat(ctx, "amount");
                                            XpManager.removeXP(player, amount);
                                            return 1;})))

                        .then(Commands.literal("set")
                                .then(Commands.argument("amount", FloatArgumentType.floatArg(0f))
                                        .executes(ctx ->{
                                            var player = EntityArgument.getPlayer(ctx, "player");
                                            var amount = FloatArgumentType.getFloat(ctx, "amount");
                                            XpManager.setXP(player, amount);
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
                                            LevelManager.addLevel(player, amount);
                                            return 1;})))

                        .then(Commands.literal("remove")
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(ctx ->{
                                            var player = EntityArgument.getPlayer(ctx, "player");
                                            var amount = IntegerArgumentType.getInteger(ctx, "amount");
                                            LevelManager.removeLevel(player, amount);
                                            return 1;})))

                        .then(Commands.literal("set")
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(ctx ->{
                                            var player = EntityArgument.getPlayer(ctx, "player");
                                            var amount = IntegerArgumentType.getInteger(ctx, "amount");
                                            LevelManager.setLevel(player, amount);
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
                                            SpManager.addSP(player, amount);
                                            return 1;})))

                        .then(Commands.literal("remove")
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(ctx ->{
                                            var player = EntityArgument.getPlayer(ctx, "player");
                                            var amount = IntegerArgumentType.getInteger(ctx, "amount");
                                            SpManager.removeSP(player, amount);
                                            return 1;})))

                        .then(Commands.literal("set")
                                .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                        .executes(ctx ->{
                                            var player = EntityArgument.getPlayer(ctx, "player");
                                            var amount = IntegerArgumentType.getInteger(ctx, "amount");
                                            SpManager.setSp(player, amount);
                                            return 1;})))
                );

    }

    private static LiteralArgumentBuilder<CommandSourceStack> skill(){
        return Commands.literal("skill")
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.literal("set")
                                .then(Commands.argument("skill_id", StringArgumentType.string())
                                        .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
                                                SkillManager.Get().getAllSkills().stream().map(Skill::GetID), builder
                                        ))
                                        .then(Commands.argument("level", IntegerArgumentType.integer(0, 10))
                                            .executes(ctx -> {
                                                var player = EntityArgument.getPlayer(ctx, "player");
                                                var id = StringArgumentType.getString(ctx, "skill_id");
                                                var level = IntegerArgumentType.getInteger(ctx, "level");
                                                SkillManager.Get().setSkillLevel(player, id, level);
                                                return 1;}))))
                        .then(Commands.literal("add")
                                .then(Commands.argument("skill_id", StringArgumentType.string())
                                        .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
                                                SkillManager.Get().getAllSkills().stream().map(Skill::GetID), builder
                                        ))
                                        .then(Commands.argument("level", IntegerArgumentType.integer(1, 10))
                                                .executes(ctx -> {
                                                    var player = EntityArgument.getPlayer(ctx, "player");
                                                    var id = StringArgumentType.getString(ctx, "skill_id");
                                                    var level = IntegerArgumentType.getInteger(ctx, "level");
                                                    SkillManager.Get().addSKillLevel(player, id, level);
                                                    return 1;}))))
                        .then(Commands.literal("remove")
                                .then(Commands.argument("skill_id", StringArgumentType.string())
                                        .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
                                                SkillManager.Get().getAllSkills().stream().map(Skill::GetID), builder
                                        ))
                                        .then(Commands.argument("level", IntegerArgumentType.integer(1, 10))
                                                .executes(ctx -> {
                                                    var player = EntityArgument.getPlayer(ctx, "player");
                                                    var id = StringArgumentType.getString(ctx, "skill_id");
                                                    var level = IntegerArgumentType.getInteger(ctx, "level");
                                                    SkillManager.Get().removeSkillLevel(player, id, level);
                                                    return 1;})))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> reload(){
        return Commands.literal("reload")
                .executes(ctx -> {
                    try {
                        DebugManager.DebugReload(ctx.getSource().getServer());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return 1;
                });
    }

    private static LiteralArgumentBuilder<CommandSourceStack> reset(){
        return Commands.literal("reset")
                    .then(Commands.argument("player", EntityArgument.player())
                            .executes(ctx -> {
                                DebugManager.DebugResetData(EntityArgument.getPlayer(ctx, "player"));
                                return 1;
                            }));


    }


}
