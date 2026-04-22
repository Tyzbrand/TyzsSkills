package com.tyzsskills.api.interfaces;

import com.tyzsskills.api.records.SkillPrefab;
import com.tyzsskills.api.model.SkillBehavior;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface used to register your custom mod elements on the server side.
 * This is provided via the {@link com.tyzsskills.api.events.TyzsSkillsCommonSetupEvent} on the mod {@code MOD BUS}.
 */
public interface ITyzsSkillsCommonRegistration {

    /**
     * Registers a custom skill into the mod.
     * This method handles both providing the default fallback JSON data for the disk and registering the associated logic.
     * @param prefab A {@link SkillPrefab} containing all the skill's definitions, including its optional behavior.
     */
    void registerSkill(@NotNull SkillPrefab prefab);

}
