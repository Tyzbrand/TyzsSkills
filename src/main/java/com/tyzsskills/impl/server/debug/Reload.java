package com.tyzsskills.impl.server.debug;

import com.tyzsskills.Settings;
import com.tyzsskills.Tyzsskills;
import com.tyzsskills.api.events.ModReloadEvent;
import com.tyzsskills.impl.server.Level.LevelManager;
import com.tyzsskills.impl.server.active.ErrorManager;
import com.tyzsskills.impl.server.active.FileManager;
import com.tyzsskills.impl.server.payloads.UpdatePayloads;
import com.tyzsskills.impl.server.skills.SkillManager;
import com.tyzsskills.impl.server.spells.SpellManager;
import com.tyzsskills.impl.server.xp.XpGainRegistry;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;

@ApiStatus.Internal
public class Reload {
    public static void reload(MinecraftServer server) throws IOException {
        ErrorManager.clear(false);
        SkillManager.clearSkills();
        SpellManager.clearSpells();
        XpGainRegistry.clearAll();
        LevelManager.clearPool();

        try {
            FileManager.reload(server);
        }
        catch (IOException e) {
            Tyzsskills.LOGGER.error("CRITICAL ERROR: Unable to load files during mod reload", e);
            throw new RuntimeException(e);
        }

        for(var player : server.getPlayerList().getPlayers()) {
            Repair.repair(player);
            PacketDistributor.sendToPlayer(player, UpdatePayloads.getInitPayload(player));

            server.getCommands().sendCommands(player);

            if (player.hasPermissions(Settings.Server.RELOAD_PERM.get().level()) && ErrorManager.hasErrorsOrWarns(false)) ErrorManager.printErrors(player, false);
        }

        NeoForge.EVENT_BUS.post(new ModReloadEvent());
    }
}
