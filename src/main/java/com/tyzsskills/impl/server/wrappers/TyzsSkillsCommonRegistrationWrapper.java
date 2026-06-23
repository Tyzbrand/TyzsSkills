package com.tyzsskills.impl.server.wrappers;

import com.tyzsskills.api.interfaces.ITyzsSkillsCommonRegistration;
import com.tyzsskills.api.model.SkillBehavior;
import com.tyzsskills.api.records.SkillPrefab;
import com.tyzsskills.impl.server.active.FileManager;
import com.tyzsskills.impl.server.skills.SkillDataRegistry;
import org.jetbrains.annotations.NotNull;


public class TyzsSkillsCommonRegistrationWrapper implements ITyzsSkillsCommonRegistration {

    @Override
    public void registerSkill(@NotNull SkillPrefab prefab) {
        FileManager.registerPrefab(prefab);
        if(prefab.behavior() instanceof SkillBehavior behavior) SkillDataRegistry.registerCustomBehavior(prefab.id(), behavior);
    }

}
