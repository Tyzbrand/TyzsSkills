package com.tyzsskills.impl.server.active;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@ApiStatus.Internal
public class ErrorManager {

    private static final List<MutableComponent> ERRORS = new ArrayList<>();
    private static final List<MutableComponent> WARNS = new ArrayList<>();

    public static void clear(){
        ERRORS.clear();
        WARNS.clear();
    }
    public static boolean hasErrorsOrWarns(){
        return !ERRORS.isEmpty() || !WARNS.isEmpty();
    }

    private static final MutableComponent HEADER = Component.literal("▶ ");

    private static final ChatFormatting WARNING = ChatFormatting.GOLD;
    private static final ChatFormatting ERROR = ChatFormatting.RED;
    private static final ChatFormatting ID = ChatFormatting.BLUE;


    //ACTIVE
    public static void printErrors(ServerPlayer player){
        if (ERRORS.isEmpty() && WARNS.isEmpty()) return;

        var headerMessage = Component.empty()
                .append((Component.literal("⚠ Tyz's Skills loaded with ")))
                .append(Component.literal(ERRORS.size() + " issue(s)").withStyle(ERROR))
                .append(Component.literal(" and "))
                .append(Component.literal(WARNS.size() + " warning(s)").withStyle(WARNING))
                .append(Component.literal(":"));

        player.sendSystemMessage(headerMessage);

        if(!ERRORS.isEmpty()) player.sendSystemMessage(Component.empty());
        for(var error : ERRORS) {
            player.sendSystemMessage(error);
        }

        if(!WARNS.isEmpty()) player.sendSystemMessage(Component.empty());
        for(var warn : WARNS){
            player.sendSystemMessage(warn);
        }
    }

    public static void printDynamicError(ServerPlayer player, @NotNull MutableComponent details){
        player.sendSystemMessage(Component.literal("[TyzsSkills]: ").withStyle(ERROR).append(details.withStyle(ERROR)));
    }

    //REGISTRATION
    public static void registerLoadError(@NotNull String context, @NotNull String detail){
        var message = HEADER.copy().withStyle(ERROR)
                .append(Component.literal("Error when " + context).withStyle(ERROR))
                .append(Component.literal(": " + detail).withStyle(ChatFormatting.WHITE));

        ERRORS.add(message);
    }

    public static void registerSkillError(@NotNull String source, @NotNull String error){
        var message = HEADER.copy().withStyle(ERROR)
                .append(Component.literal("Error in ").withStyle(ERROR))
                .append(Component.literal("[" + source + "]").withStyle(ID))
                .append(Component.literal(": " + error).withStyle(ChatFormatting.WHITE));

        ERRORS.add(message);
    }


    public static void registerLoadDeprecationModification(@NotNull String source, @NotNull String field, @NotNull String newFormat){
        var message = HEADER.copy().withStyle(WARNING)
                .append(Component.literal(field + " format is outdated in " ).withStyle(WARNING))
                .append(Component.literal("[" + source + "], ").withStyle(ID))
                .append(Component.literal("use this format: ").withStyle(WARNING))
                .append(Component.literal(newFormat + ".").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.DARK_GRAY));

        WARNS.add(message);
    }

    public static void registerLoadDeprecationRemoval(@NotNull String source, @NotNull String field){
        var message = HEADER.copy().withStyle(WARNING)
                .append(Component.literal(field + " is obsolete in " ).withStyle(WARNING))
                .append(Component.literal("[" + source + "].").withStyle(ID));

        WARNS.add(message);
    }



}
