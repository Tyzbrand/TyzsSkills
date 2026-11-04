package com.tyzsskills.impl.client.cache;

import com.tyzsskills.api.records.LevelData;
import com.tyzsskills.impl.client.overlay.XpTriggerOverlay;
import com.tyzsskills.impl.server.payloads.UpdatePayloads;
import org.jetbrains.annotations.NotNull;

public class DataCache {
    private final ClientCache CACHE;
    DataCache(ClientCache cache){CACHE = cache;}

    public final Update UPDATE = new Update();

    public class Update{
        void init(UpdatePayloads.PlayerSyncData playerData, LevelData levelData){
            level = playerData.level();
            sp = playerData.sp();
            xp = playerData.xp();
            DataCache.this.levelData = levelData;
        }

        public void updateLevel(@NotNull UpdatePayloads.LevelPayload payload) {
            level = payload.level();
            CACHE.UPDATE.registerUpdate("Level Synced: New Cached Level [" + level + "]");
        }

        public void updateSp(@NotNull UpdatePayloads.SpPayload payload) {
            sp = payload.sp();
            CACHE.UPDATE.registerUpdate("Sp Synced: New Cached Sp Value [" + sp + "]");
        }

        public void updateXp(@NotNull UpdatePayloads.XpPayload payload) {
            if (payload.gained() > 0) XpTriggerOverlay.AddXp(payload.gained(), payload.triggersOverlay());
            xp = payload.xp();
            CACHE.UPDATE.registerUpdate("Xp Synced: New Cached Xp Value [" + xp + "]");
        }


        public void updateLevelData(@NotNull UpdatePayloads.LevelDataPayload payload) {
            levelData = payload.data();
            CACHE.UPDATE.registerUpdate("Level Data Synced: New Cached Level Data [" + payload.data().goal() + " xp, " + payload.data().reward() + "sp]");
        }
    }



    private int level;
    public int getLevel() {return level;}

    int sp;
    public int getSp() {return sp;}

    private float xp;
    public float getXp() {
        return xp;
    }

    private LevelData levelData;
    public float getXpGoal() {
        return levelData != null ? levelData.goal() : 999999;
    }
    public int getSpReward() {
        return levelData != null ? levelData.reward() : 0;
    }
}
