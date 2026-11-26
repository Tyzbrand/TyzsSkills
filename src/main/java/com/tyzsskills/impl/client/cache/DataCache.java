package com.tyzsskills.impl.client.cache;

import com.tyzsskills.Settings;
import com.tyzsskills.api.records.LevelData;
import com.tyzsskills.impl.client.overlay.MetadataOverlay;
import com.tyzsskills.impl.server.payloads.UpdatePayloads;
import com.tyzsskills.integration.ui.renderers.UIOverlayRenderer;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

import java.util.TimeZone;

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

            allTimeXp = playerData.totalXp();
            earnedSp = playerData.spEarned();
            spentSp = playerData.spSpent();

            xpQuota = playerData.xpQuota();
            spQuota = playerData.spQuota();
            lastResetBucket = playerData.lastResetBucket();
        }

        public void updateLevel(@NotNull UpdatePayloads.LevelPayload payload) {
            level = payload.level();
            CACHE.UPDATE.registerUpdate("Level Synced: New Cached Level [" + level + "]");
        }

        public void updateSp(@NotNull UpdatePayloads.SpPayload payload) {
            setSp(payload.sp());
            CACHE.UPDATE.registerUpdate("Sp Synced: New Cached Sp Value [" + sp + "]");
        }

        public void updateXp(@NotNull UpdatePayloads.XpPayload payload) {
            if (payload.gained() > 0 && payload.triggersOverlay() && payload.gained() >= Settings.Client.XP_OVERLAY_THRESHOLD.get())
                triggerXpOverlay(payload.gained());

            sessionXp += payload.gained();
            allTimeXp += payload.gained();
            xp = payload.xp();
            CACHE.UPDATE.registerUpdate("Xp Synced: New Cached Xp Value [" + xp + "]");
        }

        public void updateLevelData(@NotNull UpdatePayloads.LevelDataPayload payload) {
            levelData = payload.data();
            CACHE.UPDATE.registerUpdate("Level Data Synced: New Cached Level Data [" + payload.data().goal() + " xp, " + payload.data().reward() + "sp]");
        }

        public void updateLimits(@NotNull UpdatePayloads.LimitsPayload payload){
            spQuota = payload.spQuota();
            xpQuota = payload.xpQuota();
            lastResetBucket = payload.lastResetBucket();
            CACHE.UPDATE.registerUpdate("Limit Quotas Synced");
        }
    }

    private int level;
    public int getLevel() {return level;}

    private int sp;
    public int getSp() {return sp;}
    public void setSp(int amount){
        var diff = amount - sp;
        if (diff < 0) spentSp -= diff;
        else if (diff > 0) earnedSp += diff;
        sp = amount;
    }
    public void addSp(int amount){setSp(sp + amount);}
    public void removeSp(int amount){setSp(sp - amount);}

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

    //LIMITS
    private float xpQuota ;
    public float getXpQuota() {return xpQuota;}

    private int spQuota;
    public int getSpQuota(){return spQuota;}

    private long lastResetBucket;
    public long getLastResetBucket() {return lastResetBucket;}

    //STATS
    private float sessionXp;
    public float getSessionXp() {return sessionXp;}

    private float allTimeXp;
    public float getAllTimeXp() {return allTimeXp;}

    private int earnedSp;
    public int getEarnedSp() {return earnedSp;}

    private int spentSp;
    public int getSpentSp() {return spentSp;}

    int ownedSkills;
    public int getOwnedSkills() {return ownedSkills;}

    int maxSkills;
    public int getMaxSkills(){return maxSkills;}

    public int getOwnedSpells() {return CACHE.SPELLS.getAllPurchasedSpells().size();}

    private MetadataOverlay getOrCreateStack(){
        var overlay = UIOverlayRenderer.get(MetadataOverlay.ID, MetadataOverlay.class);
        if(overlay != null) return overlay;

        overlay = new MetadataOverlay(4 + Settings.Client.META_OFFSET_X.get(), 8 + Settings.Client.META_OFFSET_Y.get());
        UIOverlayRenderer.add(overlay);
        return overlay;

    }
    public void triggerXpOverlay(float gain){
        getOrCreateStack().addXp(gain);
    }
    public void triggerLevelOverlay(int currentLvl, int spGain){
        getOrCreateStack().addLevel(currentLvl, spGain);
    }

    //Quotas
    private long getCurrentTimeSeconds() {
        return switch (Settings.Server.LIMIT_TYPE.get()) {
            case REAL_TIME -> {
                var now = System.currentTimeMillis();
                yield (now + TimeZone.getDefault().getOffset(now)) / 1000L;
            }
            case GAME_TIME -> {
                var level = Minecraft.getInstance().level;
                yield level == null ? -1L : level.getGameTime() / 20L;
            }
        };
    }

    public void syncClientQuotas() {
        var intervalSec = Settings.Server.LIMIT_RESET_INTERVAL.get() * 60L;
        var nowSec = getCurrentTimeSeconds();
        if(nowSec < 0) return;

        var current = intervalSec <= 0 ? 0L : nowSec / intervalSec;
        if(current != lastResetBucket) {
            xpQuota = 0f;
            spQuota = 0;
            lastResetBucket = current;
            CACHE.incrVersion();
        }
    }

    public long getRemainingSeconds() {
        long intervalSec = Settings.Server.LIMIT_RESET_INTERVAL.get() * 60L;
        if(intervalSec <= 0) return -1L;

        var nowSec = getCurrentTimeSeconds();
        if(nowSec < 0) return 0L;

        return intervalSec - Math.floorMod(nowSec, intervalSec);
    }
}
