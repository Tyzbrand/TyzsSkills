package com.tyzsskills.server.active;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import org.apache.commons.lang3.mutable.Mutable;

import java.util.ArrayList;
import java.util.List;

public class ErrorManager {

    private static final List<MutableComponent> ERROR_PRINTS = new ArrayList<>();

    public static void RegisterLoadError(String context, String detail){
        if(context == null || detail == null) return;

        var message = Component.literal("[TyzSkills] ")
                .withStyle(ChatFormatting.GOLD)
                .append(Component.literal("Error when " + context).withStyle(ChatFormatting.RED))
                .append(Component.literal(": " + detail).withStyle(ChatFormatting.WHITE));

        ERROR_PRINTS.add(message);
    }

    public static void RegisterSkillError(String source, String error){
        if(source == null || error == null) return;

        var message = Component.literal("[TyzSkills] ")
                .withStyle(ChatFormatting.GOLD)
                .append(Component.literal("Error in ").withStyle(ChatFormatting.RED))
                .append(Component.literal("[" + source + "]").withStyle(ChatFormatting.AQUA))
                .append(Component.literal(": " + error).withStyle(ChatFormatting.WHITE));

        ERROR_PRINTS.add(message);
    }

    public static void ClearErrors(){
        ERROR_PRINTS.clear();
    }

    public static boolean HasErrors(){
        return !ERROR_PRINTS.isEmpty();
    }

    public static void PrintErrors(ServerPlayer player){
        if (ERROR_PRINTS.isEmpty()) return;

        player.sendSystemMessage(Component.literal("§c⚠ Tyz's Skills loaded with " + ERROR_PRINTS.size() + " error(s):"));
        for(var error : ERROR_PRINTS){
            player.sendSystemMessage(error);
        }
    }
}
