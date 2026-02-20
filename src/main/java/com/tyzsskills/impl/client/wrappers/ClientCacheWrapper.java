package com.tyzsskills.impl.client.wrappers;

import com.tyzsskills.api.interfaces.IClientDataManager;
import com.tyzsskills.impl.client.ClientCache;

import java.util.List;

public class ClientCacheWrapper implements IClientDataManager {

    @Override
    public int getLevel() {
        return ClientCache.GetLvl();
    }

    @Override
    public int getSP() {
        return ClientCache.GetSP();
    }

    @Override
    public float getXP() {
        return ClientCache.GetXP();
    }

    @Override
    public int getPower() {
        return ClientCache.GetPower();
    }

    @Override
    public int getSpReward() {
        return ClientCache.GetReward();
    }

    @Override
    public float getXpGoal() {
        return ClientCache.GetXPGOAL();
    }

    @Override
    public float getAllTimeXP() {
        return ClientCache.GetAllTimeXp();
    }

    @Override
    public float getSessionXP() {
        return ClientCache.GetSessionXp();
    }

    @Override
    public int getSpEarned() {
        return ClientCache.GetSpEarned();
    }

    @Override
    public int getSpSpent() {
        return ClientCache.GetSpSpent();
    }

    @Override
    public int getOwnedSkills() {
        return ClientCache.GetUnlockedSkills();
    }

    @Override
    public List<String> getSkillList() {
        return ClientCache.GetAllSkillIDs();
    }

    @Override
    public List<String> getBookmarks() {
        return ClientCache.GetAllBookmarkedIDs();
    }

    @Override
    public boolean isSkillBookmarked(String id) {
        return ClientCache.isSkillBookmarked(id);
    }

    @Override
    public int getSkillLevel(String id) {
        return ClientCache.GetSkillLevel(id);
    }
}
