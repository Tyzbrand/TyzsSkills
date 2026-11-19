package com.tyzsskills.impl.client.cache;

import com.tyzsskills.api.model.settings.Setting;
import com.tyzsskills.impl.server.payloads.UpdatePayloads;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class SettingsCache {
    private final ClientCache CACHE;
    SettingsCache(ClientCache cache){CACHE = cache;}

    public final Update UPDATE = new Update();

    private final Map<String, Setting<?>> serverSettings = new LinkedHashMap<>();
    private final Collection<Setting<?>> serverSettingsView = Collections.unmodifiableCollection(serverSettings.values());

    public class Update{
        void init(UpdatePayloads.ServerSyncData data){
            serverSettings.clear();

            for(var setting : data.settings()) serverSettings.put(setting.meta().key(), setting);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> @NotNull T serverGet(@NotNull Setting<T> setting) {
        var synced = serverSettings.get(setting.meta().key());
        if (setting.getClass().isInstance(synced)) {
            return ((Setting<T>) synced).get();
        }
        return setting.getDefault();
    }

    public @NotNull @UnmodifiableView Collection<Setting<?>> getServerSettings(){return serverSettingsView;}
}
