package com.tyzsskills.api.interfaces;

import com.tyzsskills.api.records.SkillPrefab;
import com.tyzsskills.api.model.SkillBehavior;
import com.tyzsskills.api.records.SpellPrefab;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.core.jmx.Server;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

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

    void registerSpell(@NotNull SpellPrefab prefab);


}
