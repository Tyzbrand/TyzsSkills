package com.tyzsskills.impl.server.skills;

import com.tyzsskills.api.model.SkillBehavior;
import com.tyzsskills.api.model.SpellBehavior;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ApiStatus.Internal
public class SkillBehaviorRegistry {
    private static final Map<String, SkillBehavior> SKILL_BEHAVIORS = new HashMap<>();

    private static final Map<String, SpellBehavior> SPELL_BEHAVIORS = new HashMap<>();

    //Skills
    public static void registerSkillBehavior(String id, SkillBehavior behavior) {
        if (id == null || id.isBlank() || behavior == null) return;
        SKILL_BEHAVIORS.put(id.toLowerCase(), behavior);
    }

    public static @Nullable SkillBehavior getSkillBehavior(@NotNull String id) {
        return SKILL_BEHAVIORS.getOrDefault(id.toLowerCase(), null);
    }

    //Spells
    public static void registerSpellBehavior(String id, SpellBehavior behavior) {
        if (id == null || id.isBlank() || behavior == null) return;
        SPELL_BEHAVIORS.put(id.toLowerCase(), behavior);
    }

    public static @Nullable SpellBehavior getSpellBehavior(@NotNull String id) {
        return SPELL_BEHAVIORS.getOrDefault(id.toLowerCase(), null);
    }
}
