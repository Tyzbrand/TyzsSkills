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
    public int getSkillLevel(@NotNull ServerPlayer player, @NotNull ISkill skill) {
        return SkillManager.getPlayerSkillLevel(player, skill);
    }

    @Override
    public void setSkillLevel(@NotNull ServerPlayer player, @NotNull ISkill skill, int newLevel) {
        SkillManager.setSkillLevel(player, skill, newLevel);
    }

    @Override
    public void resetSkillLevels(@NotNull ServerPlayer player) {
        SkillManager.resetSkillLevels(player);
    }

    @Override
    public boolean tryAddSkillLevel(@NotNull ServerPlayer player, @NotNull ISkill skill, int amount) {
        return SkillManager.tryAddSkillLevel(player, skill, amount);
    }

    @Override
    public boolean tryRemoveSkillLevel(@NotNull ServerPlayer player, @NotNull ISkill skill, int amount) {
        return SkillManager.tryRemoveSkillLevel(player, skill, amount);
    }

    @Override
    public boolean tryBuySkill(@NotNull ServerPlayer player, @NotNull ISkill skill) {
        return SkillManager.tryBuySkill(player, skill);
    }

    @Override
    public boolean tryRefundSkill(@NotNull ServerPlayer player, @NotNull ISkill skill) {
        return SkillManager.tryRefundSkill(player, skill);
    }

    @Override
    public boolean tryBulkBuy(@NotNull ServerPlayer player, @NotNull ISkill skill) {
        return SkillManager.tryBulkBuy(player, skill);
    }

    @Override
    public boolean tryBulkRefund(@NotNull ServerPlayer player, @NotNull ISkill skill) {
        return SkillManager.tryBulkRefund(player, skill);
    }

    @Override
    public @NotNull List<ISkill> getSkillList() {return new ArrayList<>(SkillManager.getAllSkills());}

    @Override
    public @Nullable ISkill getSkill(@NotNull String skillId) {
        return SkillManager.getSkill(skillId);
    }

    @Override
    public void triggerSkillBookmark(@NotNull ServerPlayer player, @NotNull ISkill skill) {
        SkillManager.bookmarkSkill(player, skill);
    }

    @Override
    public boolean isSkillBookmarked(@NotNull ServerPlayer player, @NotNull ISkill skill) {
        return SkillManager.isSkillBookmarked(player, skill);
    }

    @Override
    public @NotNull @Unmodifiable List<String> getBookmarkedSkillIDs(@NotNull ServerPlayer player) {
        return SkillManager.getAllBookmarkIDs(player);
    }

    @Override
    public void triggerSkillActivationOverlay(@NotNull ServerPlayer player, @NotNull ISkill skill) {
        PacketDistributor.sendToPlayer(player, new SkillTriggerPayload(skill.getId()));
    }
}
