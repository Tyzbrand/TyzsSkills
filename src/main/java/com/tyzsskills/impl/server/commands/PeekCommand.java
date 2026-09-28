package com.tyzsskills.impl.server.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tyzsskills.api.TyzsSkillsAPI;
import com.tyzsskills.impl.client.tools.StringTools;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;

public class PeekCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> peek(){
        return Commands.literal("peek")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(ctx -> {
                            var player = EntityArgument.getPlayer(ctx, "player");
                            ctx.getSource().sendSuccess(() -> printInfos(player), false);
                            return 1;
                        }));
    }

    private static Component printInfos(ServerPlayer player){
        var root = Component.empty();

        var playerName =  Component.empty().append(player.getDisplayName()).withStyle(ChatFormatting.GOLD);
        var head = Component.empty().append(playerName).append(Component.literal(" infos: "));
        root.append(head).append("\n\n");

        var metaBand = Component.literal("Metadata").withStyle(ChatFormatting.UNDERLINE, ChatFormatting.BLUE);
        root.append(metaBand).append("\n");

        var playerLvl = TyzsSkillsAPI.level().getLevel(player);
        var level = Component.literal(String.format("▶ Lvl: %d.", playerLvl));
        var xp = Component.literal(String.format("▶ Xp: %s/%s.", StringTools.valueSmartFormat(TyzsSkillsAPI.xp().getXp(player)),
                StringTools.valueSmartFormat(TyzsSkillsAPI.level().getXpGoal(playerLvl))));
        var sp = Component.literal(String.format("▶ Skill Points: %d.", TyzsSkillsAPI.sp().getSp(player)));
        root.append(level).append("\n").append(xp).append("\n").append(sp).append("\n\n");

        var skillsBand = Component.literal("Skills").withStyle(ChatFormatting.UNDERLINE, ChatFormatting.BLUE);
        root.append(skillsBand).append("\n");

        var skills = TyzsSkillsAPI.skills().getSkillList();
        var skillCount = 0;
        for(var skill : skills){
            var skillLvl =TyzsSkillsAPI.skills().getSkillLevel(player, skill.getID());
            if(skillLvl <= 0) continue;

            var message = Component.empty()
                    .append(Component.literal("▶ "))
                    .append(Component.translatable(skill.getDisplayName()))
                    .append(Component.literal(String.format(" [%s] ", skill.getID())).withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(String.format("(%d/%d).", skillLvl, skill.getMaximumLevel())));

            root.append(message).append("\n");
            skillCount++;
        }

        if(skillCount == 0) root.append(Component.literal("▶ No skills").withStyle(ChatFormatting.RED));

        return root;
    }
}
