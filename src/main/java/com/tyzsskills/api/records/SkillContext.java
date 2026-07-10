package com.tyzsskills.api.records;

import com.tyzsskills.api.interfaces.ISkill;
import com.tyzsskills.impl.server.skills.SkillGraph;
import org.jetbrains.annotations.NotNull;

public record SkillContext(
        @NotNull ISkill skill,
        int skillLevel,
        @NotNull SkillGraph graph)
{ }
