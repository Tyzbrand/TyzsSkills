package com.tyzsskills.impl.server.wrappers;

import com.tyzsskills.api.interfaces.ITyzsSkillsCommonRegistration;
import com.tyzsskills.api.records.SkillPrefab;
import com.tyzsskills.impl.server.active.FileManager;
import com.tyzsskills.impl.server.skills.SkillDataRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;


public class TyzsSkillsCommonRegistrationWrapper implements ITyzsSkillsCommonRegistration {

    @Override
    public void registerSkill(@NotNull SkillPrefab prefab) {
        FileManager.registerPrefab(prefab);
        if(prefab.behavior() != null) SkillDataRegistry.registerCustomBehavior(prefab.id(), prefab.behavior());
    }

    @Override
    public void registerSkillUnlockCondition(@NotNull String skillID, @NotNull List<Predicate<Player>> conditions) {
        SkillDataRegistry.registerConditions(skillID, conditions);
    }
}
