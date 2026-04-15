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
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class SkillWrapper implements ISkillManager {

    @Override
    public boolean isSkillLoaded(String id) {
        return SkillManager.get().isSkillLoaded(id);
    }

    @Override
    public int getSkillLevel(ServerPlayer player, String id) {
        return SkillManager.get().getPlayerSkillLevel(player, id);
    }

    @Override
    public void addSkillLevel(ServerPlayer player, String id, int amount) {
        SkillManager.get().addSKillLevel(player, id, amount);
    }

    @Override
    public void removeSkillLevel(ServerPlayer player, String id, int amount) {
        SkillManager.get().removeSkillLevel(player, id, amount);
    }

    @Override
    public void setSkillLevel(ServerPlayer player, String id, int amount) {
        SkillManager.get().setSkillLevel(player, id, amount);
    }

    @Override
    public boolean tryBuySkill(ServerPlayer player, String id) {
        return SkillManager.get().tryBuySkill(player, id);
    }

    @Override
    public boolean tryRefundSkill(ServerPlayer player, String id) {
        return SkillManager.get().tryRefundSkill(player, id);
    }

    @Override
    public List<ISkill> getSkillList(){
        return SkillManager.get().getAllSkillsInfos();
    }

    @Override
    public ISkill getSkill(String id) {
        return SkillManager.get().getSkillInfos(id);
    }

    @Override
    public void triggerSkillBookmark(ServerPlayer player, String id) {
        SkillManager.get().bookmarkSkill(player, id);
    }

    @Override
    public boolean isSkillBookmarked(ServerPlayer player, String id) {
        return SkillManager.get().isSkillBookmarked(player, id);
    }

    @Override
    public List<String> getBookmarkedSkillIDs(ServerPlayer player) {
        return SkillManager.get().getAllBookmarkIDs(player);
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
