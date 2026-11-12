package com.tyzsskills.impl.client.cache;

import com.tyzsskills.Config;
import com.tyzsskills.Constants;
import com.tyzsskills.api.events.client.ClientInitSynchronizationEvent;
import com.tyzsskills.api.events.client.ClientUpdateEvent;
import com.tyzsskills.api.model.Context;
import com.tyzsskills.impl.server.payloads.UpdatePayloads;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class ClientCache {

    private ClientCache() {}

    private static ClientCache INSTANCE;
    public static @NotNull ClientCache get() throws IllegalStateException {
        if (INSTANCE == null)
            throw new IllegalStateException("Attempt to Access ClientCache.java While it's not yet Initialized.");
        return INSTANCE;
    }
    public static void deleteCache() {INSTANCE = null;}
    public static void init() {INSTANCE = new ClientCache();}
    public static boolean isReady() {return INSTANCE != null;}

    public final Update UPDATE = new Update();

    public final SkillCache SKILLS = new SkillCache(this);
    public final SpellCache SPELLS = new SpellCache(this);
    public final DataCache METADATA = new DataCache(this);

    private int cacheVersion = 0;
    public void incrVersion(){cacheVersion++;}
    public int getVersion(){return cacheVersion;}


    //SECURITY
    private long lastClickTime = 0L;
    public void registerNewClick(){lastClickTime = System.currentTimeMillis();}
    public boolean isSpamming(){return System.currentTimeMillis() - lastClickTime < Constants.PAYLOAD_COOLDOWN_MS;}

    //SERVER
    private final Map<String, Object> configMap = new HashMap<>();


    public class Update {
        public void updateInit(@NotNull UpdatePayloads.InitPayload payload){
            var serverData = payload.serverData(); var configData = payload.configData();
            var playerSkillData = payload.playerSkillData(); var playerSpellData = payload.playerSpellData();
            var playerData = payload.playerData();

            SKILLS.UPDATE.init(playerSkillData, serverData);
            SPELLS.UPDATE.init(playerSpellData, serverData);
            METADATA.UPDATE.init(playerData, payload.levelData());

           configMap.clear();

            configMap.putAll(configData.booleanMap());
            configMap.putAll(configData.doubleMap());

            registerUpdate("Initial Synchronization", true);
        }

        void registerUpdate(String message){registerUpdate(message, false);}
        void registerUpdate(String message, boolean initialSynchronization) {
            cacheVersion++;

            if(!initialSynchronization) NeoForge.EVENT_BUS.post(new ClientUpdateEvent());
            else NeoForge.EVENT_BUS.post(new ClientInitSynchronizationEvent());

            var player = Minecraft.getInstance().player;
            if (player == null || !Config.SHOW_DEBUG_MESSAGES.get()) return;
            player.displayClientMessage(Component.literal(message), false);
        }
    }

    //UTILS
    private final Context.Player playerContext = new Context.Player();
    public @NotNull Context.Player getPlayerContext() {
        var player = Minecraft.getInstance().player;
        Objects.requireNonNull(player, "Attempt to access PlayerContext with null client.");
        return playerContext.updateContext(player, METADATA.getLevel(), METADATA.getSp(), SKILLS.getOwnedSkills());
    }


    public static int parseColor(String hexString, int fallback) {
        if (hexString == null || hexString.isEmpty()) return fallback;
        try {
            String clean = hexString.replace("#", "");
            if (clean.length() == 6) clean = "FF" + clean;
            return (int) Long.parseLong(clean, 16);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }


    //CONFIG
    public boolean getConfigBool(@NotNull String id, boolean fallback) {
        var value = configMap.get(id);
        if (value instanceof Boolean bool) return bool;
        else return fallback;
    }

    public double getConfigDouble(@NotNull String id, double fallback) {
        var value = configMap.get(id);
        if (value instanceof Double dbl) return dbl;
        else if (value instanceof Number nbr) return nbr.doubleValue();
        else return fallback;
    }
}