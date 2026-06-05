package com.tyzsskills.impl.server.skills;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.interfaces.ISkillManager;
import com.tyzsskills.impl.server.payloads.SkillTriggerPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;

public class SkillWrapper implements ISkillManager {

    @Override
    public boolean isSkillLoaded(@NotNull String skillId) {
        return SkillManager.isSkillLoaded(skillId);
    }

    @Override
    public int getSkillLevel(@NotNull ServerPlayer player, @NotNull String skillId) {
        return SkillManager.getPlayerSkillLevel(player, skillId);
    }

    @Override
    public void setSkillLevel(@NotNull ServerPlayer player, @NotNull String skillId, int newLevel) {
        SkillManager.setSkillLevel(player, skillId, newLevel);
    }

    @Override
    public void resetSkillLevels(@NotNull ServerPlayer player) {
        SkillManager.resetSkillLevels(player);
    }

    @Override
    public boolean tryAddSkillLevel(@NotNull ServerPlayer player, @NotNull String skillId, int amount) {
        return SkillManager.tryAddSkillLevel(player, skillId, amount);
    }

    @Override
    public boolean tryRemoveSkillLevel(@NotNull ServerPlayer player, @NotNull String skillId, int amount) {
        return SkillManager.tryRemoveSkillLevel(player, skillId, amount);
    }

    @Override
    public boolean tryBuySkill(@NotNull ServerPlayer player, @NotNull String skillId) {
        return SkillManager.tryBuySkill(player, skillId);
    }

    @Override
    public boolean tryRefundSkill(@NotNull ServerPlayer player, @NotNull String skillId) {
        return SkillManager.tryRefundSkill(player, skillId);
    }

    @Override
    public boolean tryBulkBuy(@NotNull ServerPlayer player, @NotNull String skillId) {
        return SkillManager.tryBulkBuy(player, skillId);
    }

    @Override
    public boolean tryBulkRefund(@NotNull ServerPlayer player, @NotNull String skillId) {
        return SkillManager.tryBulkRefund(player, skillId);
    }

    @Override
    public @NotNull List<ISkill> getSkillList() {return new ArrayList<>(SkillManager.getAllSkills());}

    @Override
    public @Nullable ISkill getSkill(@NotNull String skillId) {
        return SkillManager.getSkill(skillId);
    }

    @Override
    public void triggerSkillBookmark(@NotNull ServerPlayer player, @NotNull String skillID) {
        SkillManager.bookmarkSkill(player, skillID);
    }

    @Override
    public boolean isSkillBookmarked(@NotNull ServerPlayer player, @NotNull String skillID) {
        return SkillManager.isSkillBookmarked(player, skillID);
    }

    @Override
    public @NotNull @Unmodifiable List<String> getBookmarkedSkillIDs(@NotNull ServerPlayer player) {
        return SkillManager.getAllBookmarkIDs(player);
    }

    @Override
    public void triggerSkillActivationOverlay(@NotNull ServerPlayer player, @NotNull String skillId) {
        PacketDistributor.sendToPlayer(player, new SkillTriggerPayload(skillId));
    }
}
