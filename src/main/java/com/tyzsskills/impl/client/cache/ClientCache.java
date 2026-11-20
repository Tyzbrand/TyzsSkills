package com.tyzsskills.impl.client.cache;

import com.tyzsskills.Constants;
import com.tyzsskills.Settings;
import com.tyzsskills.api.events.client.ClientInitSynchronizationEvent;
import com.tyzsskills.api.events.client.ClientUpdateEvent;
import com.tyzsskills.api.model.Context;
import com.tyzsskills.api.model.settings.Setting;
import com.tyzsskills.impl.server.payloads.UpdatePayloads;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;

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

    public final SkillsCache SKILLS = new SkillsCache(this);
    public final SpellsCache SPELLS = new SpellsCache(this);
    public final DataCache METADATA = new DataCache(this);

    private int cacheVersion = 0;
    public void incrVersion(){cacheVersion++;}
    public int getVersion(){return cacheVersion;}


    //SECURITY
    private long lastClickTime = 0L;
    public void registerNewClick(){lastClickTime = System.currentTimeMillis();}
    public boolean isSpamming(){return System.currentTimeMillis() - lastClickTime < Constants.PAYLOAD_COOLDOWN_MS;}


    public class Update {
        public void updateInit(@NotNull UpdatePayloads.InitPayload payload){
            var serverData = payload.serverData(); var playerData = payload.playerData();
            var playerSkillData = payload.playerSkillData(); var playerSpellData = payload.playerSpellData();

            SKILLS.UPDATE.init(playerSkillData, serverData);
            SPELLS.UPDATE.init(playerSpellData, serverData);
            METADATA.UPDATE.init(playerData, payload.levelData());

            for(var setting : payload.serverData().settings()) {
                Setting<?> settingInstance = Settings.getByKey(setting.key(), false);
                if(settingInstance == null) throw new IllegalStateException("Missing setting on client side:"  + setting.key());

                settingInstance.applyValueFrom(setting);
            }

            registerUpdate("Initial Synchronization", true);
        }

        void registerUpdate(String message){registerUpdate(message, false);}
        void registerUpdate(String message, boolean initialSynchronization) {
            cacheVersion++;

            if(!initialSynchronization) NeoForge.EVENT_BUS.post(new ClientUpdateEvent());
            else NeoForge.EVENT_BUS.post(new ClientInitSynchronizationEvent());

            var player = Minecraft.getInstance().player;
            if (player == null || !Settings.Client.SHOW_DEBUG.get()) return;
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
}