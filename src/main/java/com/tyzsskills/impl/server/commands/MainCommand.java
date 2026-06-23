package com.tyzsskills.impl.server.commands;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tyzsskills.Constants;
import com.tyzsskills.api.Enums;
import com.tyzsskills.api.TyzsSkillsAPI;
import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.impl.server.active.DebugManager;
import com.tyzsskills.impl.server.payloads.ExportPayload;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.neoforged.neoforge.network.PacketDistributor;

import java.io.IOException;


public class MainCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("skills")
                .then(xp().requires(src -> src.hasPermission(3)))
                .then(level().requires(src -> src.hasPermission(3)))
                .then(sp().requires(src -> src.hasPermission(3)))
                .then(skill().requires(src -> src.hasPermission(3)))
                .then(reload().requires(src -> src.hasPermission(3)))
                .then(reset().requires(src -> src.hasPermission(3)))
                .then(export());
    }

    private static LiteralArgumentBuilder<CommandSourceStack> xp(){
        return Commands.literal("xp")
                .then(Commands.argument("player", EntityArgument.player())

                        .then(Commands.literal("add")
                                .then(Commands.argument("amount", FloatArgumentType.floatArg(0.1f))
                                        .executes(ctx ->{
                                            var player = EntityArgument.getPlayer(ctx, "player");
                                            var amount = FloatArgumentType.getFloat(ctx, "amount");
                                            TyzsSkillsAPI.xp().tryAddXp(player, amount);
                                            return 1;})))

                        .then(Commands.literal("remove")
                                .then(Commands.argument("amount", FloatArgumentType.floatArg(0.1f))
                                        .executes(ctx ->{
                                            var player = EntityArgument.getPlayer(ctx, "player");
                                            var amount = FloatArgumentType.getFloat(ctx, "amount");
                                            TyzsSkillsAPI.xp().tryRemoveXp(player, amount);
                                            return 1;})))

                        .then(Commands.literal("set")
                                .then(Commands.argument("amount", FloatArgumentType.floatArg(0f))
                                        .executes(ctx ->{
                                            var player = EntityArgument.getPlayer(ctx, "player");
                                            var amount = FloatArgumentType.getFloat(ctx, "amount");
                                            TyzsSkillsAPI.xp().setXp(player, amount, false);
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
                                            TyzsSkillsAPI.level().tryAddLevel(player, amount);
                                            return 1;})))

                        .then(Commands.literal("remove")
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(ctx ->{
                                            var player = EntityArgument.getPlayer(ctx, "player");
                                            var amount = IntegerArgumentType.getInteger(ctx, "amount");
                                            TyzsSkillsAPI.level().tryRemoveLevel(player, amount);
                                            return 1;})))

                        .then(Commands.literal("set")
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(ctx ->{
                                            var player = EntityArgument.getPlayer(ctx, "player");
                                            var amount = IntegerArgumentType.getInteger(ctx, "amount");
                                            TyzsSkillsAPI.level().setLevel(player, amount);
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
                                            TyzsSkillsAPI.sp().tryAddSp(player, amount);
                                            return 1;})))

                        .then(Commands.literal("remove")
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(ctx ->{
                                            var player = EntityArgument.getPlayer(ctx, "player");
                                            var amount = IntegerArgumentType.getInteger(ctx, "amount");
                                            TyzsSkillsAPI.sp().tryRemoveSp(player, amount);
                                            return 1;})))

                        .then(Commands.literal("set")
                                .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                        .executes(ctx ->{
                                            var player = EntityArgument.getPlayer(ctx, "player");
                                            var amount = IntegerArgumentType.getInteger(ctx, "amount");
                                            TyzsSkillsAPI.sp().setSp(player, amount);
                                            return 1;})))
                );

    }

    private static LiteralArgumentBuilder<CommandSourceStack> skill(){
        return Commands.literal("skill")
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.literal("set")
                                .then(Commands.argument("skill_id", StringArgumentType.string())
                                        .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
                                                TyzsSkillsAPI.skills().getSkillList().stream().map(ISkill::getID), builder
                                        ))
                                        .then(Commands.argument("level", IntegerArgumentType.integer(0, Constants.SKILL_MAX_LEVEL))
                                            .executes(ctx -> {
                                                var player = EntityArgument.getPlayer(ctx, "player");
                                                var id = StringArgumentType.getString(ctx, "skill_id");
                                                var skill = TyzsSkillsAPI.skills().getSkill(id);
                                                if(skill == null) return 0;

                                                var level = Math.min(IntegerArgumentType.getInteger(ctx, "level"), skill.getMaximumLevel());
                                                TyzsSkillsAPI.skills().setSkillLevel(player, id, level);
                                                return 1;}))))

                        .then(Commands.literal("add")
                                .then(Commands.argument("skill_id", StringArgumentType.string())
                                        .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
                                                TyzsSkillsAPI.skills().getSkillList().stream().map(ISkill::getID), builder
                                        ))
                                        .then(Commands.argument("level", IntegerArgumentType.integer(1, Constants.SKILL_MAX_LEVEL))
                                                .executes(ctx -> {
                                                    var player = EntityArgument.getPlayer(ctx, "player");
                                                    var id = StringArgumentType.getString(ctx, "skill_id");
                                                    var skill = TyzsSkillsAPI.skills().getSkill(id);
                                                    if(skill == null) return 0;

                                                    var level = Math.min(IntegerArgumentType.getInteger(ctx, "level"),
                                                            skill.getMaximumLevel() - TyzsSkillsAPI.skills().getSkillLevel(player, id));
                                                    TyzsSkillsAPI.skills().tryAddSkillLevel(player, id, level);
                                                    return 1;}))))

                        .then(Commands.literal("remove")
                                .then(Commands.argument("skill_id", StringArgumentType.string())
                                        .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
                                                TyzsSkillsAPI.skills().getSkillList().stream().map(ISkill::getID), builder
                                        ))
                                        .then(Commands.argument("level", IntegerArgumentType.integer(1, Constants.SKILL_MAX_LEVEL))
                                                .executes(ctx -> {
                                                    var player = EntityArgument.getPlayer(ctx, "player");
                                                    var id = StringArgumentType.getString(ctx, "skill_id");
                                                    var level = Math.min(IntegerArgumentType.getInteger(ctx, "level"), TyzsSkillsAPI.skills().getSkillLevel(player, id));
                                                    TyzsSkillsAPI.skills().tryRemoveSkillLevel(player, id, level);
                                                    return 1;})))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> reload(){
        return Commands.literal("reload")
                .executes(ctx -> {
                    try {
                        DebugManager.reload(ctx.getSource().getServer());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return 1;
                });
    }

    private static LiteralArgumentBuilder<CommandSourceStack> reset(){
        return Commands.literal("reset")
                .then(Commands.literal("all")
                    .then(Commands.argument("player", EntityArgument.player())
                            .executes(ctx -> {
                                DebugManager.reset(EntityArgument.getPlayer(ctx, "player"), Enums.ResetType.ALL);
                                return 1;
                            })))
                .then(Commands.literal("skills")
                    .then(Commands.argument("player", EntityArgument.player())
                        .executes(ctx -> {
                            DebugManager.reset(EntityArgument.getPlayer(ctx, "player"), Enums.ResetType.SKILLS);
                            return 1;
                        })))
                .then(Commands.literal("metadata")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(ctx -> {
                            DebugManager.reset(EntityArgument.getPlayer(ctx, "player"), Enums.ResetType.METADATA);
                            return 1;
                        })))
                .then(Commands.literal("stats")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(ctx -> {
                            DebugManager.reset(EntityArgument.getPlayer(ctx, "player"), Enums.ResetType.STATS);
                            return 1;
                        })));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> export(){
        return Commands.literal("export")
                .executes(ctx -> {
                    var player = ctx.getSource().getPlayer();
                    if(player != null) PacketDistributor.sendToPlayer(player, new ExportPayload());
                    return 1;
                });
    }


}
