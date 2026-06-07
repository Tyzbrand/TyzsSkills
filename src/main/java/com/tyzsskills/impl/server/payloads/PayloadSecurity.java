package com.tyzsskills.impl.server.payloads;

import com.tyzsskills.Constants;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PayloadSecurity {
    private static final Map<UUID, Long> COOLDOWNS = new ConcurrentHashMap<>();

    public static boolean isSpamming(@NotNull ServerPlayer player) {
        long currentTime = System.currentTimeMillis();
        long lastAction = COOLDOWNS.getOrDefault(player.getUUID(), 0L);

        if (currentTime - lastAction < Constants.PAYLOAD_COOLDOWN_MS) return true;

        COOLDOWNS.put(player.getUUID(), currentTime);
        return false;
    }
}
