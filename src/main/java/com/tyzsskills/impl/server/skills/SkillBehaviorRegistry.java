package com.tyzsskills.impl.server.skills;

import com.tyzsskills.impl.server.effects.skillEffects.*;
import com.tyzsskills.api.model.SkillBehavior;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

@ApiStatus.Internal
public class SkillBehaviorRegistry {
    private static final Map<String, SkillBehavior> behaviors = new HashMap<>();

    public static void registerCustomBehavior(String id, SkillBehavior behavior){
        if(id == null || id.isBlank()) return;
        if(behavior == null) return;

        behaviors.put(id.toLowerCase(), behavior);
    }

    public static SkillBehavior getBehavior(String id){
        return behaviors.getOrDefault(id.toLowerCase(), null);
    }
}
