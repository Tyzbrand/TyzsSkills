package com.tyzsskills.impl.client;


import com.tyzsskills.Tyzsskills;
import com.tyzsskills.impl.client.wrappers.ExportManager;
import net.minecraft.commands.Commands;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

@EventBusSubscriber(modid = Tyzsskills.MODID, value = Dist.CLIENT)
public class ClientCommand {

    @SubscribeEvent
    public static void registerClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("export_skill_progression")
                        .executes(ctx -> {
                            ExportManager.saveExport();
                            return 1;
                        })
        );
    }
}
