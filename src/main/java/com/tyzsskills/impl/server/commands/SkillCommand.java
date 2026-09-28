package com.tyzsskills.impl.server.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tyzsskills.Constants;
import com.tyzsskills.api.TyzsSkillsAPI;
import com.tyzsskills.api.interfaces.ISkill;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;

public class SkillCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> skill(){
        return Commands.literal("skill")

                .then(Commands.literal("add")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("skillId", StringArgumentType.string())
                                        .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
                                                TyzsSkillsAPI.skills().getSkillList().stream().map(ISkill::getID), builder
                                        ))
                                        .then(Commands.argument("level", IntegerArgumentType.integer(1, Constants.SKILL_MAX_LEVEL))
                                                .executes(ctx -> {
                                                    var players = EntityArgument.getPlayers(ctx, "targets");
                                                    var id = StringArgumentType.getString(ctx, "skillId");
                                                    var level = IntegerArgumentType.getInteger(ctx, "level");
                                                    var successCount = 0;
                                                    var total = 0;

                                                    var skill = TyzsSkillsAPI.skills().getSkill(id);
                                                    if(skill == null) {
                                                        ctx.getSource().sendFailure(Component.literal(String.format("Unable to find a skill with id [%s].", id)));
                                                        return 0;
                                                    }

                                                    for(var player : players) {
                                                        var levelToAdd = Math.min(level, skill.getMaximumLevel() - TyzsSkillsAPI.skills().getSkillLevel(player, id));
                                                        if(levelToAdd > 0 && TyzsSkillsAPI.skills().tryAddSkillLevel(player, id, levelToAdd)) {
                                                            successCount++;
                                                            total += levelToAdd;
                                                        }
                                                    }

                                                    var finalCount = successCount;
                                                    var finalTotal = total;
                                                    if(successCount == 0) ctx.getSource().sendFailure(Component.literal(String.format("Failed to add level(s) to [%s] for %d player(s)", id, players.size())));
                                                    else ctx.getSource().sendSuccess(() -> Component.literal(String.format("%d level(s) added to [%s] across %d player(s)", finalTotal, id, finalCount)), true);
                                                    return finalCount;})))))
                .then(Commands.literal("remove")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("skillId", StringArgumentType.string())
                                        .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
                                                TyzsSkillsAPI.skills().getSkillList().stream().map(ISkill::getID), builder
                                        ))
                                        .then(Commands.argument("level", IntegerArgumentType.integer(1, Constants.SKILL_MAX_LEVEL))
                                                .executes(ctx -> {
                                                    var players = EntityArgument.getPlayers(ctx, "targets");
                                                    var id = StringArgumentType.getString(ctx, "skillId");
                                                    var level = IntegerArgumentType.getInteger(ctx, "level");
                                                    var successCount = 0;
                                                    var total = 0;

                                                    var skill = TyzsSkillsAPI.skills().getSkill(id);
                                                    if(skill == null) {
                                                        ctx.getSource().sendFailure(Component.literal(String.format("Unable to find a skill with id [%s].", id)));
                                                        return 0;
                                                    }
                                                    for(var player : players){
                                                        var levelToRemove = Math.min(level, TyzsSkillsAPI.skills().getSkillLevel(player, id));
                                                        if(levelToRemove > 0 && TyzsSkillsAPI.skills().tryRemoveSkillLevel(player, id, levelToRemove)) {
                                                            successCount++;
                                                            total += levelToRemove;
                                                        }
                                                    }

                                                    var finalCount = successCount;
                                                    var finalTotal = total;
                                                    if(successCount == 0) ctx.getSource().sendFailure(Component.literal(String.format("Failed to remove level(s) of [%s] from %d player(s)", id, players.size())));
                                                    else ctx.getSource().sendSuccess(() -> Component.literal(String.format("%d level(s) removed from [%s] across %d player(s)", finalTotal, id, finalCount)), true);
                                                    return finalCount;})))))
                .then(Commands.literal("set")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("skillId", StringArgumentType.string())
                                        .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
                                                TyzsSkillsAPI.skills().getSkillList().stream().map(ISkill::getID), builder
                                        ))
                                        .then(Commands.argument("level", IntegerArgumentType.integer(0, Constants.SKILL_MAX_LEVEL))
                                                .executes(ctx -> {
                                                    var players = EntityArgument.getPlayers(ctx, "targets");
                                                    var id = StringArgumentType.getString(ctx, "skillId");

                                                    var skill = TyzsSkillsAPI.skills().getSkill(id);
                                                    if(skill == null) {
                                                        ctx.getSource().sendFailure(Component.literal(String.format("Unable to find a skill with id [%s].", id)));
                                                        return 0;
                                                    }

                                                    var level = Math.min(IntegerArgumentType.getInteger(ctx, "level"), skill.getMaximumLevel());

                                                    for(var player : players) {
                                                        TyzsSkillsAPI.skills().setSkillLevel(player, id, level);
                                                    }
                                                    ctx.getSource().sendSuccess(() -> Component.literal(String.format("[%s] level set to %d for %d player(s)", id, level, players.size())), true);
                                                    return players.size();})))));
    }
}
