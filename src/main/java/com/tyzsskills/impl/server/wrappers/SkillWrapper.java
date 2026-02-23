package com.tyzsskills.impl.server.wrappers;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.interfaces.ISkillManager;
import com.tyzsskills.api.records.SkillPrefab;
import com.tyzsskills.impl.server.active.FileManager;
import com.tyzsskills.impl.server.model.SkillBehavior;
import com.tyzsskills.impl.server.payloads.SkillTriggerPayload;
import com.tyzsskills.impl.server.skills.SkillBehaviorRegistry;
import com.tyzsskills.impl.server.skills.SkillManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.repository.Pack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class SkillWrapper implements ISkillManager {

    @Override
    public boolean isSkillLoaded(String id) {
        return SkillManager.Get().isSkillLoaded(id);
    }

    @Override
    public int getSkillLevel(ServerPlayer player, String id) {
        return SkillManager.Get().getPlayerSkillLevel(player, id);
    }

    @Override
    public void addSkillLevel(ServerPlayer player, String id, int amount) {
        SkillManager.Get().addSKillLevel(player, id, amount);
    }

    @Override
    public void removeSkillLevel(ServerPlayer player, String id, int amount) {
        SkillManager.Get().removeSkillLevel(player, id, amount);
    }

    @Override
    public void setSkillLevel(ServerPlayer player, String id, int amount) {
        SkillManager.Get().setSkillLevel(player, id, amount);
    }

    @Override
    public boolean buySkill(ServerPlayer player, String id) {
        return SkillManager.Get().buySkill(player, id);
    }

    @Override
    public boolean refundSkill(ServerPlayer player, String id) {
        return SkillManager.Get().refundSkill(player, id);
    }

    @Override
    public List<ISkill> getSkillList(){
        return SkillManager.Get().getAllSkillsInfos();
    }

    @Override
    public ISkill getSkill(String id) {
        return SkillManager.Get().getSkillInfos(id);
    }

    @Override
    public void triggerSkillBookmark(ServerPlayer player, String id) {
        SkillManager.Get().bookmarkSkill(player, id);
    }

    @Override
    public boolean isSkillBookmarked(ServerPlayer player, String id) {
        return SkillManager.Get().isSkillBookmarked(player, id);
    }

    @Override
    public List<String> getBookmarkedSkillIDs(ServerPlayer player) {
        return SkillManager.Get().getAllBookmarkIDs(player);
    }

    @Override
    public void registerSkillBehavior(String id, SkillBehavior behavior) {
        SkillBehaviorRegistry.registerCustomBehavior(id, behavior);
    }

    @Override
    public void triggerSkillActivationOverlay(ServerPlayer player, String id) {
        if(player == null || id == null) return;
        PacketDistributor.sendToPlayer(player, new SkillTriggerPayload(id));
    }

    @Override
    public void registerSkillPrefab(SkillPrefab prefab) {
        FileManager.registerSkillPrefab(prefab);

    }
}
