package com.tyzsskills.impl.client.wrappers;

import com.tyzsskills.api.interfaces.IClientDataManager;
import com.tyzsskills.impl.client.ClientCache;

import java.util.List;

public class ClientCacheWrapper implements IClientDataManager {

    @Override
    public int getLevel() {
        return ClientCache.getLvl();
    }

    @Override
    public int getSP() {
        return ClientCache.getSP();
    }

    @Override
    public float getXP() {
        return ClientCache.getXP();
    }

    @Override
    public int getSpReward() {
        return ClientCache.getReward();
    }

    @Override
    public float getXpGoal() {
        return ClientCache.getXpGoal();
    }

    @Override
    public float getAllTimeXP() {
        return ClientCache.getAllTimeXp();
    }

    @Override
    public float getSessionXP() {
        return ClientCache.getSessionXp();
    }

    @Override
    public int getSpEarned() {
        return ClientCache.getSpEarned();
    }

    @Override
    public int getSpSpent() {
        return ClientCache.getSpSpent();
    }

    @Override
    public int getOwnedSkills() {
        return ClientCache.getUnlockedSkills();
    }

    @Override
    public List<String> getSkillList() {
        return ClientCache.getAllSkillIDs();
    }

    @Override
    public List<String> getBookmarks() {
        return ClientCache.getAllBookmarkedIDs();
    }

    @Override
    public boolean isSkillBookmarked(String id) {
        return ClientCache.isSkillBookmarked(id);
    }

    @Override
    public int getSkillLevel(String id) {
        return ClientCache.getSkillLevel(id);
    }

    @Override
    public List<String> getPurchasedSkills(){
        return ClientCache.getPurchasedSkills();
    }
}
