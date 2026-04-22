package com.tyzsskills.impl.client.wrappers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tyzsskills.impl.client.records.ExportData;
import com.tyzsskills.impl.client.ClientCache;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ExportManager {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void saveExport(){

        var mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        Map<String, Integer> purchasedSkills = new HashMap<>();
        for (var id : ClientCache.getPurchasedSkills()){
            purchasedSkills.put(id, ClientCache.getSkillLevel(id));
        }

        var data = new ExportData(
                mc.level.getGameTime(),
                ClientCache.getLvl(),
                ClientCache.getXP(),
                ClientCache.getSP(),
                ClientCache.getTotalXpPerHour(),
                purchasedSkills
        );


        Path exportDir = mc.gameDirectory.toPath().resolve("tyzs_exports");

        try {
            Files.createDirectories(exportDir);

            String fileName = "export_" + System.currentTimeMillis() + ".json";
            Path filePath = exportDir.resolve(fileName);

            Files.writeString(filePath, gson.toJson(data));

            MutableComponent message = Component.literal("§aSuccessful export §e[Open Folder]");
            message.setStyle(message.getStyle().withClickEvent(
                    new ClickEvent(ClickEvent.Action.OPEN_FILE, exportDir.toString())
            ));

            mc.player.displayClientMessage(message, false);

        } catch (IOException ex) {
            mc.player.displayClientMessage(Component.literal("§cErreur d'export: " + ex.getMessage()), false);
        }
    }
}
