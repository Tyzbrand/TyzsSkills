package com.tyzsskills.impl.server.wrappers;

import com.tyzsskills.api.interfaces.ITyzsSkillsCommonRegistration;
import com.tyzsskills.api.model.SkillBehavior;
import com.tyzsskills.api.model.SpellBehavior;
import com.tyzsskills.api.records.SkillPrefab;
import com.tyzsskills.api.records.SpellPrefab;
import com.tyzsskills.impl.server.active.FileManager;
import com.tyzsskills.impl.server.active.BehaviorRegistries;
import org.jetbrains.annotations.NotNull;


public class TyzsSkillsCommonRegistrationWrapper implements ITyzsSkillsCommonRegistration {

    @Override
    public void registerSkill(@NotNull SkillPrefab prefab) {
        FileManager.registerSkillPrefab(prefab);
        if(prefab.behavior() instanceof SkillBehavior behavior) BehaviorRegistries.registerSkillBehavior(prefab.id(), behavior);
    }

    @Override
    public void registerSpell(@NotNull SpellPrefab prefab) {
        FileManager.registerSpellPrefab(prefab);
        if(prefab.behavior() instanceof SpellBehavior behavior) BehaviorRegistries.registerSpellBehavior(prefab.id(), behavior);
    }
}
