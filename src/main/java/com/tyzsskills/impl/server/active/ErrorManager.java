package com.tyzsskills.impl.server.active;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@ApiStatus.Internal
public class ErrorManager {

    private static final List<MutableComponent> ERROR_PRINTS = new ArrayList<>();
    public static void clearErrors(){
        ERROR_PRINTS.clear();
    }
    public static boolean hasErrors(){
        return !ERROR_PRINTS.isEmpty();
    }

    private static final MutableComponent HEADER = Component.literal("▶ ");

    private static final ChatFormatting WARNING = ChatFormatting.GOLD;
    private static final ChatFormatting ERROR = ChatFormatting.RED;
    private static final ChatFormatting ID = ChatFormatting.BLUE;


    //ACTIVE
    public static void printErrors(ServerPlayer player){
        if (ERROR_PRINTS.isEmpty()) return;

        player.sendSystemMessage(Component.literal("§c⚠ Tyz's Skills loaded with " + ERROR_PRINTS.size() + " issue(s):"));
        for(var error : ERROR_PRINTS){
            player.sendSystemMessage(error);
        }
    }

    //REGISTRATION
    public static void registerLoadError(@NotNull String context, @NotNull String detail){
        var message = HEADER.copy().withStyle(ERROR)
                .append(Component.literal("Error when " + context).withStyle(ERROR))
                .append(Component.literal(": " + detail).withStyle(ChatFormatting.WHITE));

        ERROR_PRINTS.add(message);
    }

    public static void registerSkillError(@NotNull String source, @NotNull String error){
        var message = HEADER.copy().withStyle(ERROR)
                .append(Component.literal("Error in ").withStyle(ERROR))
                .append(Component.literal("[" + source + "]").withStyle(ID))
                .append(Component.literal(": " + error).withStyle(ChatFormatting.WHITE));

        ERROR_PRINTS.add(message);
    }


    public static void registerLoadDeprecation(@NotNull String source, @NotNull String field, @NotNull String newFormat){
        var message = HEADER.copy().withStyle(WARNING)
                .append(Component.literal(field + " format is outdated in " ).withStyle(WARNING))
                .append(Component.literal("[" + source + "], ").withStyle(ID))
                .append(Component.literal("use this format: ").withStyle(WARNING))
                .append(Component.literal(newFormat).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.DARK_GRAY));

        ERROR_PRINTS.add(message);
    }



}
