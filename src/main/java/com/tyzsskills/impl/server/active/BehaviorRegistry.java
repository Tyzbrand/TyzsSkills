package com.tyzsskills.impl.server.active;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.api.interfaces.ISpell;
import com.tyzsskills.api.model.SkillBehavior;
import com.tyzsskills.api.model.SpellBehavior;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@ApiStatus.Internal
public class BehaviorRegistry {
    private static final Map<String, SkillBehavior> SKILL_BEHAVIORS = new HashMap<>();

    private static final Map<String, SpellBehavior> SPELL_BEHAVIORS = new HashMap<>();

    //Skills
    public static void registerSkillBehavior(@NotNull String skillId, @NotNull SkillBehavior behavior) {
        SKILL_BEHAVIORS.put(skillId, behavior);
    }

    public static @Nullable SkillBehavior getSkillBehavior(@NotNull ISkill skill) {
        return SKILL_BEHAVIORS.getOrDefault(skill.getId(), null);
    }

    //Spells
    public static void registerSpellBehavior(@NotNull String spellId, @NotNull SpellBehavior behavior) {
        SPELL_BEHAVIORS.put(spellId, behavior);
    }

    public static @Nullable SpellBehavior getSpellBehavior(@NotNull ISpell spell) {
        return SPELL_BEHAVIORS.getOrDefault(spell.getId(), null);
    }
}
