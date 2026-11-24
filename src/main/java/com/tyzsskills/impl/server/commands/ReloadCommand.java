package com.tyzsskills.impl.server.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tyzsskills.Settings;
import com.tyzsskills.impl.server.debug.Reload;
import com.tyzsskills.impl.server.payloads.ClientReloadPayload;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.neoforge.network.PacketDistributor;

import java.io.IOException;

public class ReloadCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> reload(){
        return Commands.literal("reload")
                .then(Commands.literal("server")
                        .requires(src -> src.hasPermission(Settings.Server.RELOAD_PERM.get().level()))
                        .executes(ctx -> {
                            try {Reload.reload(ctx.getSource().getServer());}
                            catch (IOException e) {throw new RuntimeException(e);}
                            return 1;
                        }))
                .then(Commands.literal("client")
                        .executes(ctx -> {
                            var player = ctx.getSource().getPlayer();
                            if (player == null) return 0;
                            PacketDistributor.sendToPlayer(player, new ClientReloadPayload());
                            return 1;
                        }));

    }
}
