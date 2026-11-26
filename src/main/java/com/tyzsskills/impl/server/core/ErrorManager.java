package com.tyzsskills.impl.server.core;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@ApiStatus.Internal
public class ErrorManager {

    private static final List<MutableComponent> SERVER_ERRORS = new ArrayList<>();
    private static final List<MutableComponent> SERVER_WARNS = new ArrayList<>();

    private static final List<MutableComponent> CLIENT_ERRORS = new ArrayList<>();
    private static final List<MutableComponent> CLIENT_WARNS = new ArrayList<>();

    public static void clear(boolean isClient){
        if(isClient){
            CLIENT_ERRORS.clear();
            CLIENT_WARNS.clear();
        }
        else{
            SERVER_ERRORS.clear();
            SERVER_WARNS.clear();
        }
    }
    public static boolean hasErrorsOrWarns(boolean isClient){
        if(isClient) return !CLIENT_ERRORS.isEmpty() || !CLIENT_WARNS.isEmpty();
        else return !SERVER_ERRORS.isEmpty() || !SERVER_WARNS.isEmpty();
    }

    private static final MutableComponent HEADER = Component.literal("▶ ");

    private static final ChatFormatting WARNING = ChatFormatting.GOLD;
    private static final ChatFormatting ERROR = ChatFormatting.RED;
    private static final ChatFormatting ID = ChatFormatting.BLUE;


    //ACTIVE
    public static void printErrors(Player player, boolean isClient){
        var warns = isClient ? CLIENT_WARNS : SERVER_WARNS;
        var errors = isClient ? CLIENT_ERRORS : SERVER_ERRORS;
        if (warns.isEmpty() && errors.isEmpty()) return;

        var prefix = isClient ? "client" : "server";

        var headerMessage = Component.empty()
                .append((Component.literal("⚠ Tyz's Skills " + prefix + " loaded with ")))
                .append(Component.literal(errors.size() + " issue(s)").withStyle(ERROR))
                .append(Component.literal(" and "))
                .append(Component.literal(warns.size() + " warning(s)").withStyle(WARNING))
                .append(Component.literal(":"));

        player.sendSystemMessage(headerMessage);

        if(!errors.isEmpty()) player.sendSystemMessage(Component.empty());
        for(var error : errors) {
            player.sendSystemMessage(error);
        }

        if(!warns.isEmpty()) player.sendSystemMessage(Component.empty());
        for(var warn : warns){
            player.sendSystemMessage(warn);
        }
    }

    public static void printDynamicError(Player player, @NotNull MutableComponent details){
        player.sendSystemMessage(Component.literal("[TyzsSkills]: ").withStyle(ERROR).append(details.withStyle(ERROR)));
    }

    //REGISTRATION
    public static void registerLoadError(@NotNull String context, @NotNull String detail, boolean isClient){
        var message = HEADER.copy().withStyle(ERROR)
                .append(Component.literal("Error when " + context).withStyle(ERROR))
                .append(Component.literal(": " + detail + ".").withStyle(ChatFormatting.WHITE));

        var errors = isClient ? CLIENT_ERRORS : SERVER_ERRORS;
        errors.add(message);
    }

    public static void registerSkillError(@NotNull String source, @NotNull String error, boolean isClient){
        var message = HEADER.copy().withStyle(ERROR)
                .append(Component.literal("Error in ").withStyle(ERROR))
                .append(Component.literal("[" + source + "]").withStyle(ID))
                .append(Component.literal(": " + error + ".").withStyle(ChatFormatting.WHITE));

        var errors = isClient ? CLIENT_ERRORS : SERVER_ERRORS;
        errors.add(message);
    }

    public static void registerSettingWarn(@NotNull String source, @NotNull String error, boolean isClient){
        var message = HEADER.copy().withStyle(WARNING)
                .append(Component.literal("Warn in setting ").withStyle(WARNING))
                .append(Component.literal("'" + source + "'").withStyle(ID))
                .append(Component.literal(": " + error + ".").withStyle(ChatFormatting.WHITE));

        var warns = isClient ? CLIENT_WARNS : SERVER_WARNS;
        warns.add(message);
    }


    public static void registerSkillWarn(@NotNull String source, @NotNull String warn, boolean isClient){
        var message = HEADER.copy().withStyle(WARNING)
                .append(Component.literal("Warn in ").withStyle(WARNING))
                .append(Component.literal("[" + source + "]").withStyle(ID))
                .append(Component.literal(": " + warn + ".").withStyle(ChatFormatting.WHITE));

        var warns = isClient ? CLIENT_WARNS : SERVER_WARNS;
        warns.add(message);
    }
}
