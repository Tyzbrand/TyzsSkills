package com.tyzsskills.api.interfaces;

import com.tyzsskills.api.records.SkillPrefab;
import com.tyzsskills.api.model.SkillBehavior;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface uses to register your mod elements on the server side.
 */
public interface ITyzsSkillsCommonRegistration {

    /**
     * Register your custom skill.
     * @param prefab A {@link SkillPrefab} containing all skill infos.
     */
    void registerSkill(@NotNull SkillPrefab prefab);

}
